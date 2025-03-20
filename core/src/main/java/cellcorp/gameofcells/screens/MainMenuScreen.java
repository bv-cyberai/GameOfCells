package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Gdx;
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
    private static final String[] MENU_OPTIONS = {"Start Game", "Settings", "Exit"};
    private int selectedOption = 0; // Index of the currently selected option

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
        // Navigate menu options with arrow keys
        if (inputProvider.isKeyPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + MENU_OPTIONS.length) % MENU_OPTIONS.length;
        }
        if (inputProvider.isKeyPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % MENU_OPTIONS.length;
        }

        // Confirm selection with Enter key
        if (inputProvider.isKeyPressed(Input.Keys.ENTER)) {
            switch (selectedOption) {
                case 0:
                    // Start the game
                    game.setScreen(new GamePlayScreen(
                            inputProvider,
                            graphicsProvider,
                            game,
                            assetManager,
                            camera,
                            viewport
                    ));
                    break;
                case 1: // Settings
                    // Open the settings screen
                    game.setScreen(new SettingsScreen(
                            inputProvider,
                            graphicsProvider,
                            game,
                            assetManager,
                            camera,
                            viewport
                    ));
                    break;
                case 2:
                    // Exit the game
                    Gdx.app.exit();
                    break;
            }
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
        // Clear the screen with a gradient background
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1); // Dark blue background

        viewport.apply(true);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Draw the instructional message
        spriteBatch.begin();
        var font = assetManager.get(AssetFileNames.DEFAULT_FONT, BitmapFont.class);
        font.getData().setScale(2); // Set the font size

        float menuX = viewport.getWorldWidth() / 2 - 100; // Center the menu
        float menuY = viewport.getWorldHeight() / 2 + 50; // Start position for the menu

        for (int i = 0; i < MENU_OPTIONS.length; i++) {
            // Highlight the selected option
            if (i == selectedOption) {
                font.setColor(Color.YELLOW); // Yellow for selected option
            } else {
                font.setColor(Color.WHITE); // Default color
            }
            // Draw the menu option
            font.draw(spriteBatch, MENU_OPTIONS[i], menuX, menuY - i * 50);
        }

        spriteBatch.end();
    }
}
