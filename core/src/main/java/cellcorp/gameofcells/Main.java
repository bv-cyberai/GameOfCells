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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import cellcorp.gameofcells.providers.DefaultGraphicsProvider;
import cellcorp.gameofcells.providers.DefaultInputProvider;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import cellcorp.gameofcells.screens.GameOfCellsScreen;
import cellcorp.gameofcells.screens.MainMenuScreen;

public class Main implements ApplicationListener {
    public static final int DEFAULT_SCREEN_WIDTH = 1280;
    public static final int DEFAULT_SCREEN_HEIGHT = 800;

    public static final Color PURPLE = new Color(.157f, .115f, .181f, 1f);
    public static final Color TEAL = new Color(.424f, .553f, .573f, 1f);


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

    }

    @Override
    public void create() {
        // This method should be "test-safe".
        // When run with properly-faked providers, it should not crash test code.

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
        setScreen(new MainMenuScreen(inputProvider, graphicsProvider, this, assetManager));
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
