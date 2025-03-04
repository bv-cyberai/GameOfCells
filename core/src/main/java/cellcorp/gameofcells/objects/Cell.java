package cellcorp.gameofcells.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    Texture cellTexture;
    float cellPositionX;
    float cellPositionY;
    float cellWidth;
    float cellHeight;

    public Cell() {
        cellPositionX = 500;
        cellPositionY = 300;
        cellWidth = 200;
        cellHeight = 200;
        cellTexture = new Texture("Cell.png");
    }

    /**
     * Draw
     * @param batch - The passed spritebatch.
     */
    public void draw(SpriteBatch batch) {
        batch.draw(cellTexture, cellPositionX, cellPositionY, cellWidth, cellHeight);
    }

    /**
     * Get Cell Texture
     * @return cellTexture
     */
    public Texture getCellTexture() {
        return cellTexture;
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

    /**
     * Dispose
     */
    public void dispose() {
        cellTexture.dispose();
    }

}
