package cellcorp.gameofcells.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;
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
    public static int MAX_HEALTH = 100;
    public static int MAX_ATP = 100;
    private static float CELL_SPEED = 200f; // Speed of the cell
    public static int ZERO_ATP_DAMAGE_PER_SECOND = 10;
    private static float ROTATION_SPEED = 20f; //How quickly the cell rotates
    public static int ATP_HEAL_COST = 5;
    public static int AMOUNT_HEALED = 5;
    /**
     * Time between applications of zero-ATP damage, in seconds.
     */
    private static final int ZERO_ATP_DAMAGE_INCREMENT_SECONDS = 1;

    // May change, but used to ensure that invalid case selection is not hit.
    private static final int MAX_SIZE_UPGRADES = 4;
    private static final int MAX_ORGANELLE_UPGRADES = 4;
    float cellSize;

    private final AssetManager assetManager;
    private final ConfigProvider configProvider;
    private final GamePlayScreen gamePlayScreen;

    private int cellHealth;
    private int cellATP;
    private final Circle cellCircle;

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

    // Upgrade multipliers
    private float proteinSynthesisMultiplier = 1.0f;
    private float movementSpeedMultiplier = 1.0f;
    private boolean canSplit = false;

    // Values used to push glucose
    private final Circle forceCircle; // The circle surrounding the cell pushing glucose.
    private float forceCircleSizeScalar;
    private float forceCircleSizeMultiplier; // Used on forceCircle to scale up as the cell grows
    private float glucoseVectorScaleFactor; //Used to set how far glucose moves when pushed

    private float cellRotation = 0f; // The cells starting angle, tracks current angle of the cell.

    //flagellum
    private float amplitude = 50f;
    private float frequency = 0.05f;
    private float flagTime = 0f; // storePhase
    private int wiggleVelcoityMultiplier = 5; //How quickly to wiggle
    private Array<Vector2> FlagellumVectors = new Array<>(); //sine wave vectors
    private Vector2 previousPosition; //previous position of the cell

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
    private int sizeUpgradeLevel; // size upgrades 0-4
    private int organelleUpgradeLevel; //organelle upgrades 0-4
    private float currentATPLost; // used to track atp loss up to 1
    private float totalATPLossFactor; //tracks total atp burn
    private float distanceMovedSinceLastTick;
    private boolean wasAtpBurnedThisFrame; //tracks if ATP has been burnt, mostly for testing
    private float currTimeTakenforATPLoss;
    private float lastTimeTakenforATPLoss;

    //potential gameOverStat
    private float totalDistanceMoved;

    public Cell(GamePlayScreen gamePlayScreen, AssetManager assetManager, ConfigProvider configProvider) {
        this.assetManager = assetManager;
        this.gamePlayScreen = gamePlayScreen;
        this.configProvider = configProvider;

        setUserConfigOrDefault();
        cellSize = 100;

        totalATPLossFactor = 0f;
        sizeUpgradeLevel = 0;
        organelleUpgradeLevel = 0;
        currentATPLost = 0f;
        currTimeTakenforATPLoss = 0f;
        lastTimeTakenforATPLoss = 0f;
        wasAtpBurnedThisFrame = false;
        totalDistanceMoved = 0f;

        forceCircleSizeMultiplier = 1.375f;
        forceCircleSizeScalar = 1f;
        glucoseVectorScaleFactor = 50f;

        cellCircle = new Circle(new Vector2(0, 0), cellSize / 2);
        forceCircle = new Circle(new Vector2(0, 0), cellSize * forceCircleSizeMultiplier);
        previousPosition = new Vector2(-500, -500);
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
        // Draw cell centered around its position.

        float bottomLeftX = cellCircle.x - (cellSize / 2);
        float bottomLeftY = cellCircle.y - (cellSize / 2);

        // Get the already-loaded cell texture
        // The asset manager expects the asset's file name,
        // and the class of the asset to load.

        //cell Texture
        Texture cellTexture = assetManager.get(AssetFileNames.CELL, Texture.class);

        drawFlagellum(hasFlagella, shapeRenderer); //moved outside of draw organelles to be underneath the cell.

        batch.begin();

        batch.draw(cellTexture,
            bottomLeftX, bottomLeftY,
            cellSize / 2, cellSize / 2,
            cellSize, cellSize,
            1f, 1f,
            cellRotation,
            0, 0,
            cellTexture.getWidth(), cellTexture.getHeight(),
            false, false);

        drawOrganelles(batch);
        batch.end();
        if (GamePlayScreen.DEBUG_DRAW_ENABLED) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.circle(cellCircle.x, cellCircle.y, cellCircle.radius);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.circle(forceCircle.x, forceCircle.y, forceCircle.radius);
            shapeRenderer.end();
        }

    }

    public void update(float deltaTimeSeconds) {
        damageIfZeroATP(deltaTimeSeconds);

        //Recalculate loss factor.
        totalATPLossFactor = setTotalLossFactor();
        calculateATPLoss(deltaTimeSeconds);

        //Used for testing. Set when 1 ATP burn has occurred.
        if (wasAtpBurnedThisFrame) {
            wasAtpBurnedThisFrame = false;
        }

        //tracked for ATP and game over stats.
        totalDistanceMoved += distanceMovedSinceLastTick;
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

        // Draw mitochondria (bottom-left quadrant)
        if (hasMitochondria) {
            var mitochondriaTexture = assetManager.get(AssetFileNames.MITOCHONDRIA_ICON, Texture.class);
            float mitochondriaSize = cellSize * 0.3f; // Adjust size as needed

            float mitoX = centerX - mitochondriaSize / 2 - cellRadius * 0.5f;
            float mitoY = centerY - mitochondriaSize / 2 - cellRadius * 0.5f;

            batch.draw(mitochondriaTexture,
                mitoX, mitoY,
                centerX - mitoX, centerY - mitoY,
                mitochondriaSize, mitochondriaSize,
                1f, 1f,
                cellRotation,
                0, 0,
                mitochondriaTexture.getWidth(), mitochondriaTexture.getHeight(),
                false, false);
        }

        // Draw ribosomes (top-left quadrant)
        if (hasRibosomes) {
            var ribosomeTexture = assetManager.get(AssetFileNames.RIBOSOME_ICON, Texture.class);
            float ribosomeSize = cellSize * 0.2f; // Adjust size as needed

            float riboX = centerX - ribosomeSize / 2 - cellRadius * 0.7f;
            float riboY = centerY - ribosomeSize / 2 + cellRadius * 0.3f;

            batch.draw(ribosomeTexture,
                riboX, riboY,
                centerX - riboX, centerY - riboY,
                ribosomeSize, ribosomeSize,
                1f, 1f,
                cellRotation,
                0, 0,
                ribosomeTexture.getWidth(), ribosomeTexture.getHeight(),
                false, false);
        }


        // Draw nucleus (center with pulse effect)
        if (hasNucleus) {
            var nucleusTexture = assetManager.get(AssetFileNames.NUCLEUS_ICON, Texture.class);
            float nucleusSize = cellSize * 0.4f * pulseScale; // Adjust size and pulse effect
            float nukeX = centerX - nucleusSize / 2;
            float nukeY = centerY - nucleusSize / 2;

            batch.draw(nucleusTexture,
                nukeX, nukeY,
                centerX - nukeX, centerY - nukeY,
                nucleusSize, nucleusSize,
                1f, 1f,
                cellRotation,
                0, 0,
                nucleusTexture.getWidth(), nucleusTexture.getHeight(),
                false, false);
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
            amplitude = configProvider.getFloatValue("amplitude");
        } catch (NumberFormatException e) {
            amplitude = 50f;
        }
        try {
            frequency = configProvider.getFloatValue("frequency");
        } catch (NumberFormatException e) {
            frequency = 0.05f;
        }
        try {
            wiggleVelcoityMultiplier = configProvider.getIntValue("velocity");
        } catch (NumberFormatException e) {
            wiggleVelcoityMultiplier = 5;
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
    public void increasecellSize(float sizeIncrease) {
        this.cellSize += sizeIncrease;

        //increase forceCircle factors
        forceCircleSizeScalar += .125f;
        glucoseVectorScaleFactor += 10f;

        //Increase radi
        cellCircle.radius += sizeIncrease / 2;
        forceCircle.radius = cellCircle.radius * forceCircleSizeMultiplier * forceCircleSizeScalar;
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
        var newHealth = cellHealth - damage;
        if (newHealth <= 0) {
            gamePlayScreen.endGame();
        } else {
            this.cellHealth -= damage;
        }
    }

    /**
     * Decreases current atp amount in order to increase cell Health
     * Only usable if the Cell has the Mitochondria upgrade
     */
    public void healDamage() {
        if (cellATP > ATP_HEAL_COST && cellHealth < MAX_HEALTH) {
            if (cellHealth - MAX_HEALTH < AMOUNT_HEALED) {
                if (MAX_HEALTH - cellHealth < AMOUNT_HEALED) {
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
    public void setSmallSizeUpgrade(boolean hasSmallSizeUpgrade) {
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
    public void setMediumSizeUpgrade(boolean hasMediumSizeUpgrade) {
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
    public void setLargeSizeUpgrade(boolean hasLargeSizeUpgrade) {
        this.hasLargeSizeUpgrade = hasLargeSizeUpgrade;
        if ((sizeUpgradeLevel < MAX_SIZE_UPGRADES) && hasLargeSizeUpgrade) sizeUpgradeLevel++;
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
    public void setMassiveSizeUpgrade(boolean hasMassiveSizeUpgrade) {
        if ((sizeUpgradeLevel < MAX_SIZE_UPGRADES) && hasMassiveSizeUpgrade) sizeUpgradeLevel++;
        this.hasMassiveSizeUpgrade = hasMassiveSizeUpgrade;
    }

    /**
     * Get the protein synthesis multiplier
     *
     * @return
     */
    public float getProteinSynthesisMultiplier() {
        return proteinSynthesisMultiplier;
    }

    /**
     * Set the protein synthesis multiplier
     *
     * @param proteinSynthesisMultiplier
     */
    public void setProteinSynthesisMultiplier(float proteinSynthesisMultiplier) {
        this.proteinSynthesisMultiplier = proteinSynthesisMultiplier;
    }

    /**
     * Get the movement speed multiplier
     *
     * @return
     */
    public float getMovementSpeedMultiplier() {
        return movementSpeedMultiplier;
    }

    /**
     * Set the movement speed multiplier
     *
     * @param movementSpeedMultiplier
     */
    public void setMovementSpeedMultiplier(float movementSpeedMultiplier) {
        this.movementSpeedMultiplier = movementSpeedMultiplier;
    }

    /**
     * Get the canSplit boolean
     *
     * @return
     */
    public boolean canSplit() {
        return canSplit;
    }

    /**
     * Set the canSplit boolean
     *
     * @param canSplit
     */
    public void setCanSplit(boolean canSplit) {
        this.canSplit = canSplit;
    }

    /**
     * Get Size Upgrade level
     *
     * @return size upgrade level
     */
    public int getSizeUpgradeLevel() {
        return sizeUpgradeLevel;
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

    public void drawFlagellum(boolean drawFlagellum, ShapeRenderer shapeRenderer) {

        if (!drawFlagellum || FlagellumVectors.isEmpty()) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.CYAN);

        float thickness = 12.5f;

        for (int i = 0; i < FlagellumVectors.size; i++) {
            Vector2 point = new Vector2(cellCircle.x + FlagellumVectors.get(i).x, cellCircle.y + FlagellumVectors.get(i).y);

            //Draw Circles along the sine wave.
            shapeRenderer.circle(point.x, point.y, thickness);
        }

        shapeRenderer.end();
    }

    public void updateFlagellum(float deltaTime) {
        //Prevent moving flagellum if cell hasn't moved
        if (cellCircle.x == previousPosition.x && cellCircle.y == previousPosition.y) {
            return;
        }
        //Track movement for the next render cycle
        previousPosition.set(cellCircle.x, cellCircle.y);

        amplitude = 25f;
        frequency = .05f;

        FlagellumVectors.clear();

        //calculate new sin wave positions.
        for (int y = 0; y < 300f; y++) {
            float flagX = (float) (amplitude * Math.sin((y * frequency + flagTime)));
            FlagellumVectors.add(new Vector2(flagX, y - cellCircle.radius - 300)); // <-shifts flagella down, stupid calc, but it works
        }

        // Rotate the flagellum
        for (int i = 0; i < FlagellumVectors.size; i++) {
            Vector2 vector = FlagellumVectors.get(i);
            vector.rotateDeg(cellRotation);

        }

        flagTime += deltaTime * wiggleVelcoityMultiplier;
    }
}
