package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.inputproviders.InputProvider;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.GlucoseManager;

/**
* GamePlay Screen
*
* Contains the main gameplay loop.
*
* @author Brendon Vinyard / vineyabn207
* @author Andrew Sennoga-Kimuli / sennogat106
* @author Mark Murphy / murphyml207
* @author Tim Davey / daveytj206
*
* @date 03/05/2025
* @course CIS 405
* @assignment GameOfCells
*/


/**
 * First screen of the application. Displayed after the application is created.
 */
public class GamePlayScreen implements Screen {
    private Main game;


    /// Loads assets during game creation,
    /// then provides loaded assets to draw code, using [AssetManager#get(String)]
    private final AssetManager assetManager;

    /// Gets information about inputs, like held-down keys.
    /// Use this instead of `Gdx.input`, to avoid crashing tests.
    private final InputProvider inputProvider;

    // Camera/Viewport
    private OrthographicCamera camera;
    private FitViewport viewport;

    // For rendering text
    private BitmapFont font;  // Define the font for text
    private SpriteBatch batch;  // Define the batch for drawing text
    private String message = "Press the spacebar to continue...";

    //Objects
    private Cell cell;
    private GlucoseManager glucoseManager;

    public GamePlayScreen(Main game, AssetManager assetManager, InputProvider inputProvider, OrthographicCamera camera, FitViewport viewport) {
        this.assetManager = assetManager;
        this.game = game;
        this.inputProvider = inputProvider;

        this.camera = camera;
        this.viewport = viewport;
    }

    @Override
    public void show() {

        // Prepare your screen here.
        font = new BitmapFont();  // Initialize the font
        batch = new SpriteBatch();  // Initialize the batch
        cell = new Cell(assetManager); // Initialize cell
        glucoseManager = new GlucoseManager(assetManager);

    }

    /// Move the game state forward a tick, handling input, performing updates, and rendering.
    /// LibGDX combines these into a single method call, but we separate them out into public methods,
    /// to let us write tests where we call only [GamePlayScreen#handleInput] and [GamePlayScreen#update]
    @Override
    public void render(float delta) {
        handleInput(delta);
        update(delta);
        draw();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        font.dispose();  // Dispose of the font
        cell.dispose(); // dispose cell
        glucoseManager.dispose();
        batch.dispose();  // Dispose of the batch
        
        //need to handle actually disposing but for now meh 
        // glucose.dispose();

    }

    /// Handle input, responding to key-presses.
    public void handleInput(float deltaTime) {
        // Check for key input
        if (inputProvider.isKeyPressed(Input.Keys.SPACE)) {
            // Change the message when spacebar is pressed
            message = "Spacebar was pressed!";
        }
    }

    /// Perform updates that don't depend on input.
    public void update(float deltaTime) {

    }

    /// Draw the game state.
    /// No logic should happen here, because it won't be seen by tests.
    public void draw() {
        // Draw your screen here. "delta" is the time since last render in seconds.
        ScreenUtils.clear(0, 0, 0, 1);

        // Set up the camera for 2d rendering
        camera.update();

        // Draw the prompt or message
        batch.begin();  // Start the batch for drawing 2d elements
        font.draw(batch, message, 100, 100);  // Draw the message
        glucoseManager.draw(batch); // draws glucose beneath the cell.
        cell.draw(batch); //Draws the Cell on Screen
        batch.end(); // End the batch
    }

    /// Get the current message of this screen.
    public String getMessage() {
        return this.message;
    }
}
