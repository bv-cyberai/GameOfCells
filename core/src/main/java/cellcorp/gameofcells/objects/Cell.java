package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import cellcorp.gameofcells.AssetFileNames;
import com.badlogic.gdx.math.Circle;

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

    float cellPositionX;
    float cellPositionY;
    float cellDiameter;
    private final float cellSpeed = 200f; // Speed of the cell

    private int cellHealth;
    private int cellATP;
    private int maxHealth;
    private int maxATP;
    private Circle cellCircle;


    private Circle collisionCircle = null;

    public Cell(AssetManager assetManager) {
        this.assetManager = assetManager;

        // cellPositionX = 500;
        // cellPositionY = 300;
        // cellDiameter = 200;
        cellHealth = 100;
        maxHealth = 100;
        
        cellATP = 30;
        maxATP = 100; // Alpha is actually 99, but thats painful.
        
        cellCircle = new Circle(new Vector2(500,300),100);
        // cellCircle.set(cellPositionX, cellPositionY, cellDiameter/2);
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
        this.cellPositionX = x;
        this.cellPositionY = y;
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

        
    }

    /**
     * Get Cell Position X
     * 
     * @return cellPositionX
     */
    public float getCellPositionX() {
        return cellCircle.x;
    }

    /**
     * Get Cell Position Y
     * 
     * @return cellPositionY
     */
    public float getCellPositionY() {
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

    public void increaseCellDiameter(float diameterIncrease) {
        // this.cellDiameter += diameterIncrease;
        // updateCollisionCircle();
        cellCircle.radius += diameterIncrease;
    }

    /**
     * Calculate the cell collision circle based on the cell's radius and position.
     */
    // private void updateCollisionCircle() {
    //     this.cellCircle.set();
    //     this.cellCircle = new Circle(cellPositionX, cellPositionY, cellDiameter / 2);
    // }
}
