package cellcorp.gameofcells;

import com.badlogic.gdx.graphics.Texture;
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
    Texture cellTexture;
    float cellPositionX;
    float cellPositionY;

    public Texture getCellTexture() {
        return cellTexture;
    }
}
