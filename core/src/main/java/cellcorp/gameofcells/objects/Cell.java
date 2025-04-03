package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;

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

    // May change, but used to ensure that invalid case selection is not hit.
    private static final int MAX_SIZE_UPGRADES = 4;
    private static final int MAX_OGRANELLE_UPGRADES = 4;
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

    // Energy Use tracking
    private float lastX;
    private float lastY;
    private int sizeUpgradeLevel; // size upgrades 0-4
    private int organelleUpgradeLevel; //organelle upgrades 0-4
    private float currentATPLost; // used to track atp loss up to 1
    private float totalATPLossFactor; //tracks total atp burn
    private float distanceMovedSinceLastTick;
    private boolean atpTickFlag; //tracks if ATP has been burnt, mostly for testing
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
        atpTickFlag = false;

        cellCircle = new Circle(new Vector2(0, 0), cellSize / 2);
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

        float dx = 0;
        float dy = 0;

        if (moveLeft) {
            dx -= 1;
        }
        if (moveRight) {
            dx += 1;
        }
        if (moveUp) {
            dy += 1;
        }
        if (moveDown) {
            dy -= 1;
        }
        // Normalize movement along diagonal
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx /= length;
            dy /= length;
        }

        cellCircle.x += dx * CELL_SPEED * deltaTime;
        cellCircle.y += dy * CELL_SPEED * deltaTime;
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
     */
    /**
     +----+---------+------+-----+-----+------+------+------+
     |    | Type    | Size | BBR | Mit | Ribo | Flag | Nuke |
     +====+=========+======+=====+=====+======+======+======+
     |  0 | none    |  0   |  11 |  -  |  -   |  -   |  -   |
     +----+---------+------+-----+-----+------+------+------+
     |  1 | small   |  1   |  10 |  9  |  -   |  -   |  -   |
     +----+---------+------+-----+-----+------+------+------+
     |  2 | medium  |  2   |  9  |  8  |  7   |  -   |  -   |
     +----+---------+------+-----+-----+------+------+------+
     |  3 | large   |  3   |  8  |  7  |  6   |  5   |  -   |
     +----+---------+------+-----+-----+------+------+------+
     |  4 | massive |  4   |  7  |  6  |  5   |  4   |  3   |
     +----+---------+------+-----+-----+------+------+------+
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
        var cellTexture = assetManager.get(AssetFileNames.CELL, Texture.class);

        batch.begin();
        batch.draw(cellTexture, bottomLeftX, bottomLeftY, cellSize, cellSize);

        drawOrganelles(batch);

        batch.end();
        if (GamePlayScreen.DEBUG_DRAW_ENABLED) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.circle(cellCircle.x, cellCircle.y, cellCircle.radius);
            shapeRenderer.end();
        }
    }

    public void update(float delta) {
        //Recalculate loss factor.
        totalATPLossFactor = setTotalLossFactor();
        calculateATPLoss(delta);

        //Used for testing. Set when 1 ATP burn has occurred.
        if(atpTickFlag) {
            atpTickFlag = false;
        }

        //tracked for ATP and game over stats.
        totalDistanceMoved += distanceMovedSinceLastTick;
        distanceMovedSinceLastTick = 0f;

        // Update animations
        if (hasFlagella) {
            flagellaRotation += 100 * delta; // Adjust rotation speed as needed
        }

        if (hasNucleus) {
            nucleusPulse += delta; // Adjust pulse speed as needed
            pulseScale = 1.0f + 0.1f * MathUtils.sin(nucleusPulse * 2.0f); // Adjust pulse effect
        }

        //When ATP loss accumulates above 1, subtract and reset currentATPLost.
        if (currentATPLost >= 1) {
            if (cellATP > 0) {
                cellATP -= 1;
                atpTickFlag = true;
                lastTimeTakenforATPLoss = currTimeTakenforATPLoss;
                currTimeTakenforATPLoss = 0f;
            }
            currentATPLost = 0;
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
            batch.draw(mitochondriaTexture,
                centerX - cellRadius * 0.6f,
                centerY - cellRadius * 0.6f,
                mitochondriaSize, mitochondriaSize);
        }

        // Draw ribosomes (top-left quadrant)
        if (hasRibosomes) {
            var ribosomeTexture = assetManager.get(AssetFileNames.RIBOSOME_ICON, Texture.class);
            float ribosomeSize = cellSize * 0.2f; // Adjust size as needed
            batch.draw(ribosomeTexture,
                centerX - cellRadius * 0.7f,
                centerY + cellRadius * 0.3f,
                ribosomeSize, ribosomeSize);
        }

        // Draw flagella (right edge)
        if (hasFlagella) {
            var flagellaTexture = assetManager.get(AssetFileNames.FLAGELLA_ICON, Texture.class);
            batch.draw(flagellaTexture,
                centerX + cellRadius * 0.8f, centerY - 5,
                10, 5, // Origin for rotation
                40, 10, // Size
                1, 1, // Scale
                flagellaRotation,
                0, 0,
                flagellaTexture.getWidth(), flagellaTexture.getHeight(),
                false, false);
        }

        // Draw nucleus (center with pulse effect)
        if (hasNucleus) {
            var nucleusTexture = assetManager.get(AssetFileNames.NUCLEUS_ICON, Texture.class);
            float nucleusSize = cellSize * 0.4f * pulseScale; // Adjust size and pulse effect
            batch.draw(nucleusTexture,
                centerX - nucleusSize / 2,
                centerY - nucleusSize / 2,
                nucleusSize, nucleusSize);
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
    public int getcellSize() {
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
     * ATP Getter
     *
     * @return ATP
     */
    public int getCellATP() {
        return cellATP;
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
     * Increases the cell size
     * @param sizeIncrease - The amount to increase the cell by.
     */
    public void increasecellSize(float sizeIncrease) {
        this.cellSize += sizeIncrease;
        cellCircle.radius += sizeIncrease / 2;
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
        if ((organelleUpgradeLevel < MAX_OGRANELLE_UPGRADES) && hasMitochondria) organelleUpgradeLevel++;
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
        if ((organelleUpgradeLevel < MAX_OGRANELLE_UPGRADES) && hasRibosomes) organelleUpgradeLevel++;
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
        if((organelleUpgradeLevel < MAX_OGRANELLE_UPGRADES) && hasFlagella) organelleUpgradeLevel++;
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
        if((organelleUpgradeLevel < MAX_OGRANELLE_UPGRADES) && hasNucleus) organelleUpgradeLevel++;
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

    public void setCellATP(int cellATP) {
        this.cellATP = cellATP;
    }

    public void setCurrentATPLost(float currentATPLost) {
        this.currentATPLost = currentATPLost;
    }

    public boolean isAtpTickFlag() {
        return atpTickFlag;
    }

    public float getCurrTimeTakenforATPLoss() {
        return currTimeTakenforATPLoss;
    }

    public float getLastTimeTakenforATPLoss() {
        return lastTimeTakenforATPLoss;
    }
}
