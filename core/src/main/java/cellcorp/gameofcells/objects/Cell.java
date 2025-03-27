package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;

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
    public static  int MAX_HEALTH = 100;
    public static  int MAX_ATP = 100;
    private static float CELL_SPEED = 200f; // Speed of the cell

    private final AssetManager assetManager;
    private final ConfigProvider configProvider;
    private final GamePlayScreen gamePlayScreen;

    private int cellHealth;
    private int cellATP;
    private final Circle cellCircle;

    private boolean hasShownGlucosePopup = false; // If the glucose popup has been shown
    private boolean hasMitochondria = false; // Whether the cell has the mitochondria upgrade

    public Cell(GamePlayScreen gamePlayScreen, AssetManager assetManager, ConfigProvider configProvider) {
        this.assetManager = assetManager;
        this.gamePlayScreen = gamePlayScreen;
        this.configProvider = configProvider;
        setUserConfigOrDefault();

        
        cellCircle = new Circle(new Vector2(0, 0),100);
    }

    /**
     * Moves the cell based on input direction as well as its collision circle
     *
     * @param deltaTime - The time passed since the last frame
     * @param moveLeft  - If the cell should move left
     * @param moveRight - If the cell should move right
     * @param moveUp    - If the cell should move up
     * @param moveDown  - If the cell should move down
     */
    public void move(float deltaTime, boolean moveLeft, boolean moveRight, boolean moveUp, boolean moveDown) {
        if (moveLeft)
            cellCircle.x -= CELL_SPEED * deltaTime;
        if (moveRight)
            cellCircle.x += CELL_SPEED * deltaTime;
        if (moveUp)
            cellCircle.y += CELL_SPEED * deltaTime;
        if (moveDown)
            cellCircle.y -= CELL_SPEED * deltaTime;
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
    public void draw(SpriteBatch batch) {
        // Draw cell centered around its position.

        float bottomLeftX = cellCircle.x - (cellCircle.radius);
        float bottomLeftY = cellCircle.y - (cellCircle.radius);

        // Get the already-loaded cell texture
        // The asset manager expects the asset's file name,
        // and the class of the asset to load.
        var cellTexture = assetManager.get(AssetFileNames.CELL, Texture.class);
        assert (cellTexture != null);
        batch.draw(cellTexture, bottomLeftX, bottomLeftY, cellCircle.radius*2, cellCircle.radius*2);

        // Draw mitochondria if the upgrade is purchased
        if (hasMitochondria) {
            var mitochondriaTexture = assetManager.get(AssetFileNames.MITOCHONDRIA_ICON, Texture.class);
            assert (mitochondriaTexture != null);

            // Draw mitochondria inside the cell
            float mitochondriaSize = (cellCircle.radius*2) * 0.2f; // Make mitochondria smaller (30% of cell size)
            float mitochondriaX = cellCircle.x - ((cellCircle.radius*2) / 4); // Position in the bottom-left of the cell
            float mitochondriaY = cellCircle.y - (cellCircle.radius*2 / 4); // Position in the bottom-left of the cell
            batch.draw(mitochondriaTexture, mitochondriaX, mitochondriaY, mitochondriaSize, mitochondriaSize);
        }


    }

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
     * @return cellDiameter
     */
    public float getCellDiameter() {
        return cellCircle.radius*2;
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
     *
     *
     */
    public Circle getcellCircle() {
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
     * Increase the cell diameter
     *
     * @param diameterIncrease - The amount to increase the cell by.
     */
    public void increaseCellDiameter(float diameterIncrease) {
        cellCircle.radius += diameterIncrease/2;
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
     * Check if the cell has the mitochondria upgrade
     */
    public boolean hasMitochondria() {
        return hasMitochondria;
    }

    /**
     * Set whether the cell has the mitochondria upgrade
     */
    public void setHasMitochondria(boolean hasMitochondria) {
        this.hasMitochondria = hasMitochondria;
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
}
