package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
/**
* Cell Class
*
* Includes the data for the primary cell the player
* Controls in the game
* @author Brendon Vinyard / vineyabn207
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

    public Cell(AssetManager assetManager) {
        this.assetManager = assetManager;

        cellPositionX = 500;
        cellPositionY = 300;
        cellWidth = 200;
        cellHeight = 200;
    }
    /**
     * Draw
     * @param batch - The passed spritebatch.
     */
    public void draw(SpriteBatch batch) {
        // Get the already-loaded cell texture
        // The asset manager expects the asset's file name,
        // and the class of the asset to load.
        var cellTexture = assetManager.get("Cell.png", Texture.class);
        assert (cellTexture != null);
        batch.draw(cellTexture, cellPositionX, cellPositionY, cellWidth, cellHeight);
    }

    /**
     * Dispose
     */
    public void dispose() {
        assetManager.unload("Cell.png");
    }
}
