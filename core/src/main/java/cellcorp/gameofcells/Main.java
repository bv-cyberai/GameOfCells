package cellcorp.gameofcells;

/**
 * Game Class
 * <p>
 * The base game class for GameOfCells
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */

import cellcorp.gameofcells.providers.*;
import cellcorp.gameofcells.screens.GameOfCellsScreen;
import cellcorp.gameofcells.screens.MainMenuScreen;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {
    public static final int DEFAULT_SCREEN_WIDTH = 1280;
    public static final int DEFAULT_SCREEN_HEIGHT = 800;

    public static final Color PURPLE = new Color(0.08f, 0.05f, 0.10f, 1f); // Darker purple
    public static final Color TEAL = new Color(.424f, .553f, .573f, 1f);
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
    /**
     * The camera used to render the game.
     */
    private final OrthographicCamera camera;
    /**
     * The viewport used to render the game.
     */
    private final FitViewport viewport;
    private final ConfigProvider configProvider;
    /**
     * The currently-shown screen.
     */
    private GameOfCellsScreen screen;

    /**
     * Creates a game instance.
     *
     * @param inputProvider    Input provider. Use FakeInputProvider in test code.
     * @param graphicsProvider Graphics provider. Use FakeGraphicsProvider in test code.
     * @param assetManager     Loads assets. Use `mock(AssetManager.class)` in test code.
     * @param camera           The camera. Use `mock(OrthographicCamera.class)` in test code.
     * @param viewport         The viewport. Use `mock(FitViewport.class)` in test code.
     * @param configProvider
     */
    public Main(InputProvider inputProvider,
                GraphicsProvider graphicsProvider,
                AssetManager assetManager,
                OrthographicCamera camera,
                FitViewport viewport,
                ConfigProvider configProvider) {
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.assetManager = assetManager;
        this.camera = camera;
        this.viewport = viewport;
        this.configProvider = configProvider;
    }

    /**
     * Constructs a new `Main`.
     * This is a
     * <a href=https://en.wikipedia.org/wiki/Factory_method_pattern>"factory method"</a>.
     * Here, it's necessary because the `FitViewport` constructor references `camera`.
     *
     * @return The new GameRunner
     */
    public static Main createMain() {
        var inputProvider = new DefaultInputProvider();
        var graphicsProvider = new DefaultGraphicsProvider();
        var assetManager = new AssetManager();
        var camera = new OrthographicCamera();
        var configProvider = new ConfigProvider();
        // Gdx.graphics is not instantiated until `create()`.
        // Just use the configuration constants here.
        var viewport = new FitViewport(
            DEFAULT_SCREEN_WIDTH,
            DEFAULT_SCREEN_HEIGHT,
            camera
        );

        return new Main(inputProvider, graphicsProvider, assetManager, camera, viewport, configProvider);
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
        assetManager.load("rubik_yellow.fnt", BitmapFont.class);
        assetManager.load("rubik_yellow1.png", Texture.class);
        assetManager.load("rubik_yellow2.png", Texture.class);
        assetManager.load(AssetFileNames.CELL, Texture.class);
        assetManager.load(AssetFileNames.CELL_MEMBRANE, Texture.class);
        assetManager.load(AssetFileNames.CELL_MEMBRANE_DAMAGED, Texture.class);
        assetManager.load(AssetFileNames.ATTRACT_SCREEN_CELL, Texture.class);
        assetManager.load(AssetFileNames.GLUCOSE, Texture.class);
        assetManager.load(AssetFileNames.DANGER, Texture.class);
        assetManager.load(AssetFileNames.DEFAULT_FONT, BitmapFont.class);
        assetManager.load(AssetFileNames.HUD_FONT, BitmapFont.class);
        assetManager.load(AssetFileNames.NOTIFICATION_FONT, BitmapFont.class);
        assetManager.load(AssetFileNames.MITOCHONDRIA_ICON, Texture.class);
        assetManager.load(AssetFileNames.RIBOSOME_ICON, Texture.class);
        assetManager.load(AssetFileNames.NUCLEUS_ICON, Texture.class);
        assetManager.load(AssetFileNames.LOCK_ICON, Texture.class);
        assetManager.load(AssetFileNames.WHITE_PIXEL, Texture.class);
        assetManager.load(AssetFileNames.ACID_ZONE, Texture.class);
        assetManager.load(AssetFileNames.BASIC_ZONE, Texture.class);
        assetManager.load(AssetFileNames.WASD_ARROWS_ICON, Texture.class);
        assetManager.load(AssetFileNames.SPACE_ENTER_ICON, Texture.class);
        assetManager.load(AssetFileNames.SHOP_BUTTON, Texture.class);
        assetManager.load(AssetFileNames.MOVE_KEY, Texture.class);
        assetManager.load(AssetFileNames.PAUSE_BUTTON, Texture.class);
        assetManager.load(AssetFileNames.QUIT_BUTTON, Texture.class);
        assetManager.load(AssetFileNames.HEAL_ICON, Texture.class);
        assetManager.load(AssetFileNames.PARALLAX_FAR, Texture.class);
        assetManager.load(AssetFileNames.PARALLAX_MID, Texture.class);
        assetManager.load(AssetFileNames.PARALLAX_NEAR, Texture.class);
        assetManager.load(AssetFileNames.FLOATING_OVERLAY, Texture.class);
        assetManager.load(AssetFileNames.VIGNETTE_LOW_HEALTH, Texture.class);
        assetManager.load(AssetFileNames.SCROLL_BAR, Texture.class);
        assetManager.load(AssetFileNames.WS_KEYS, Texture.class);
        assetManager.load(AssetFileNames.ARROW_TO_BASIC_ZONE, Texture.class);
        assetManager.load(AssetFileNames.CONTROLS_INFO_BUTTON, Texture.class);
        assetManager.finishLoading();

        // I know. I know.
        // HeadlessFiles isn't available to GWT, cause the headless backend isn't.
        // I'm sure there's a better way to do this.
        if (Gdx.files != null && !(Gdx.files.getClass().getName().contains("Mockito")) && !(Gdx.files.getClass().getName().contains("Headless"))) {
            configProvider.loadConfig();
        }
        // May need to set to gameScreenManager at somepoint.
        setScreen(new MainMenuScreen(inputProvider, graphicsProvider, this, assetManager, camera, viewport, configProvider));
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

    public GameOfCellsScreen getScreen() {
        return this.screen;
    }

    public void setScreen(GameOfCellsScreen screen) {
        if (this.screen != null) this.screen.hide();

        this.screen = screen;
        this.screen.show();
        this.screen.resize(graphicsProvider.getWidth(), graphicsProvider.getHeight());
    }

    /**
     * Responds to key-presses, updating game state.
     *
     * @param deltaTimeSeconds The time passed since the last call to `handleInput`, in seconds.
     */
    public void handleInput(float deltaTimeSeconds) {
        screen.handleInput(deltaTimeSeconds);
    }

    /**
     * Updates game state that does not depend on input.
     *
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

    /**
     * @return this game's input provider.
     */
    public InputProvider getInputProvider() {
        return this.inputProvider;
    }

    /**
     * @return this game's asset manager.
     */
    public AssetManager getAssetManager() {
        return this.assetManager;
    }

    /**
     * @return this game's camera.
     */
    public OrthographicCamera getCamera() {
        return this.camera;
    }

    /**
     * @return this game's viewport.
     */
    public FitViewport getViewport() {
        return this.viewport;
    }

}
