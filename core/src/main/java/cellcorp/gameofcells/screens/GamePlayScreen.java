package cellcorp.gameofcells.screens;

/**
* GamePlay Screen
*
* This screen provides the main gameplay loop
* functionality.
*
* @author Brendon Vineyard / vineyabn207
* @author Andrew Sennoga-Kimuli / sennogat106
* @author Mark Murphy / murphyml207
* @author Tim Davey / daveytj206
*
* @date 02/18/2025
* @course CIS 405
* @assignment GameOfCells
*/

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.InputProvider;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class GamePlayScreen implements Screen {
    private Main game;

    /// Gets information about inputs, like held-down keys.
    /// Use this instead of `Gdx.input`, to avoid crashing tests.
    private final InputProvider inputProvider;

    // Camera/Viewport
    private OrthographicCamera camera;
    private FitViewport viewport;

    // For rendering text
    private SpriteBatch batch;  // Define the batch for drawing text
    private BitmapFont font;  // Define the font for text

    private static final String MESSAGE_START = "Press Enter to Start";  // Message for the start screen
    private static final String MESSAGE_GAME = "Game is now playing..."; // Message after starting the screen

    private boolean gameStarted = false;  // Flag to indicate if the game has started
    private boolean startScreen = true;  // Flag to indicate if the start screen is being displayed

    //Objects for rendering the game
    private Cell cell;

    // Background textures
    private Texture startBackground;
    private Texture gameBackground;

    /**
     * Constructs the GamePlayScreen.
     * @param game The main game instance.
     * @param inputProvider Handles user input.
     * @param camera The camera for rendering.
     * @param viewport The viewport for screen rendering scaling.
     */
    public GamePlayScreen(Main game, InputProvider inputProvider, OrthographicCamera camera, FitViewport viewport) {
        this.game = game;
        this.inputProvider = inputProvider;
        this.camera = camera;
        this.viewport = viewport;
    }

    /**
     * Show the screen.
     */
    @Override
    public void show() {
        // Prepare your screen here.
        font = new BitmapFont();  // Initialize the font
        font.getData().setScale(2);  // Set the font size

        // Set the font color to white
        font.setColor(Color.BLACK);

        batch = new SpriteBatch();  // Initialize the batch
        cell = new Cell(); // Initialize cell

        // Load background textures
        startBackground = new Texture("startBackground.png");
        gameBackground = new Texture("gameBackground.png");
    }

    /// Move the game state forward a tick, handling input, performing updates, and rendering.
    /// LibGDX combines these into a single method call, but we separate them out into public methods,
    /// to let us write tests where we call only [GamePlayScreen#handleInput] and [GamePlayScreen#update]
    @Override
    public void render(float delta) {
        handleInput();
        update(delta);
        draw();
    }

    /**
     * Resize the screen.
     * @param width The new width of the screen.
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
        font.dispose();  // Dispose of the font
        cell.dispose(); // dispose cell
        batch.dispose();  // Dispose of the batch

        // Dispose of background textures
        startBackground.dispose();
        gameBackground.dispose();
    }

    /**
     * Handle input from the user and transitions to the GamePlayScreen when Enter is pressed.
     */
    public void handleInput() {
        // Check for key input
        if (!gameStarted && inputProvider.isKeyPressed(Input.Keys.ENTER)) {
            gameStarted = true;
            startScreen = false;
        }
    }

    /**
     * Update the game state.
     * @param deltaTime The time since the last frame in seconds.
     */
    public void update(float deltaTime) {
        // Future update logic can do here
        if (gameStarted) {
            // Any updates for the game state after it starts
        }
    }

    /**
     * Renders the start screen.
     */
    public void draw() {
        ScreenUtils.clear(0, 0, 0, 1);  // Clear the screen with a black background

        camera.update();    // Update the camera

        batch.begin();  // Start the batch for drawing 2d elements
        
        if (!gameStarted) {
            // Display the start screen
            batch.draw(startBackground, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());  // Draw the start background

            // Draw the actual text in white
            font.setColor(Color.WHITE);  // white for text
            font.draw(batch, MESSAGE_START, 100, 100);  // Regular position
        } else {
            // Draw the gameplay screen
            batch.draw(gameBackground, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());  // Draw the game background

            // Draw the text in white
            font.setColor(Color.WHITE);  // white for text
            font.draw(batch, MESSAGE_GAME, 100, 100);  // Regular position
            
            // You can start rendering other game objects (like the cell) here
            cell.draw(batch);
        }

        batch.end();    // End the batch
    }

    /**
     * Get the message being displayed.
     * @return The message being displayed.
     */
    public String getMessage() {
        return gameStarted ? MESSAGE_GAME : MESSAGE_START;
    }
}
