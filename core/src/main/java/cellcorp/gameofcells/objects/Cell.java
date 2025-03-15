package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import cellcorp.gameofcells.AssetFileNames;
/**
* Cell Class
*
* Includes the data for the primary cell the player
* Controls in the game
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
    float cellWidth;
    float cellHeight;
    private final float cellSpeed = 200f;   // Speed of the cell

    private int cellHealth;
    private int cellATP;
    private int maxHealth;
    private int maxATP;

    public Cell(AssetManager assetManager) {
        this.assetManager = assetManager;

        cellPositionX = 500;
        cellPositionY = 300;
        cellWidth = 200;
        cellHeight = 200;

        cellHealth = 100;
        cellATP = 0;
        maxHealth = 100;
        maxATP = 100;
    }

    /**
     * Moves the cell based on input direction
     * 
     * @param deltaTime - The time passed since the last frame
     * @param moveLeft - If the cell should move left
     * @param moveRight - If the cell should move right
     * @param moveUp - If the cell should move up
     * @param moveDown - If the cell should move down
     */
    public void move(float deltaTime, boolean moveLeft, boolean moveRight, boolean moveUp, boolean moveDown) {
        if (moveLeft) cellPositionX -= cellSpeed * deltaTime;
        if (moveRight) cellPositionX += cellSpeed * deltaTime;
        if (moveUp) cellPositionY += cellSpeed * deltaTime;
        if (moveDown) cellPositionY -= cellSpeed * deltaTime;
    }

    /**
     * Draw
     * @param batch - The passed spritebatch.
     */
    public void draw(SpriteBatch batch) {
        // Get the already-loaded cell texture
        // The asset manager expects the asset's file name,
        // and the class of the asset to load.
        var cellTexture = assetManager.get(AssetFileNames.CELL, Texture.class);
        assert (cellTexture != null);
        batch.draw(cellTexture, cellPositionX, cellPositionY, cellWidth, cellHeight);
    }

    /**
     * Get Cell Position X
     * @return cellPositionX
     */
    public float getCellPositionX() {
        return cellPositionX;
    }

    /**
     * Get Cell Position Y
     * @return cellPositionY
     */
    public float getCellPositionY() {
        return cellPositionY;
    }

    /**
     * Get Cell Width
     * @return cellWidth
     */
    public float getCellWidth() {
        return cellWidth;
    }

    /**
     * Get Cell Height
     * @return cellHeight
     */
    public float getCellHeight() {
        return cellHeight;
    }

    public int getCellHealth() {
        return cellHealth;
    }

    public int getCellATP() {
        return cellATP;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMaxATP() {
        return maxATP;
    }


    /**
     * Dispose
     */
    public void dispose() {
        assetManager.unload(AssetFileNames.CELL);
    }


}
