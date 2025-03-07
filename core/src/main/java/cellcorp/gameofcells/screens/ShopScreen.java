package cellcorp.gameofcells.screens;

/**
 * GamePlay Screen
 * <p>
 * This screen provides the main gameplay loop
 * functionality.
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;


/**
 * First screen of the application. Displayed after the application is created.
 */
public class ShopScreen implements GameOfCellsScreen {
    private Main game;

    /// Gets information about inputs, like held-down keys.
    /// Use this instead of `Gdx.input`, to avoid crashing tests.
    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;

    private final AssetManager assetManager;

    // Camera/Viewport
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    // Keeps track of the initial screen prior to transition
    private final GameOfCellsScreen previousScreen;

    // For rendering text
    private SpriteBatch batch;  // Define the batch for drawing text

    /**
     * Constructs the GamePlayScreen.
     *
     * @param game           The main game instance.
     * @param inputProvider  Handles user input.
     * @param camera         The camera for rendering.
     * @param viewport       The viewport for screen rendering scaling.
     * @param previousScreen The current screen gameplayscreen
     */
    public ShopScreen(
            Main game,
            InputProvider inputProvider,
            GraphicsProvider graphicsProvider,
            AssetManager assetManager,
            OrthographicCamera camera,
            FitViewport viewport,
            GameOfCellsScreen previousScreen
    ) {
        this.game = game;
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.assetManager = assetManager;
        this.camera = camera;
        this.viewport = viewport;
        this.previousScreen = previousScreen;
    }

    /**
     * Show the screen.
     */
    @Override
    public void show() {
        this.batch = graphicsProvider.createSpriteBatch();
    }

    /// Move the game state forward a tick, handling input, performing updates, and rendering.
    /// LibGDX combines these into a single method call, but we separate them out into public methods,
    /// to let us write tests where we call only [ShopScreen#handleInput] and [ShopScreen#update]
    @Override
    public void render(float delta) {
        handleInput(delta);
        update(delta);
        draw();
    }

    /**
     * Resize the screen.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        viewport.update(width, height);  // Update the viewport
        camera.viewportWidth = width;   // Update the camera viewport width
        camera.viewportHeight = height; // Update the camera viewport height
    }

    /**
     * Pause the screen.
     */
    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    /**
     * Resume the screen.
     */
    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    /**
     * Hide the screen.
     */
    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    /**
     * Dispose of the screen's assets.
     */
    @Override
    public void dispose() {
        // Destroy screen's assets here.
        batch.dispose();  // Dispose of the batch
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        if (inputProvider.isKeyPressed(Input.Keys.E)) {
            game.setScreen(previousScreen);
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
    }


    @Override
    public void draw() {
        var font = assetManager.get(AssetFileNames.DEFAULT_FONT, BitmapFont.class);
        var shopBackground = assetManager.get(AssetFileNames.SHOP_BACKGROUND, Texture.class);

        // Set up font
        font.getData().setScale(2);  // Set the font size

        // Set the font color to white
        font.setColor(Color.BLACK);

        ScreenUtils.clear(0, 0, 0, 1);  // Clear the screen with a black background

        camera.update();    // Update the camera

        batch.begin();  // Start the batch for drawing 2d elements

        // Draw the shop screen
        batch.draw(shopBackground, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());  // Draw the game background

        // Draw the text in white
        font.setColor(Color.WHITE);  // white for text
        font.draw(batch, "Welcome to the Shop Screen", 100, 100);  // Regular position
        font.draw(batch, "Press E to exit", 102, 70);
        // You can start rendering other game objects (like the cell) here

        batch.end();    // End the batch
    }

    /**
     * Get the batch for rendering.
     *
     * @return The batch for rendering.
     */
    public SpriteBatch getBatch() {
        return batch;
    }
}
