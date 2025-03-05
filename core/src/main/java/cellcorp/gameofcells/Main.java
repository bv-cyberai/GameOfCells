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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.inputproviders.DefaultInputProvider;
import cellcorp.gameofcells.inputproviders.InputProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;

public class Main extends Game {
    // Constants - These were the sizes of the beta, and have been noted here for
    // reference.
    // WORLD_WIDTH = 1200
    // WORLD_HEIGHT = 800

    /// Loads assets during game creation,
    /// then provides loaded assets to draw code, using [AssetManager#get(String)]
    private final AssetManager assetManager;

    /// Gets information about inputs, like held-down keys.
    /// Use this instead of `Gdx.input`, to avoid crashing tests.
    private final InputProvider inputProvider;

    // Camera/Viewport - These can be shared by screens/don't need to be Fit
    // viewport but its not a bad choice per se.
    private OrthographicCamera camera;
    private FitViewport viewport;

    /// Create a game instance with default providers.
    public Main() {
        this(new AssetManager(), new DefaultInputProvider());
    }

    /// Create a game instance.
    /// @param assetManager Loads assets. Use `mock(AssetManager.class)` in test code.
    /// @param inputProvider Input provider. Use FakeInputProvider in test code.
    public Main(AssetManager assetManager, InputProvider inputProvider) {
        this.assetManager = assetManager;
        this.inputProvider = inputProvider;
    }

    @Override
    public void create() {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        // Load up the assets, blocking until they're loaded.
        // The asset manager expects the asset's file name,
        // and the class of the asset to load.
        assetManager.load("Cell.png", Texture.class);
        assetManager.finishLoading();

        // May need to set to gameScreenManager at somepoint.
        setScreen(new GamePlayScreen(this, assetManager, inputProvider, camera, viewport));
    }

    @Override
    public void dispose () {
        super.dispose();
        assetManager.dispose();
    }

}
