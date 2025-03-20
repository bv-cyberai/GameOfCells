package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * The screen for the main menu.
 * When the game starts this screen is loaded first.
 */
public class MainMenuScreen implements GameOfCellsScreen {
    // Mark set these to be the previous `WORLD_WIDTH` and `WORLD_HEIGHT`.
    // Change as is most convenient.
    /**
     * Width of the HUD view rectangle.
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_WIDTH = 1200;
    /**
     * Height of the HUD view rectangle.
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_HEIGHT = 800;

    /**
     * The instructional message displayed on the main menu
     */
    private static final String INSTRUCTIONS = "Press Enter to Start";

    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;

    private final Main game;
    private final AssetManager assetManager;
    private final Viewport viewport;
    private final SpriteBatch spriteBatch;

    public MainMenuScreen(
            InputProvider inputProvider,
            GraphicsProvider graphicsProvider,
            Main game,
            AssetManager assetManager
    ) {
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.game = game;
        this.assetManager = assetManager;
        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        this.spriteBatch = graphicsProvider.createSpriteBatch();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        update(delta);
        draw();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        viewport.update(width, height, true);  // Update the viewport
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.spriteBatch.dispose();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Move to `GamePlayScreen` when 'enter' is pressed.
        if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GamePlayScreen(
                    inputProvider,
                    graphicsProvider,
                    game,
                    assetManager
            ));
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {

    }

    @Override
    public void draw() {
        var startBackground = assetManager.get(AssetFileNames.START_BACKGROUND, Texture.class);

        var font = assetManager.get(AssetFileNames.DEFAULT_FONT, BitmapFont.class);
        font.getData().setScale(2);  // Set the font size

        // Draw the screen
        ScreenUtils.clear(Color.BLACK);  // Clear the screen with a black background

        // I don't know what `viewport.apply(...)` does but when it was omitted
        // the HTML version was displaying way in the bottom-left and getting cut off.
        viewport.apply(true);
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        // Display the start background
        spriteBatch.draw(startBackground, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        font.draw(spriteBatch, INSTRUCTIONS, 100, 100);

        spriteBatch.end();
    }
}
