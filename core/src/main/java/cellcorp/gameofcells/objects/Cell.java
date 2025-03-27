package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import cellcorp.gameofcells.AssetFileNames;

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
    private final AssetManager assetManager;

    private final float cellSpeed = 200f; // Speed of the cell

    private int cellHealth;
    private int cellATP;
    private int maxHealth;
    private int maxATP;
    private Circle cellCircle;

    private boolean hasShownGlucosePopup = false; // If the glucose popup has been shown
    private boolean hasMitochondria = false; // Whether the cell has the mitochondria upgrade

    public Cell(AssetManager assetManager) {
        this.assetManager = assetManager;
        cellHealth = 100;
        maxHealth = 100;
        
        cellATP = 30;
        maxATP = 100; // Alpha is actually 99, but thats painful.
        
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
            cellCircle.x -= cellSpeed * deltaTime;
        if (moveRight)
            cellCircle.x += cellSpeed * deltaTime;
        if (moveUp)
            cellCircle.y += cellSpeed * deltaTime;
        if (moveDown)
            cellCircle.y -= cellSpeed * deltaTime;
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
        return maxHealth;
    }

    /**
     * MAX ATP Getter
     * 
     * @return Cell Max ATP
     */
    public int getMaxATP() {
        return maxATP;
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
        cellATP = Math.min(cellATP + increaseAmount, maxATP);
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

    public void reduceHealth(int damage) {
        this.cellHealth -= damage;
    }
}
