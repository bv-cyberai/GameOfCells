package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.List;

import static com.badlogic.gdx.math.MathUtils.lerpAngleDeg;
import static java.lang.Math.abs;

/**
 * Cell Class
 * <p>
 * Includes the data for the primary cell the player
 * Controls in the game
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class Cell {
    /**
     * Time between applications of zero-ATP damage, in seconds.
     */
    private static final int ZERO_ATP_DAMAGE_INCREMENT_SECONDS = 1;
    // May change, but used to ensure that invalid case selection is not hit.
    private static final int MAX_SIZE_UPGRADES = 4;
    private static final int MAX_ORGANELLE_UPGRADES = 4;
    private static final float VELOCITY_SMOOTHING = 0.1f; // Adjust if you want faster/slower smoothing
    private static final float DEATH_ANIMATION_DURATION = 2.5f; // Duration of the death animation
    private static final float FORCE_CIRCLE_SIZE_MULTIPLIER = 1.375f; // Used on forceCircle to scale up as the cell grows
    public static int MAX_HEALTH = 100;
    public static int MAX_ATP = 100;
    public static int ZERO_ATP_DAMAGE_PER_SECOND = 10;
    public static int ATP_HEAL_COST = 5;
    public static int AMOUNT_HEALED = 5;
    public static int MEMBRANE_DAMAGE_REDUCTION = 1;
    private static float CELL_SPEED = 200f; // Speed of the cell
    private static float CELL_SPEED_WITH_FLAGELLA = 370f; //Amount to increase cell speed after buying flagella
    private static float ROTATION_SPEED = 20f; //How quickly the cell rotates
    private final AssetManager assetManager;
    private final ConfigProvider configProvider;
    private final GamePlayScreen gamePlayScreen;
    private Vector2 smoothedVelocity = new Vector2();
    private Vector2 previousPosition = new Vector2(-500, -500); //previous position of the cell
    private Circle cellCircle;
    // Values used to push glucose
    private Circle forceCircle; // The circle surrounding the cell pushing glucose.
    private Array<Vector2> flagellumVectors = new Array<>(); //sine wave vectors
    private float cellSize = 100;
    private int cellHealth;
    private int cellATP;
    // Organelle Upgrades
    private boolean hasMitochondria = false; // Whether the cell has the mitochondria upgrade
    private boolean hasRibosomes = false; // Whether the cell has the ribosomes upgrade
    private boolean hasFlagella = false; // Whether the cell has the flagella upgrade
    private boolean hasNucleus = false; // Whether the cell has the nucleus upgrade
    // Size Upgrades
    private boolean hasSmallSizeUpgrade = false; // Whether the cell has the small size upgrade
    private boolean hasMediumSizeUpgrade = false; // Whether the cell has the medium size upgrade
    private boolean hasLargeSizeUpgrade = false; // Whether the cell has the large size upgrade
    private boolean hasMassiveSizeUpgrade = false; // Whether the cell has the massive size upgrade
    // Add rotation and animation fields here if needed
    private float nucleusPulse = 0.0f; // Pulse effect for nucleus animation
    private float pulseScale = 1.0f; // Scale for nucleus pulse effect
    private float forceCircleSizeScalar = 1f;
    private float glucoseVectorScaleFactor = 50f; //Used to set how far glucose moves when pushed
    private float cellRotation = 0f; // The cells starting angle, tracks current angle of the cell.
    private float previousRotation = 0f;
    //flagellum
    private float amplitude = 50f;
    private float frequency = 0.05f;
    private float flagTime = 0f; // Phase offset of the sine wave.
    private int wiggleVelocityMultiplier = 5; //How quickly to wiggle
    private float flagellumThickness = 9.375f;
    private int flagellumLength = 225;
    private boolean notUpgradeRenderCycle = true; // tracks size upgrade to avoid a visual flagellum bug
    /**
     * Times how long the cell has been taking zero-ATP damage.
     * Used to group damage, instead of applying a tiny amount each tick.
     */
    private float damageTimer = 0f;
    /**
     * Zero-ATP damage accumulated since last damage application.
     */
    private float damageCounter = 0f;
    // Energy Use tracking
    private float lastX;
    private float lastY;
    private int sizeUpgradeLevel = 0; // size upgrades 0-4
    private int organelleUpgradeLevel = 0; //organelle upgrades 0-4
    private float currentATPLost = 0f; // used to track atp loss up to 1
    private float totalATPLossFactor = 0f; //tracks total atp burn
    private float distanceMovedSinceLastTick;
    private boolean wasAtpBurnedThisFrame = false; //tracks if ATP has been burnt, mostly for testing
    private float currTimeTakenforATPLoss = 0f;
    private float lastTimeTakenforATPLoss = 0f;
    // Death tracking / effects
    private boolean isDying = false; // Whether the cell is dying
    private float deathAnimationTime = 0f; // Time spent in the death animation
    private float flagellumLengthLossFactor = 0f;
    private float flagellumAlpha = 1f;

    private Vector2[] healRates = new Vector2[4]; //Holds heal,cost vectors for healing

    /**
     * Whether this cell has split and created a save point
     */
    private boolean hasSplit = false;

    public Cell(GamePlayScreen gamePlayScreen, AssetManager assetManager, ConfigProvider configProvider) {
        this.assetManager = assetManager;
        this.gamePlayScreen = gamePlayScreen;
        this.configProvider = configProvider;

        setUserConfigOrDefault();

        var position = new Vector2(0, 0);
        cellCircle = new Circle(position, cellSize / 2);
        forceCircle = new Circle(position, cellSize * FORCE_CIRCLE_SIZE_MULTIPLIER);
    }

    /**
     * Copy constructor for `Cell`.
     */
    public Cell(Cell other) {
        this(other.gamePlayScreen, other.assetManager, other.configProvider);

        this.cellCircle = new Circle(other.cellCircle);
        // Values used to push glucose
        this.forceCircle = new Circle(other.forceCircle);
        this.smoothedVelocity = new Vector2(other.smoothedVelocity);
        this.flagellumVectors = new Array<>(other.flagellumVectors);
        this.previousPosition = new Vector2(other.previousPosition);
        this.cellSize = other.cellSize;
        this.cellHealth = other.cellHealth;
        this.cellATP = other.cellATP;
        this.hasMitochondria = other.hasMitochondria;
        this.hasRibosomes = other.hasRibosomes;
        this.hasFlagella = other.hasFlagella;
        this.hasNucleus = other.hasNucleus;
        this.hasSmallSizeUpgrade = other.hasSmallSizeUpgrade;
        this.hasMediumSizeUpgrade = other.hasMediumSizeUpgrade;
        this.hasLargeSizeUpgrade = other.hasLargeSizeUpgrade;
        this.hasMassiveSizeUpgrade = other.hasMassiveSizeUpgrade;
        this.nucleusPulse = other.nucleusPulse;
        this.pulseScale = other.pulseScale;
        this.forceCircleSizeScalar = other.forceCircleSizeScalar;
        this.glucoseVectorScaleFactor = other.glucoseVectorScaleFactor;
        this.cellRotation = other.cellRotation;
        this.amplitude = other.amplitude;
        this.frequency = other.frequency;
        this.flagTime = other.flagTime;
        this.wiggleVelocityMultiplier = other.wiggleVelocityMultiplier;
        this.damageTimer = other.damageTimer;
        this.damageCounter = other.damageCounter;
        this.lastX = other.lastX;
        this.lastY = other.lastY;
        this.sizeUpgradeLevel = other.sizeUpgradeLevel;
        this.organelleUpgradeLevel = other.organelleUpgradeLevel;
        this.currentATPLost = other.currentATPLost;
        this.totalATPLossFactor = other.totalATPLossFactor;
        this.distanceMovedSinceLastTick = other.distanceMovedSinceLastTick;
        this.wasAtpBurnedThisFrame = other.wasAtpBurnedThisFrame;
        this.currTimeTakenforATPLoss = other.currTimeTakenforATPLoss;
        this.lastTimeTakenforATPLoss = other.lastTimeTakenforATPLoss;
        this.isDying = other.isDying;
        this.deathAnimationTime = other.deathAnimationTime;
        this.hasSplit = other.hasSplit;
        this.notUpgradeRenderCycle = other.notUpgradeRenderCycle;
    }

    /**
     * Moves the cell based on input direction as well as its collision circle,
     * diagonal movement is normalized.
     * <p>
     * Updates target rotation to match desired angle of cell to align with
     * movement.
     * <p>
     * Note: Rotation angles appear to be counterclockwise.
     *
     * @param deltaTime - The time passed since the last frame
     * @param moveLeft  - If the cell should move left
     * @param moveRight - If the cell should move right
     * @param moveUp    - If the cell should move up
     * @param moveDown  - If the cell should move down
     */
    public void move(float deltaTime, boolean moveLeft, boolean moveRight, boolean moveUp, boolean moveDown) {
        //track these values to calculate ATP Burn.

        //prevents undesirable movement and rotation.
        if ((moveLeft && moveRight) || (moveUp && moveDown)) {
            return;
        }

        lastX = cellCircle.x;
        lastY = cellCircle.y;

        float targetRotation = cellRotation;

        float dx = 0;
        float dy = 0;

        if (moveUp) {
            dy += 1;
            targetRotation = 0f;
        }
        if (moveLeft) {
            dx -= 1;
            targetRotation = 90f;
        }
        if (moveDown) {
            dy -= 1;
            targetRotation = 180f;
        }
        if (moveRight) {
            dx += 1;
            targetRotation = 270f;
        }

        // If there is movement along a diagonal specifically set the target rotation.
        if (moveUp && moveLeft) {
            targetRotation = 45;
        }
        if (moveDown && moveLeft) {
            targetRotation = 135;
        }
        if (moveDown && moveRight) {
            targetRotation = 225;
        }
        if (moveUp && moveRight) {
            targetRotation = 315;
        }

        //Use linear interpolation to smooth the cell rotation angle.
        //All cell textures rely on this angle to rotate.
        cellRotation = lerpAngleDeg(cellRotation, targetRotation, deltaTime * ROTATION_SPEED);

        // Normalize movement along diagonal
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx /= length;
            dy /= length;
        }

        //Update cell position.
        cellCircle.x += dx * CELL_SPEED * deltaTime;
        cellCircle.y += dy * CELL_SPEED * deltaTime;

        //Update force circle
        setCellForceCircle(cellCircle.x, cellCircle.y);

        // game stasts
        if (moveLeft || moveRight || moveUp || moveDown) {
            gamePlayScreen.stats.distanceMoved += CELL_SPEED * deltaTime;
        }
    }

    /**
     * ATP Loss Calculation
     * <p>
     * Returns ATPburn based on movement upgrades and size.
     * <p>
     * When moving burn rate is twice the base burn. See
     * setTotalLossFactor() for more detailed burn rates.
     *
     * @param deltaTime time since last render
     */
    private void calculateATPLoss(float deltaTime) {
        distanceMovedSinceLastTick = abs(lastX - cellCircle.x) + abs(lastY - cellCircle.y);
        currTimeTakenforATPLoss += deltaTime;

        float movementMultiplier = (1 - (1 / (1 + distanceMovedSinceLastTick)));
        if (movementMultiplier > 0) {
            currentATPLost += deltaTime * ((2 * totalATPLossFactor));
        } else {
            currentATPLost += deltaTime * (totalATPLossFactor);
        }

    }

    /**
     * TotalLossFactor Settor
     *
     * Sets the loss factor based on cell size, and number of upgrades.
     *
     * Case = Size of Cell
     *
     * Idle burn rate are given for each case.
     * Moving burn rates are half of these values and calculated in the
     * calculate ATP Loss function.
     * Organelles lower the base burn rate by the upgrade level.
     * @return The total loss factor.
     *
     * TODO: make values static and implement into config.
     */
    /**
     * Base Burn Rate(BBR) - Rate burned when idle at given size
     * Each Organelle upgrade reduces this value by 1
     * Each Size upgrade reduces this value by 1
     * <p>
     * +----+---------+------+-----+------+------+------+
     * |    | Type    | BBR  | Mit | Ribo | Flag | Nuke |
     * +====+=========+======+=====+=====+======+======+=
     * |  0 | none    |  11  |  -  |  -   |  -   |  -   |
     * +----+---------+------+-----+-----+------+-------+
     * |  1 | small   |  10  |  9  |  -   |  -   |  -   |
     * +----+---------+------+-----+-----+------+-------+
     * |  2 | medium  |  9   |  8  |  7   |  -   |  -   |
     * +----+---------+------+-----+-----+------+-------+
     * |  3 | large   |  8   |  7  |  6   |  5   |  -   |
     * +----+---------+------+-----+-----+------+-------+
     * |  4 | massive |  7   |  6  |  5   |  4   |  3   |
     * +----+---------+------+-----+-----+------+-------+
     */
    private float setTotalLossFactor() {
        switch (sizeUpgradeLevel) {
            case 0:
                return 1f / (11f - organelleUpgradeLevel); // No upgrade (1 ATP -> 11 sec idle)
            case 1:
                return 1f / (10f - organelleUpgradeLevel); // 1 ATP -> 10 sec
            case 2:
                return 1f / (9f - organelleUpgradeLevel); // 1 ATP -> 9 sec
            case 3:
                return 1f / (8f - organelleUpgradeLevel); // 1 ATP -> 8 sec
            case 4:
                return 1f / (7f - organelleUpgradeLevel); // 1 ATP -> 7 sec
            //should never be hit.
            default:
                return 0.0f;
        }
    }

    /**
     * Heal Cost Factor Setter
     *
     * Sets both the ATP_HEAL_COST and AMOUNT_HEALED
     * based on organelle upgrade level.
     * @param organelleUpgradeLevel
     */
    private void setHealCostFactor(int organelleUpgradeLevel) {
        int indexShift = organelleUpgradeLevel - 1;
        switch (indexShift) {
            case 1:
                //Ribo - Cost: 15 / Healed: 10
                ATP_HEAL_COST = (int) healRates[1].x;
                AMOUNT_HEALED = (int) healRates[1].y;
                break;
            case 2:
                //Flag - Cost: 10 / Healed: 15
                ATP_HEAL_COST = (int) healRates[2].x;
                AMOUNT_HEALED = (int) healRates[2].y;
                break;
            case 3:
                //Nuke - Cost: 5 / Healed: 20
                ATP_HEAL_COST = (int) healRates[3].x;
                AMOUNT_HEALED = (int) healRates[3].y;
                break;
            default:
                //Mito/Default - Cost: 20 / Healed: 5
                ATP_HEAL_COST = (int) healRates[0].x;
                AMOUNT_HEALED = (int) healRates[0].y;

        }

    }

    /**
     * Moves the cell to a specific position
     *
     * @param x - The new X position
     * @param y - The new Y position
     */
    public void moveTo(float x, float y) {
        this.cellCircle.x = x;
        this.cellCircle.y = y;
    }

    /**
     * Draw
     *
     * @param batch - The passed spritebatch.
     */
    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        float alpha = 1f;
        if (isDying) {
            alpha = Math.max(0, 1 - (deathAnimationTime / DEATH_ANIMATION_DURATION));
        }
        batch.setColor(1, 1, 1, alpha);

        // Draw cell centered around its position.

        float bottomLeftX = cellCircle.x - (cellSize / 2);
        float bottomLeftY = cellCircle.y - (cellSize / 2);

        // Get the already-loaded cell texture
        // The asset manager expects the asset's file name,
        // and the class of the asset to load.

        //cell Texture
        Texture cellTexture;
        if (hasSmallSizeUpgrade) {
            if (cellHealth <= (MAX_HEALTH / 2)) {
                cellTexture = assetManager.get(AssetFileNames.CELL_MEMBRANE_DAMAGED, Texture.class);
            } else {
                cellTexture = assetManager.get(AssetFileNames.CELL_MEMBRANE, Texture.class);

            }
        } else {
            cellTexture = assetManager.get(AssetFileNames.CELL, Texture.class);
        }

        drawFlagellum(hasFlagella, shapeRenderer); //moved outside of draw organelles to be underneath the cell.

        batch.begin();

        batch.draw(
            cellTexture,
            bottomLeftX, bottomLeftY,
            cellSize / 2, cellSize / 2,
            cellSize, cellSize,
            1f, 1f,
            cellRotation,
            0, 0,
            cellTexture.getWidth(), cellTexture.getHeight(),
            false, false
        );

        drawOrganelles(batch);
        batch.end();

        batch.setColor(Color.WHITE);
        if (GamePlayScreen.DEBUG_DRAW_ENABLED) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.circle(cellCircle.x, cellCircle.y, cellCircle.radius);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.circle(forceCircle.x, forceCircle.y, forceCircle.radius);
            shapeRenderer.end();
        }

    }

    public void update(float deltaTimeSeconds) {
        if (isDying) {
            deathAnimationTime += deltaTimeSeconds;
            if (deathAnimationTime >= DEATH_ANIMATION_DURATION) {
                gamePlayScreen.endGame();
            }
            updateFlagellum(deltaTimeSeconds); //used to fade the flagellum
            return;
        }

        damageIfZeroATP(deltaTimeSeconds);

        //Recalculate loss factor.
        totalATPLossFactor = setTotalLossFactor();
        calculateATPLoss(deltaTimeSeconds);

        //Used for testing. Set when 1 ATP burn has occurred.
        if (wasAtpBurnedThisFrame) {
            wasAtpBurnedThisFrame = false;
        }

        //tracked for ATP and game over stats.
        distanceMovedSinceLastTick = 0f;

        // Update animations
        if (hasNucleus) {
            nucleusPulse += deltaTimeSeconds; // Adjust pulse speed as needed
            pulseScale = 1.0f + 0.1f * MathUtils.sin(nucleusPulse * 2.0f); // Adjust pulse effect
        }


        //When ATP loss accumulates above 1, subtract and reset currentATPLost.
        if (currentATPLost >= 1) {
            if (cellATP > 0) {
                cellATP -= 1;
                wasAtpBurnedThisFrame = true;
                lastTimeTakenforATPLoss = currTimeTakenforATPLoss;
                currTimeTakenforATPLoss = 0f;
            }
            currentATPLost = 0;
        }

        // Update stats
        var organellesPurchased = List.of(hasMitochondria, hasFlagella, hasNucleus, hasRibosomes)
            .stream().filter(Boolean::booleanValue).count();
        gamePlayScreen.stats.organellesPurchased = (int) organellesPurchased;
        int maxSize;
        if (hasMassiveSizeUpgrade) {
            maxSize = 4;
        } else if (hasLargeSizeUpgrade) {
            maxSize = 3;
        } else if (hasMediumSizeUpgrade) {
            maxSize = 2;
        } else if (hasSmallSizeUpgrade) {
            maxSize = 1;
        } else {
            maxSize = 0;
        }
        gamePlayScreen.stats.maxSize = maxSize;

        updateFlagellum(deltaTimeSeconds);
        setHealCostFactor(organelleUpgradeLevel);
    }

    private void damageIfZeroATP(float deltaTimeSeconds) {
        var damage = ZERO_ATP_DAMAGE_PER_SECOND * deltaTimeSeconds;
        if (getCellATP() > 0) {
            // Reset damage counter if we ever have > 0 ATP
            damageTimer = 0f;
            damageCounter = 0f;
        } else if (damageTimer > ZERO_ATP_DAMAGE_INCREMENT_SECONDS && damageCounter > 1) {
            applyDamage((int) damageCounter);
            damageTimer = deltaTimeSeconds;
            damageCounter = damage;
        } else {
            damageTimer += deltaTimeSeconds;
            damageCounter += damage;
        }
    }

    private void drawOrganelles(SpriteBatch batch) {
        float cellRadius = cellSize / 2f;
        float centerX = cellCircle.x;
        float centerY = cellCircle.y;

        if (hasMitochondria) {
            var mitochondriaTexture = assetManager.get(AssetFileNames.MITOCHONDRIA_ICON, Texture.class);

            assert (mitochondriaTexture != null);

            float size = cellSize * 0.22f;

            Vector2 offset1 = new Vector2(+cellRadius * 0.5f, +cellRadius * 0.5f).rotateDeg(cellRotation);
            Vector2 offset2 = new Vector2(-cellRadius * 0.5f, -cellRadius * 0.5f).rotateDeg(cellRotation);

            float angle1 = cellRotation + 135;
            float angle2 = cellRotation + 315;

            // Draw first mitochondrion (top-right side)
            batch.draw(
                mitochondriaTexture,
                centerX + offset1.x - size / 2,
                centerY + offset1.y - size / 2,
                size / 2, size / 2,
                size, size,
                1f, 1f,
                angle1,
                0, 0,
                mitochondriaTexture.getWidth(), mitochondriaTexture.getHeight(),
                false, true
            );

            // Draw second mitochondrion (bottom-left side)
            batch.draw(
                mitochondriaTexture,
                centerX + offset2.x - size / 2,
                centerY + offset2.y - size / 2,
                size / 2, size / 2,
                size, size,
                1f, 1f,
                angle2,
                0, 0,
                mitochondriaTexture.getWidth(), mitochondriaTexture.getHeight(),
                false, true
            );
        }

        // Draw ribosomes (top-left quadrant)
        if (hasRibosomes) {
            var ribosomeTexture = assetManager.get(AssetFileNames.RIBOSOME_ICON, Texture.class);

            assert (ribosomeTexture != null);

            float ribosomeSize = cellSize * 0.2f; // Adjust size as needed

            Vector2 offset1 = new Vector2(-cellRadius * 0.4f, +cellRadius * 0.5f).rotateDeg(cellRotation); // Top-left
            Vector2 offset2 = new Vector2(+cellRadius * 0.6f, -cellRadius * 0.1f).rotateDeg(cellRotation); // Bottom-right
            Vector2 offset3 = new Vector2(+cellRadius * 0.1f, -cellRadius * 0.7f).rotateDeg(cellRotation); // Bottom-middle-left

            // Draw first ribosome (top-left side)
            batch.draw(
                ribosomeTexture,
                centerX + offset1.x - ribosomeSize / 2,
                centerY + offset1.y - ribosomeSize / 2,
                ribosomeSize, ribosomeSize
            );

            // Draw second ribosome (bottom-right side)
            batch.draw(
                ribosomeTexture,
                centerX + offset2.x - ribosomeSize / 2,
                centerY + offset2.y - ribosomeSize / 2,
                ribosomeSize, ribosomeSize
            );

            // Draw third ribosome (bottom-middle-left side)
            batch.draw(
                ribosomeTexture,
                centerX + offset3.x - ribosomeSize / 2,
                centerY + offset3.y - ribosomeSize / 2,
                ribosomeSize, ribosomeSize
            );
        }

        // Draw nucleus (center with pulse effect)
        if (hasNucleus) {
            var nucleusTexture = assetManager.get(AssetFileNames.NUCLEUS_ICON, Texture.class);

            assert (nucleusTexture != null);

            float baseSize = cellSize * 0.4f;
            float nucleusSize = baseSize * pulseScale; // Adjust size based on pulse effect

            batch.draw(
                nucleusTexture,
                centerX - nucleusSize / 2,
                centerY - nucleusSize / 2,
                nucleusSize / 2, nucleusSize / 2,
                nucleusSize, nucleusSize,
                1f, 1f,
                cellRotation,
                0, 0,
                nucleusTexture.getWidth(), nucleusTexture.getHeight(),
                false, false
            );
        }
    }

    /**
     * Sets Cell values based on the config file if it can be
     * found/read. Other wise default values are used.
     */
    private void setUserConfigOrDefault() {
        try {
            cellHealth = configProvider.getIntValue("cellHealth");
            //Eventually may need more robust handling if user enters crazy values.
        } catch (NumberFormatException e) {
            cellHealth = 100;
            //Eventually print message telling user there value hasn't been set.
        }
        try {
            MAX_HEALTH = configProvider.getIntValue("maxHealth");
            //Eventually may need more robust handling if user enters crazy values.
        } catch (NumberFormatException e) {
            MAX_HEALTH = 100;
            //Eventually print message telling user there value hasn't been set.
        }
        try {
            cellATP = configProvider.getIntValue("cellATP");
            //Eventually may need more robust handling if user enters crazy values.
        } catch (NumberFormatException e) {
            cellATP = 30;
            //Eventually print message telling user there value hasn't been set.
        }
        try {
            MAX_ATP = configProvider.getIntValue("maxATP");
            //Eventually may need more robust handling if user enters crazy values.
        } catch (NumberFormatException e) {
            MAX_ATP = 100;
            //Eventually print message telling user there value hasn't been set.
        }

        try {
            CELL_SPEED = configProvider.getFloatValue("cellMovementSpeed");
        } catch (NumberFormatException e) {
            CELL_SPEED = 200f;
        }

        try {
            ROTATION_SPEED = configProvider.getFloatValue("cellRotationSpeed");
        } catch (NumberFormatException e) {
            ROTATION_SPEED = 20f;
        }
        try {
            ATP_HEAL_COST = configProvider.getIntValue("atpHealCost");
        } catch (NumberFormatException e) {
            ATP_HEAL_COST = 5;
        }

        try {
            AMOUNT_HEALED = configProvider.getIntValue("amountHealed");
        } catch (NumberFormatException e) {
            AMOUNT_HEALED = 5;

        }

        try {
            CELL_SPEED_WITH_FLAGELLA = configProvider.getFloatValue("cellFlagMovementSpeed");
            System.out.println("CSWF: " + CELL_SPEED_WITH_FLAGELLA);
        } catch (NumberFormatException e) {
            CELL_SPEED_WITH_FLAGELLA = 370f;

        }
        try {
            amplitude = configProvider.getFloatValue("amplitude");
        } catch (NumberFormatException e) {
            amplitude = 25f;
        }
        try {
            frequency = configProvider.getFloatValue("frequency");
        } catch (NumberFormatException e) {
            frequency = 0.05f;
        }
        try {
            wiggleVelocityMultiplier = configProvider.getIntValue("velocity");
        } catch (NumberFormatException e) {
            wiggleVelocityMultiplier = 5;
        }
        try {
            MEMBRANE_DAMAGE_REDUCTION = configProvider.getIntValue("damageReduction");
        } catch (NumberFormatException e) {
            MEMBRANE_DAMAGE_REDUCTION = 2;
        }
        try {
            healRates[0] = configProvider.getVector2("mitoHCost-Heal");
            healRates[1] = configProvider.getVector2("riboCost-Heal");
            healRates[2] = configProvider.getVector2("flagCost-Heal");
            healRates[3] = configProvider.getVector2("nukeCost-Heal");
        } catch (Exception e) {
            healRates[0] = new Vector2(20, 5);
            healRates[1] = new Vector2(15, 10);
            healRates[2] = new Vector2(10, 15);
            healRates[3] = new Vector2(5, 20);
        }

    }

    /**
     * Get Cell Position X
     *
     * @return cellPositionX
     */
    public float getX() {
        return cellCircle.x;
    }

    /**
     * Get Cell Position Y
     *
     * @return cellPositionY
     */
    public float getY() {
        return cellCircle.y;
    }

    /**
     * Get Cell Diameter
     *
     * @return cellSize
     */
    public int getCellSize() {
        return (int) cellSize;
    }

    public void setCellSize(float cellSize) {
        this.cellSize = cellSize;
        cellCircle.radius = cellSize / 2;
        forceCircle.radius = cellCircle.radius * FORCE_CIRCLE_SIZE_MULTIPLIER * forceCircleSizeScalar;
    }

    /**
     * Health Getter
     *
     * @return CellHealth
     */
    public int getCellHealth() {
        return cellHealth;
    }

    /**
     * Set Cell Health
     *
     * @param cellHealth
     */
    public void setCellHealth(int cellHealth) {
        this.cellHealth = cellHealth;
    }

    /**
     * ATP Getter
     *
     * @return ATP
     */
    public int getCellATP() {
        return cellATP;
    }

    /**
     * Set Cell Health
     *
     * @param cellATP
     */
    public void setCellATP(int cellATP) {
        this.cellATP = cellATP;
    }

    /**
     * Max Health Getter
     *
     * @return Cell Max Health
     */
    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    /**
     * MAX ATP Getter
     *
     * @return Cell Max ATP
     */
    public int getMaxATP() {
        return MAX_ATP;
    }

    public float getHeight() {
        return cellSize;
    }

    /**
     * Dispose
     */
    public void dispose() {
        assetManager.unload(AssetFileNames.CELL);
        assetManager.unload(AssetFileNames.CELL_MEMBRANE);
        assetManager.unload(AssetFileNames.CELL_MEMBRANE_DAMAGED);

    }

    /**
     * Returns the Cell BoundingCircle
     */
    public Circle getCircle() {
        return cellCircle;
    }

    /**
     * Adds ATP
     * <p>
     * increases ATP for glucose collection
     */
    public void addCellATP(int increaseAmount) {
        cellATP = Math.min(cellATP + increaseAmount, MAX_ATP);
    }

    /**
     * Adds ATP
     * decreases ATP
     */
    public void removeCellATP(int decreaseAmount) {
        cellATP = Math.max(cellATP - decreaseAmount, 0);
    }

    /**
     * Increases the cell size This method also increases the values used to
     * push glucose away.
     *
     * @param sizeIncrease - The amount to increase the cell by.
     */
    public void increaseCellSize(float sizeIncrease) {
        this.cellSize += sizeIncrease;

        //increase forceCircle factors
        forceCircleSizeScalar += .125f;
        glucoseVectorScaleFactor += 10f;

        //Increase radi
        cellCircle.radius += sizeIncrease / 2;
        forceCircle.radius = cellCircle.radius * FORCE_CIRCLE_SIZE_MULTIPLIER * forceCircleSizeScalar;
    }

    /**
     * Set whether the cell has the mitochondria upgrade
     */
    public void setHasMitochondria(boolean hasMitochondria) {
        this.hasMitochondria = hasMitochondria;
        if ((organelleUpgradeLevel < MAX_ORGANELLE_UPGRADES) && hasMitochondria) organelleUpgradeLevel++;
    }

    /**
     * Applies damage to the cell.
     * Ends the game if cell health goes below 0.
     *
     * @param damage Damage to apply.
     */
    public void applyDamage(int damage) {
        if (isDying) return;

        cellHealth = Math.max(0, cellHealth - damage);

        if (cellHealth <= 0 && !isDying) {
            isDying = true;
            deathAnimationTime = 0f;
            gamePlayScreen.triggerShake(1.5f, 12f); // Shake for 1.5 seconds
        }
    }

    public int getMembraneDamageReduction() {
        return MEMBRANE_DAMAGE_REDUCTION;
    }

    /**
     * Decreases current atp amount in order to increase cell Health
     * Only usable if the Cell has the Mitochondria upgrade
     */
    public void healDamage() {
        if (cellATP > ATP_HEAL_COST && cellHealth < MAX_HEALTH) {
            if (cellHealth - MAX_HEALTH < AMOUNT_HEALED) {
                if (MAX_HEALTH - cellHealth < AMOUNT_HEALED) {
                    cellATP -= ATP_HEAL_COST;
                    cellHealth = MAX_HEALTH;
                } else {
                    cellATP -= ATP_HEAL_COST;
                    this.cellHealth += AMOUNT_HEALED;
                }
            }
        }
    }

    /**
     * Check if the cell has the mitochondria upgrade
     */
    public boolean hasMitochondria() {
        return hasMitochondria;
    }

    /**
     * Set whether the cell has the ribosomes upgrade
     */
    public void setHasRibosomes(boolean hasRibosomes) {
        this.hasRibosomes = hasRibosomes;
        if ((organelleUpgradeLevel < MAX_ORGANELLE_UPGRADES) && hasRibosomes) organelleUpgradeLevel++;
    }

    /**
     * Check if the cell has the ribosomes upgrade
     */
    public boolean hasRibosomes() {
        return hasRibosomes;
    }

    /**
     * Set whether the cell has the flagella upgrade
     */
    public void setHasFlagella(boolean hasFlagella) {
        this.hasFlagella = hasFlagella;
        if ((organelleUpgradeLevel < MAX_ORGANELLE_UPGRADES) && hasFlagella) organelleUpgradeLevel++;

        //flagella increases movement speed.
        CELL_SPEED = CELL_SPEED_WITH_FLAGELLA;
    }

    /**
     * Check if the cell has the flagella upgrade
     */
    public boolean hasFlagella() {
        return hasFlagella;
    }

    /**
     * Set whether the cell has the nucleus upgrade
     */
    public void setHasNucleus(boolean hasNucleus) {
        this.hasNucleus = hasNucleus;
        if ((organelleUpgradeLevel < MAX_ORGANELLE_UPGRADES) && hasNucleus) organelleUpgradeLevel++;
    }

    /**
     * Check if the cell has the nucleus upgrade
     */
    public boolean hasNucleus() {
        return hasNucleus;
    }


    /**
     * Check if the cell has the small size upgrade
     */
    public boolean hasSmallSizeUpgrade() {
        return hasSmallSizeUpgrade;
    }

    /**
     * Set whether the cell has the small size upgrade
     */
    public void setHasSmallSizeUpgrade(boolean hasSmallSizeUpgrade) {
        this.hasSmallSizeUpgrade = hasSmallSizeUpgrade;
        if ((sizeUpgradeLevel < MAX_SIZE_UPGRADES) && hasSmallSizeUpgrade) sizeUpgradeLevel++;
    }

    /**
     * Check if the cell has the medium size upgrade
     */
    public boolean hasMediumSizeUpgrade() {
        return hasMediumSizeUpgrade;
    }

    /**
     * Set whether the cell has the medium size upgrade
     */
    public void setHasMediumSizeUpgrade(boolean hasMediumSizeUpgrade) {
        this.hasMediumSizeUpgrade = hasMediumSizeUpgrade;
        if ((sizeUpgradeLevel < MAX_SIZE_UPGRADES) && hasMediumSizeUpgrade) sizeUpgradeLevel++;
    }

    /**
     * Check if the cell has the large size upgrade
     */
    public boolean hasLargeSizeUpgrade() {
        return hasLargeSizeUpgrade;

    }

    /**
     * Set whether the cell has the large size upgrade
     */
    public void setHasLargeSizeUpgrade(boolean hasLargeSizeUpgrade) {
        this.hasLargeSizeUpgrade = hasLargeSizeUpgrade;
        if ((sizeUpgradeLevel < MAX_SIZE_UPGRADES) && hasLargeSizeUpgrade) sizeUpgradeLevel++;
        notUpgradeRenderCycle = false;
    }

    /**
     * Check if the cell has the massive size upgrade
     */
    public boolean hasMassiveSizeUpgrade() {
        return hasMassiveSizeUpgrade;
    }

    /**
     * Set whether the cell has the massive size upgrade
     */
    public void setHasMassiveSizeUpgrade(boolean hasMassiveSizeUpgrade) {
        if ((sizeUpgradeLevel < MAX_SIZE_UPGRADES) && hasMassiveSizeUpgrade) sizeUpgradeLevel++;
        this.hasMassiveSizeUpgrade = hasMassiveSizeUpgrade;
        setFlagellumThickness(12.5f);
        setFlagellumLength(300); //The new length of the flagellum.
        notUpgradeRenderCycle = false;
    }

    /**
     * Get Size Upgrade level
     *
     * @return size upgrade level
     */
    public int getSizeUpgradeLevel() {
        return sizeUpgradeLevel;
    }

    public void setSizeUpgradeLevel(int level) {
        this.sizeUpgradeLevel = level;
    }

    /**
     * Get Organelle Upgrade level
     *
     * @return Organelle upgrade level
     */
    public int getOrganelleUpgradeLevel() {
        return organelleUpgradeLevel;
    }

    /**
     * Current ATP Lost Getter
     *
     * @return the Current ammount of atp lost.
     */
    public float getCurrentATPLost() {
        return currentATPLost;
    }

    /**
     * Loss Factor getter
     *
     * @return The loss factor.
     */
    public float getTotalATPLossFactor() {
        return totalATPLossFactor;
    }

    /**
     * ATP flag getter
     * <p>
     * Tracks if ATP burn occured this render cycle.
     *
     * @return the state of the ATP flag
     */
    public boolean isWasAtpBurnedThisFrame() {
        return wasAtpBurnedThisFrame;
    }

    /**
     * CurrTimeTaken Getter
     * <p>
     * Tracks time taken for the current ATP loss.
     *
     * @return The time taken for atp loss.
     */
    public float getCurrTimeTakenforATPLoss() {
        return currTimeTakenforATPLoss;
    }

    /**
     * LastTimeTaken Getter
     * <p>
     * The time taken for the previous ATP loss.
     *
     * @return THe previous time taken for atp loss.
     */
    public float getLastTimeTakenforATPLoss() {
        return lastTimeTakenforATPLoss;
    }

    /**
     * cellForceCircleUpdater
     * <p>
     * Updates the position of the forceCircle with new values.
     *
     * @param newX The new X value
     * @param newY The new Y value
     */
    private void setCellForceCircle(float newX, float newY) {
        forceCircle.setX(newX);
        forceCircle.setY(newY);
    }

    /**
     * Force Circle getter
     *
     * @return The forceCricle
     */
    public Circle getForceCircle() {
        return forceCircle;
    }

    public void drawFlagellum(boolean drawFlagellum, ShapeRenderer shapeRenderer) {

        if (!drawFlagellum || flagellumVectors.isEmpty()) return;

        //These glCommands enable alpha to actually affect the color.
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.239f, 0.498f, 0.651f, flagellumAlpha);

        for (int i = 0; i < flagellumVectors.size; i++) {
            Vector2 point = new Vector2(cellCircle.x + flagellumVectors.get(i).x, cellCircle.y + flagellumVectors.get(i).y);

            //Draw Circles along the sine wave.
            shapeRenderer.circle(point.x, point.y, flagellumThickness);
        }

        shapeRenderer.end();
        //Disable this command after drawing.
        Gdx.gl.glDisable(GL20.GL_BLEND);

    }

    public void updateFlagellum(float deltaTime) {
        //Dying Animation
        if (isDying) {
            //Length Change
            flagellumLengthLossFactor += (flagellumLength / DEATH_ANIMATION_DURATION) * deltaTime * 1.25f;
            //Float, so if ths is close to 1, subtract.
            //Required as length is an int.
            if (flagellumLengthLossFactor >= 0.9) {
                flagellumLength -= 1;
                flagellumLengthLossFactor = 0;
            }
            //Fade
            flagellumAlpha -= (flagellumAlpha / DEATH_ANIMATION_DURATION) * deltaTime * 6;
            //Again, floats suck.
            if (flagellumAlpha <= 0.001) {
                flagellumAlpha = 0;
            }

            //Change thickness.
            flagellumThickness -= (flagellumThickness / DEATH_ANIMATION_DURATION) * deltaTime;
        }

        //Prevent moving flagellum if cell hasn't moved
        if ((cellCircle.x == previousPosition.x && cellCircle.y == previousPosition.y && cellRotation == previousRotation) && !isDying) {

            if (notUpgradeRenderCycle) {
                return;
            }
            notUpgradeRenderCycle = true;
        }
        //Track movement for the next render cycle
        previousPosition.set(cellCircle.x, cellCircle.y);
        previousRotation = cellRotation;

        flagellumVectors.clear();

        //calculate new sin wave positions.
        for (int y = 0; y < flagellumLength; y++) {
            float flagX = (float) (amplitude * Math.sin((y * frequency + flagTime)));
            flagellumVectors.add(new Vector2(flagX, y - cellCircle.radius - flagellumLength)); // <-shifts flagella down, stupid calc, but it works
        }

        // Rotate the flagellum
        for (int i = 0; i < flagellumVectors.size; i++) {
            Vector2 vector = flagellumVectors.get(i);
            vector.rotateDeg(cellRotation);
        }

        flagTime += deltaTime * wiggleVelocityMultiplier;
    }

    /**
     * Cell Speed Getter
     *
     * @return The Cell Speed
     */
    public float getCellSpeed() {
        return CELL_SPEED;
    }

    /**
     * Getter For the GlucoseVectorScaleFactor
     *
     * @return How much to scale glucose pushing.
     */
    public float getGlucoseVectorScaleFactor() {
        return glucoseVectorScaleFactor;
    }

    /**
     * Cell angle getter
     * Return the current angle of the cell.
     *
     * @return The cell Angle
     */
    public float getCellRotation() {
        return cellRotation;
    }

    /**
     * Sets the cells rotation to the given angle.
     *
     * @param angle Angle in degrees
     */
    public void setCellRotation(float angle) {
        this.cellRotation = angle;
    }

    /**
     * Get the death animation time
     * This is the time spent in the death animation
     *
     * @return The time spent in the death animation
     */
    public float getDeathAnimationTime() {
        return deathAnimationTime;
    }

    /**
     * Get the death animation duration
     * This is the time it takes for the cell to die
     *
     * @return The duration of the death animation
     */
    public float getDeathAnimationDuration() {
        return DEATH_ANIMATION_DURATION;
    }

    /**
     * Get the velocity of the cell
     * This is the difference between the current position and the last position
     *
     * @return The velocity of the cell
     */
    public Vector2 getVelocity() {
        float dx = cellCircle.x - lastX;
        float dy = cellCircle.y - lastY;

        Vector2 frameVelocity = new Vector2(dx, dy);

        // Smooth the velocity
        smoothedVelocity.lerp(frameVelocity, VELOCITY_SMOOTHING);

        return smoothedVelocity;
    }

    /**
     * hasSplitGetter
     *
     * @return True if cell ahs split.
     */
    public boolean hasSplit() {
        return hasSplit;
    }

    /**
     * Has Split Setter
     *
     * @param hasSplit If the cell has split
     */
    public void setHasSplit(boolean hasSplit) {
        this.hasSplit = hasSplit;
    }

    /**
     * Flagellum Vectors Getter
     *
     * @return The Flagellum Vectors
     */
    public Array<Vector2> getFlagellumVectors() {
        return this.flagellumVectors;
    }

    /**
     * Flagellum Thickness Getter
     *
     * @return The thickness of the flagellum.
     */
    public float getFlagellumThickness() {
        return flagellumThickness;
    }

    /**
     * Flagellum Length Setter
     *
     * @param value The new thickness
     */
    public void setFlagellumThickness(float value) {
        flagellumThickness = value;
    }

    /**
     * Set The length of the flagellum.
     *
     * @param value The new length
     */
    public void setFlagellumLength(int value) {
        flagellumLength = value;
    }

    /**
     * Return the length of the flagellum
     *
     * @return The Length of the flagellum
     */
    public int getFlagellumLength() {
        return flagellumLength;
    }

    /**
     * OgranelleUpgradeLevelSetter
     * CAUTION: Likely should only be used with saving/loading.
     * @param level The new number.
     */
    public void setOrganelleUpgradeLevel(int level) {
        System.out.println("LEVEL: " + level);
        if (level < 0) {
            organelleUpgradeLevel = 0;
        } else if (level > 4) {
            organelleUpgradeLevel = 4;
        } else {
            organelleUpgradeLevel = level;
        }
        System.out.println("POSTUPDATE: " + organelleUpgradeLevel);
    }

    /**
     * Heal Rates Getter
     * @return The heal reates.
     */
    public Vector2[] getHealRates() {
        return healRates;
    }

    /**
     * Is Dying Getter
     * @return True if the cell is dying.
     */
    public boolean isDying() {
        return isDying;
    }
}
