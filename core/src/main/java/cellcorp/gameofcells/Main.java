package cellcorp.gameofcells;

/**
 * Game Class
 * <p>
 * The base game class for GameOfCells
 *
 * @author Brendon Vinyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.providers.DefaultGraphicsProvider;
import cellcorp.gameofcells.providers.DefaultInputProvider;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import cellcorp.gameofcells.screens.GameOfCellsScreen;
import cellcorp.gameofcells.screens.MainMenuScreen;

public class Main implements ApplicationListener {

    /**
     * Width of the view rectangle
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_WIDTH = 1920;
    /**
     * Width of the view rectangle
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_HEIGHT = 1080;
    /**
     * The currently-shown screen.
     */
    private GameOfCellsScreen screen;

    /**
     * Loads assets during game creation,
     * then provides loaded assets to draw code, using [AssetManager#get(String)]
     */
    private final AssetManager assetManager;

    /**
     * Gets information about inputs, like held-down keys.
     * Use this instead of `Gdx.input`, to avoid crashing tests.
     */
    private final InputProvider inputProvider;

    /**
     * Gets information about graphics. Use this instead of `Gdx.graphics` in game code,
     * to avoid crashing tests.
     */
    private final GraphicsProvider graphicsProvider;


    // ==== The Camera / Viewport Regime ====
    //  (Mark is 95% sure the following is correct, from research and review of the classes' code):
    // The LibGDX 2d (= orthographic) `Camera` is responsible for:
    // - Holding a "view rectangle":
    //      A rectangle defined in world units, which is the region of the world that is drawn.
    //      Stored in the camera as a center-point `position`
    //          and a pair `viewportWidth, viewportHeight`
    //      The names `viewportWidth` and `viewportHeight` are historical, and somewhat misleading.
    //      https://stackoverflow.com/questions/40059360/difference-between-viewport-and-camera-in-libgdx
    // - Providing a `projectionMatrix` to `SpriteBatch` and `ShapeRenderer`
    //      If the view rectangle is `(0, 0) .. (1000, 1000)`,
    //      the `SpriteBatch` can draw anywhere in that range, and it will be drawn to screen.
    //
    // The LibGDX `Viewport` (_not_ the same as OpenGL viewport) is responsible for:
    // - Storing a `worldWidth` and `worldHeight`
    //      These are also questionably-named. They represent the same thing as the camera's view rectangle.
    //      When `viewport.apply()` is called in each `draw()` call,
    //          the viewport updates the width and height of the camera's view rectangle
    //          to match these values.
    // - Fitting the camera's view rectangle to whatever the actual screen size is
    //      To do this, it uses the `screenX, screenY, screenWidth, screenHeight` fields,
    //      which are updated by calling `viewport.update()`
    //
    // Takeaways:
    // - We shouldn't use `viewport.setWorldWidth()` or `viewport.setWorldHeight()`,
    //      unless we want to change the _size_ of the camera's view rectangle.
    //      We usually don't want to do that.
    // - Because `viewport.worldWidth` overrides `camera.viewportWidth` every time `viewport.apply()` is called,
    //      if we _do_ want to change it, we should always call `viewport.setWorld____()`,
    //      instead of changing it directly
    // - To move the position of the camera's view rectangle,
    //      we should call `camera.position.set(...)`
    // - For drawing HUD and menu screens, we can use a separate (viewport, camera) pair from the game screen.
    //      If we construct the viewport with a certain width and height (say 1920, 1080)
    //      and never change _that viewport_'s `worldWidth` / `worldHeight`,
    //      we can just draw GUI elements in the range `(0, 0) .. (1920, 1080)`
    //      and it will work no matter what we resize the screen to.
    // - Every class that owns a viewport should call `viewport.apply()`
    //      at the start of their draw method.
    //      Classes that aren't screens should take in the caller's viewport, and re-apply it after.
    // - Classes with a fixed camera position (like HUD and menus)
    //      should call `camera.apply(centerCamera = true)`. Others should leave it false.
    private final OrthographicCamera camera;
    public static FitViewport viewport;

    /**
     * Constructs a new `Main`.
     * This is a
     * <a href=https://en.wikipedia.org/wiki/Factory_method_pattern>"factory method"</a>.
     * Here, it's necessary because the `FitViewport` constructor references `camera`.
     * @return The new GameRunner
     */
    public static Main createMain() {
        var inputProvider = new DefaultInputProvider();
        var graphicsProvider = new DefaultGraphicsProvider();
        var assetManager = new AssetManager();
        return new Main(inputProvider, graphicsProvider, assetManager);
    }

    /**
     * Creates a game instance.
     *
     * @param inputProvider    Input provider. Use FakeInputProvider in test code.
     * @param graphicsProvider Graphics provider. Use FakeGraphicsProvider in test code.
     * @param assetManager     Loads assets. Use `mock(AssetManager.class)` in test code.
     */
    public Main(InputProvider inputProvider,
                GraphicsProvider graphicsProvider,
                AssetManager assetManager
    ) {
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.assetManager = assetManager;

        this.camera = graphicsProvider.createCamera();
        this.viewport = graphicsProvider.createViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT, camera);
    }

    @Override
    public void create() {
        // This method should be "test-safe".
        // When run with properly-faked providers, it should not crash test code.

        camera.setToOrtho(false, graphicsProvider.getWidth(), graphicsProvider.getHeight());

        // Load up the assets, blocking until they're loaded.
        // The asset manager expects the asset's file name,
        // and the class of the asset to load.
        assetManager.load("rubik.fnt", BitmapFont.class);
        assetManager.load("rubik1.png", Texture.class);
        assetManager.load("rubik2.png", Texture.class);
        assetManager.load(AssetFileNames.START_BACKGROUND, Texture.class);
        assetManager.load(AssetFileNames.GAME_BACKGROUND, Texture.class);
        assetManager.load(AssetFileNames.SHOP_BACKGROUND, Texture.class);
        assetManager.load(AssetFileNames.CELL, Texture.class);
        assetManager.load(AssetFileNames.GLUCOSE, Texture.class);
        assetManager.load(AssetFileNames.DEFAULT_FONT, BitmapFont.class);
        assetManager.finishLoading();

        // May need to set to gameScreenManager at somepoint.
        setScreen(new MainMenuScreen(inputProvider, graphicsProvider, this, assetManager, camera, viewport));
    }

    @Override
    public void dispose() {
        if (screen != null) screen.hide();
        assetManager.dispose();
    }


    @Override
    public void pause() {
        if (screen != null) screen.pause();
    }

    @Override
    public void resume() {
        if (screen != null) screen.resume();
    }

    @Override
    public void render() {
        if (screen != null) screen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        if (screen != null) screen.resize(width, height);
    }

    public void setScreen(GameOfCellsScreen screen) {
        assert screen != null;
        if (this.screen != null) this.screen.hide();

        this.screen = screen;
        this.screen.show();
        this.screen.resize(graphicsProvider.getWidth(), graphicsProvider.getHeight());
    }

    public GameOfCellsScreen getScreen() {
        return this.screen;
    }

    /**
     * Responds to key-presses, updating game state.
     * @param deltaTimeSeconds The time passed since the last call to `handleInput`, in seconds.
     */
    public void handleInput(float deltaTimeSeconds) {
        screen.handleInput(deltaTimeSeconds);
    }

    /**
     * Updates game state that does not depend on input.
     * @param deltaTimeSeconds The time passed since the last call to `update`, in seconds.
     */
    public void update(float deltaTimeSeconds) {
        screen.update(deltaTimeSeconds);
    }

    /**
     * @return this game's graphics provider.
     */
    public GraphicsProvider getGraphicsProvider() {
        return this.graphicsProvider;
    }

}
