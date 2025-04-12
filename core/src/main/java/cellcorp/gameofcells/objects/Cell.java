package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.List;

import static com.badlogic.gdx.math.MathUtils.lerpAngleDeg;
import static java.lang.Math.abs;

/**
 * Cell Class
 *
 * Includes the data for the primary cell the player
 * Controls in the game
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 *
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class Cell {
    public static int MAX_HEALTH = 100;
    public static int MAX_ATP = 100;
    private static float CELL_SPEED = 200f; // Speed of the cell
    public static int ZERO_ATP_DAMAGE_PER_SECOND = 10;
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

    private boolean hasShownGlucosePopup = false; // If the glucose popup has been shown

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
    private float flagellaRotation = 0.0f; // Rotation angle for flagella animation
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

    //cell Texture
    private TextureRegion cellTextureRegion = null;
    private TextureRegion mitochondriaTextureRegion = null;
    private TextureRegion nucleusTextureRegion = null;
    private TextureRegion ribosomeTextureRegion = null;
    private TextureRegion flagellaTextureRegion = null;
    private Texture cellTexture = null;
    private float cellRotation = 0f;
    private float rotationSpeed = 20f;
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
    }

    /**
     * Moves the cell based on input direction as well as its collision circle,
     * diagonal movment is normalized.
     *
     * @param deltaTime - The time passed since the last frame
     * @param moveLeft  - If the cell should move left
     * @param moveRight - If the cell should move right
     * @param moveUp    - If the cell should move up
     * @param moveDown  - If the cell should move down
     */
    public void move(float deltaTime, boolean moveLeft, boolean moveRight, boolean moveUp, boolean moveDown) {
        //track these values to calculate ATP Burn.
        lastX = cellCircle.x;
        lastY = cellCircle.y;

        float targetRotation = cellRotation;

        float dx = 0;
        float dy = 0;

        if (moveLeft) {
            dx -= 1;
            targetRotation = 90f;
        }
        if (moveRight) {
            dx += 1;
            targetRotation = 270f;
        }
        if (moveUp) {
            dy += 1;
            targetRotation = 0f;
        }
        if (moveDown) {
            dy -= 1;
            targetRotation = 180f;

        }

        if(moveUp  && moveRight) {
            targetRotation = 315;
        }

        if(moveUp  && moveLeft) {
            targetRotation = 45;
        }

        if(moveDown && moveRight) {
            targetRotation = 225;
        }

        if(moveDown && moveLeft) {
            targetRotation = 135;
        }

        cellRotation = lerpAngleDeg(cellRotation, targetRotation, deltaTime * rotationSpeed);

        // Normalize movement along diagonal
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx /= length;
            dy /= length;
        }

        cellCircle.x += dx * CELL_SPEED * deltaTime;
        cellCircle.y += dy * CELL_SPEED * deltaTime;

        setCellForceCircle(cellCircle.x, cellCircle.y);

        if (moveLeft || moveRight || moveUp || moveDown) {
            gamePlayScreen.stats.distanceMoved += CELL_SPEED * deltaTime;
        }
    }

    private void rotateCell(float deltaTime,boolean direction) {

    }

    /**
     * ATP Loss Calculation
     *
     * Returns ATPburn based on movement upgrades and size.
     *
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
            currentATPLost += deltaTime * ((2*totalATPLossFactor));
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
     *
     +----+---------+------+-----+------+------+------+
     |    | Type    | BBR  | Mit | Ribo | Flag | Nuke |
     +====+=========+======+=====+=====+======+======+=
     |  0 | none    |  11  |  -  |  -   |  -   |  -   |
     +----+---------+------+-----+-----+------+-------+
     |  1 | small   |  10  |  9  |  -   |  -   |  -   |
     +----+---------+------+-----+-----+------+-------+
     |  2 | medium  |  9   |  8  |  7   |  -   |  -   |
     +----+---------+------+-----+-----+------+-------+
     |  3 | large   |  8   |  7  |  6   |  5   |  -   |
     +----+---------+------+-----+-----+------+-------+
     |  4 | massive |  7   |  6  |  5   |  4   |  3   |
     +----+---------+------+-----+-----+------+-------+
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
        if(cellTextureRegion == null) {
            cellTexture = assetManager.get(AssetFileNames.CELL, Texture.class);
            cellTextureRegion = new TextureRegion(cellTexture);
        }
        batch.begin();
        batch.draw(cellTextureRegion, bottomLeftX, bottomLeftY, cellSize/2, cellSize/2, cellSize,cellSize,1f,1f,cellRotation);



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
        if(wasAtpBurnedThisFrame) {
            wasAtpBurnedThisFrame = false;
        }

        //tracked for ATP and game over stats.
        totalDistanceMoved += distanceMovedSinceLastTick;
        distanceMovedSinceLastTick = 0f;

        // Update animations
        if (hasFlagella) {
            flagellaRotation += 10 * deltaTimeSeconds; // Adjust rotation speed as needed
        }

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
        gamePlayScreen.stats.organellesPurchased = (int)organellesPurchased;
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
    }

    private void damageIfZeroATP(float deltaTimeSeconds) {
        var damage = ZERO_ATP_DAMAGE_PER_SECOND * deltaTimeSeconds;
        if (getCellATP() > 0) {
            // Reset damage counter if we ever have > 0 ATP
            damageTimer = 0f;
            damageCounter = 0f;
        } else if (damageTimer > ZERO_ATP_DAMAGE_INCREMENT_SECONDS && damageCounter > 1) {
            applyDamage((int)damageCounter);
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


            if(mitochondriaTextureRegion == null ) {
                mitochondriaTextureRegion = new TextureRegion(mitochondriaTexture);
            }

            batch.draw(mitochondriaTextureRegion, mitoX, mitoY, centerX - mitoX, centerY - mitoY, mitochondriaSize,mitochondriaSize,1f,1f,cellRotation);

        }

        // Draw ribosomes (top-left quadrant)
        if (hasRibosomes) {
            var ribosomeTexture = assetManager.get(AssetFileNames.RIBOSOME_ICON, Texture.class);
            float ribosomeSize = cellSize * 0.2f; // Adjust size as needed

            if(ribosomeTextureRegion == null ) {
                ribosomeTextureRegion = new TextureRegion(ribosomeTexture);
            }

            float riboX = centerX - ribosomeSize / 2 - cellRadius * 0.7f;
            float riboY = centerY - ribosomeSize / 2 + cellRadius * 0.3f;
            batch.draw(ribosomeTextureRegion, riboX, riboY, centerX - riboX, centerY - riboY, ribosomeSize,ribosomeSize,1f,1f,cellRotation);

//            batch.draw(ribosomeTexture,
//                centerX - cellRadius * 0.7f,
//                centerY + cellRadius * 0.3f,
//                ribosomeSize, ribosomeSize);
        }

        // Draw flagella (right edge)
        if (hasFlagella) {
            var flagellaTexture = assetManager.get(AssetFileNames.FLAGELLA_ICON, Texture.class);

            // New calculations for better flagella positioning
            float flagellaLength = cellSize * 0.5f;// Adjust length as needed
            float flagellaWidth = cellSize * 0.3f; // Adjust width as needed
            float pivotX = centerX + cellRadius; // Start at cell edge
            float pivotY = centerY; // Center vertically

            // Calculate position so only the tail sticks out
            float drawX = pivotX - flagellaLength * 0.7f; // Adjust position
            float drawY = pivotY - flagellaWidth / 2; // Center vertically


            batch.draw(flagellaTexture,
                    drawX, drawY, // Position
                    flagellaLength * 0.7f, flagellaWidth/2, // Size
                    flagellaLength, flagellaWidth, // Size
                    1, 1, // Scale
                    flagellaRotation, // Rotation
                    0, 0, // Texture region
                    flagellaTexture.getWidth(), flagellaTexture.getHeight(), // Texture region size
                    true, // Flip X
                    false); // Flip Y
        }

        // Draw nucleus (center with pulse effect)
        if (hasNucleus) {
            var nucleusTexture = assetManager.get(AssetFileNames.NUCLEUS_ICON, Texture.class);
            float nucleusSize = cellSize * 0.4f * pulseScale; // Adjust size and pulse effect

            if(nucleusTextureRegion == null ) {
                nucleusTextureRegion = new TextureRegion(nucleusTexture);
            }

            float nukeX = centerX - nucleusSize / 2;
            float nukeY = centerY - nucleusSize / 2;
            batch.draw(nucleusTextureRegion, nukeX, nukeY, centerX-nukeX, centerY-nukeY, nucleusSize,nucleusSize,1f,1f,cellRotation);

//            batch.draw(nucleusTexture,
//                centerX - nucleusSize / 2,
//                centerY - nucleusSize / 2,
//                nucleusSize, nucleusSize);
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
     *Returns the Cell BoundingCircle
     */
    public Circle getCircle() {
        return cellCircle;
    }

    /**
     * Adds ATP
     *
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
     * @param sizeIncrease - The amount to increase the cell by.
     */
    public void increasecellSize(float sizeIncrease) {
        this.cellSize += sizeIncrease;

        //increase forceCircle factors
        forceCircleSizeScalar += .125f;
        glucoseVectorScaleFactor +=10f;

        //Increase radi
        cellCircle.radius += sizeIncrease / 2;
        forceCircle.radius = cellCircle.radius *  forceCircleSizeMultiplier * forceCircleSizeScalar;
    }

    /**
     * Check if the glucose popup has been shown
     */
    public boolean hasShownGlucosePopup() {
        return hasShownGlucosePopup;
    }

    /**
     * Set the glucose popup as shown
     */
    public void setHasShownGlucosePopup(boolean hasShownGlucosePopup) {
        this.hasShownGlucosePopup = hasShownGlucosePopup;
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
        if((organelleUpgradeLevel < MAX_ORGANELLE_UPGRADES) && hasFlagella) organelleUpgradeLevel++;
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
        if((organelleUpgradeLevel < MAX_ORGANELLE_UPGRADES) && hasNucleus) organelleUpgradeLevel++;
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
        if((sizeUpgradeLevel < MAX_SIZE_UPGRADES) && hasSmallSizeUpgrade ) sizeUpgradeLevel++;
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
        if((sizeUpgradeLevel < MAX_SIZE_UPGRADES) && hasMediumSizeUpgrade) sizeUpgradeLevel++;
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
        if((sizeUpgradeLevel < MAX_SIZE_UPGRADES) && hasLargeSizeUpgrade) sizeUpgradeLevel++;
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
        if((sizeUpgradeLevel < MAX_SIZE_UPGRADES)&& hasMassiveSizeUpgrade ) sizeUpgradeLevel++;
        this.hasMassiveSizeUpgrade = hasMassiveSizeUpgrade;
    }

    /**
     * Get the protein synthesis multiplier
     * @return
     */
    public float getProteinSynthesisMultiplier() {
        return proteinSynthesisMultiplier;
    }

    /**
     * Set the protein synthesis multiplier
     * @param proteinSynthesisMultiplier
     */
    public void setProteinSynthesisMultiplier(float proteinSynthesisMultiplier) {
        this.proteinSynthesisMultiplier = proteinSynthesisMultiplier;
    }

    /**
     * Get the movement speed multiplier
     * @return
     */
    public float getMovementSpeedMultiplier() {
        return movementSpeedMultiplier;
    }

    /**
     * Set the movement speed multiplier
     * @param movementSpeedMultiplier
     */
    public void setMovementSpeedMultiplier(float movementSpeedMultiplier) {
        this.movementSpeedMultiplier = movementSpeedMultiplier;
    }

    /**
     * Get the canSplit boolean
     * @return
     */
    public boolean canSplit() {
        return canSplit;
    }

    /**
     * Set the canSplit boolean
     * @param canSplit
     */
    public void setCanSplit(boolean canSplit) {
        this.canSplit = canSplit;
    }

    /**
     * Get Size Upgrade level
     * @return size upgrade level
     */
    public int getSizeUpgradeLevel() {
        return sizeUpgradeLevel;
    }

    /**
     * Get Organelle Upgrade level
     * @return Organelle upgrade level
     */
    public int getOrganelleUpgradeLevel() {
        return organelleUpgradeLevel;
    }

    /**
     * Current ATP Lost Getter
     * @return the Current ammount of atp lost.
     */
    public float getCurrentATPLost() {
        return currentATPLost;
    }

    /**
     * Loss Factor getter
     * @return The loss factor.
     */
    public float getTotalATPLossFactor() {
        return totalATPLossFactor;
    }

    /**
     * ATP flag getter
     *
     * Tracks if ATP burn occured this render cycle.
     *
     * @return the state of the ATP flag
     */
    public boolean isWasAtpBurnedThisFrame() {
        return wasAtpBurnedThisFrame;
    }

    /**
     * CurrTimeTaken Getter
     *
     * Tracks time taken for the current ATP loss.
     * @return The time taken for atp loss.
     */
    public float getCurrTimeTakenforATPLoss() {
        return currTimeTakenforATPLoss;
    }

    /**
     * LastTimeTaken Getter
     *
     * The time taken for the previous ATP loss.
     * @return THe previous time taken for atp loss.
     */
    public float getLastTimeTakenforATPLoss() {
        return lastTimeTakenforATPLoss;
    }

    /**
     * cellForceCircleUpdater
     *
     * Updates the position of the forceCircle with new values.
     * @param newX The new X value
     * @param newY The new Y value
     */
    private void setCellForceCircle(float newX, float newY) {
        forceCircle.setX(newX);
        forceCircle.setY(newY);
    }

    /**
     * Force Circle getter
     * @return The forceCricle
     */
    public Circle getForceCircle() {
        return forceCircle;
    }

    /**
     * Getter For the GlucoseVectorScaleFactor
     * @return How much to scale glucose pushing.
     */
    public float getGlucoseVectorScaleFactor() {
        return glucoseVectorScaleFactor;
    }
}
