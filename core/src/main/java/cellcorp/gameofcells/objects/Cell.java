package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

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

    private static final float WIDTH = 200;
    private static final float HEIGHT = 200;

    private float cellPositionX;
    private float cellPositionY;
    private final float cellSpeed = 200f; // Speed of the cell

    private int cellHealth;
    private int cellATP;
    private int maxHealth;
    private int maxATP;
    private Circle cellCircle;


    public Cell(AssetManager assetManager) {
        this.assetManager = assetManager;

        cellPositionX = 500;
        cellPositionY = 300;
        cellHealth = 100;
        cellATP = 30; // starting value in alpha.
        maxHealth = 100;
        maxATP = 100; // Alpha is actually 99, but thats painful.
        cellCircle = new Circle();
        cellCircle.set(cellPositionX, cellPositionY, WIDTH/2);
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
            cellPositionX -= cellSpeed * deltaTime;
        if (moveRight)
            cellPositionX += cellSpeed * deltaTime;
        if (moveUp)
            cellPositionY += cellSpeed * deltaTime;
        if (moveDown)
            cellPositionY -= cellSpeed * deltaTime;
        updateCollisionCircle();
    }

    /**
     * Draw
     * 
     * @param batch - The passed spritebatch.
     */
    public void draw(SpriteBatch batch) {

        // Draw cell centered around its position.
        float topLeftX = cellPositionX - (WIDTH / 2);
        float topLeftY = cellPositionY - (HEIGHT / 2);

        // Get the already-loaded cell texture
        // The asset manager expects the asset's file name,
        // and the class of the asset to load.
        var cellTexture = assetManager.get(AssetFileNames.CELL, Texture.class);
        assert (cellTexture != null);

        batch.begin();
        // x, y are the _top-left_ of the drawn sprite
        batch.draw(cellTexture, topLeftX, topLeftY, WIDTH, HEIGHT);
        batch.end();
    }

    /**
     * Get Cell Position X
     * 
     * @return cellPositionX
     */
    public float getCellPositionX() {
        return cellPositionX;
    }

    /**
     * Get Cell Position Y
     * 
     * @return cellPositionY
     */
    public float getCellPositionY() {
        return cellPositionY;
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
        cellATP = cellATP + increaseAmount;
    }
    /**
     * Adds ATP
     *
     * decreases ATP
     */
    public void removeCellATP(int decreaseAmount) {
        cellATP = cellATP - decreaseAmount;

    }

    /**
     * Calculate the cell collision circle based on the cell's radius and position.
     */
    private void updateCollisionCircle() {
        this.cellCircle = new Circle(cellPositionX, cellPositionY, WIDTH / 2);
    }
}
