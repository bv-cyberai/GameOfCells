package cellcorp.gameofcells;

/**
* Game Class
*
* The base game class for GameOfCells
*
* @author Brendon Vinyard / vineyabn207
* @author Andrew Sennoga-Kimuli / sennogat106
* @author Mark Murphy / murphyml207
* @author Tim Davey / daveytj206
*
* @date 02/18/2025
* @course CIS 405
* @assignment GameOfCells
*/

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.screens.GamePlayScreen;

public class Main extends Game {
    // Constants - These were the sizes of the beta, and have been noted here for
    // reference.
    // WORLD_WIDTH = 1200
    // WORLD_HEIGHT = 800

    // Camera/Viewport - These can be shared by screens/don't need to be Fit
    // viewport but its not a bad choice per se.
    private OrthographicCamera camera;
    private FitViewport viewport;

    @Override
    public void create() {
    
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        // May need to set to gameScreenManager at somepoint.
        setScreen(new GamePlayScreen(this, camera, viewport));


    }
}
