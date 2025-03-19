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

/**
 * The screen for the main menu.
 * When the game starts this screen is loaded first.
 */
public class MainMenuScreen implements GameOfCellsScreen {

    /**
     * The instructional message displayed on the main menu
     */
    private static final String INSTRUCTIONS = "Press Enter to Start";
    private static final float INACTIVITY_TIMEOUT = 5f; // 5 seconds of inactivity

    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;

    private final Main game;
    private final AssetManager assetManager;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;

    private float inactivityTimer = 0f;

    public MainMenuScreen(
            InputProvider inputProvider,
            GraphicsProvider graphicsProvider,
            Main game,
            AssetManager assetManager,
            OrthographicCamera camera,
            FitViewport viewport
    ) {
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.game = game;
        this.assetManager = assetManager;
        this.camera = camera;
        this.viewport = viewport;
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
        camera.viewportWidth = width;   // Update the camera viewport width
        camera.viewportHeight = height; // Update the camera viewport height
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
        if (inputProvider.isKeyPressed(Input.Keys.ENTER)) {
            game.setScreen(new GamePlayScreen(
                    inputProvider,
                    graphicsProvider,
                    game,
                    assetManager,
                    camera,
                    viewport
            ));
        }

        // Reset the inactivity timer if any key is pressed
        if (inputProvider.isKeyPressed(Input.Keys.ANY_KEY)) {
            inactivityTimer = 0f;
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
        // Update the inactivity timer
        inactivityTimer += deltaTimeSeconds;

        // Transition to the attract screen if the inactivity timer exceeds the timeout
        if (inactivityTimer >= INACTIVITY_TIMEOUT) {
            game.setScreen(new AttractScreen(
                    inputProvider,
                    graphicsProvider,
                    game,
                    assetManager,
                    camera,
                    viewport
            ));
        }
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
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        // Display the start background
        spriteBatch.draw(startBackground, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        font.draw(spriteBatch, INSTRUCTIONS, 100, 100);

        spriteBatch.end();
    }
}
