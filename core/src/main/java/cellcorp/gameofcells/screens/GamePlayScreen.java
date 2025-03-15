package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.GlucoseManager;
import cellcorp.gameofcells.objects.HUD;
import cellcorp.gameofcells.objects.HUDBar;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * GamePlay Screen
 * <p>
 * Contains the main gameplay loop.
 *
 * @author Brendon Vinyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 03/05/2025
 * @course CIS 405
 * @assignment GameOfCells
 */


/**
 * First screen of the application. Displayed after the application is created.
 */
public class GamePlayScreen implements GameOfCellsScreen {
    public static final String MESSAGE_GAME = "Game is now playing..."; // Message after starting the screen
    public static final String MESSAGE_SHOP = "Press S to access the shop screen.";

    private Main game;

    /// Loads assets during game creation,
    /// then provides loaded assets to draw code, using [AssetManager#get(String)]
    private final AssetManager assetManager;

    /// Gets information about inputs, like held-down keys.
    /// Use this instead of `Gdx.input`, to avoid crashing tests.
    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;

    // Camera/Viewport
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    // For rendering text
    protected SpriteBatch batch;  // Define the batch for drawing text
    protected ShapeRenderer shape;

    //Objects for rendering the game
    private final Cell cell;
    private final GlucoseManager glucoseManager;
    private final HUD hud;
    private final HUDBar healthBar;
    private final HUDBar ATPBar;

    /**
     * Constructs the GamePlayScreen.
     *
     * @param inputProvider    Provides user input information.
     * @param graphicsProvider Provide graphics information.
     * @param game             The main game instance.
     * @param camera           The camera for rendering.
     * @param viewport         The viewport for screen rendering scaling.
     */
    public GamePlayScreen(
            InputProvider inputProvider, GraphicsProvider graphicsProvider, Main game,
            AssetManager assetManager,
            OrthographicCamera camera,
            FitViewport viewport
    ) {
        this.assetManager = assetManager;
        this.game = game;
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;

        this.camera = camera;
        this.viewport = viewport;

        this.cell = new Cell(assetManager);
        this.glucoseManager = new GlucoseManager(assetManager);
        
        this.batch = graphicsProvider.createSpriteBatch();
        this.shape = graphicsProvider.createShapeRenderer();

        this.hud = new HUD(assetManager, shape);
        this.healthBar = new HUDBar(HUDBar.Type.HEALTH);
        this.ATPBar = new HUDBar(HUDBar.Type.ATP);
    }

    /**
     * Show the screen.
     */
    @Override
    public void show() {
        // This method should be "test-safe".
        // When run, if the game has been constructed with properly-mocked providers,
        // it should not crash test code.
    }

    /// Move the game state forward a tick, handling input, performing updates, and rendering.
    /// LibGDX combines these into a single method call, but we separate them out into public methods,
    /// to let us write tests where we call only [GamePlayScreen#handleInput] and [GamePlayScreen#update]
    @Override
    public void render(float deltaTimeSeconds) {
        handleInput(deltaTimeSeconds);
        update(deltaTimeSeconds);
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
        viewport.update(width, height, true);  // Update the viewport
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
        cell.dispose(); // dispose cell
        glucoseManager.dispose();
        hud.dispose();
        batch.dispose();  // Dispose of the batch

        //need to handle actually disposing but for now meh
        // glucose.dispose();
    }

    /**
     * Handle input from the user and transitions to the GamePlayScreen when Enter is pressed.
     */
    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Move to `ShopScreen` when 's' is pressed.
        if (inputProvider.isKeyPressed(Input.Keys.S)) {
            game.setScreen(new ShopScreen(
                    game,
                    inputProvider,
                    graphicsProvider,
                    assetManager,
                    camera,
                    viewport,
                    this
            ));
        }

        cell.move(
                deltaTimeSeconds,
                inputProvider.isKeyPressed(Input.Keys.LEFT),    // Check if the left key is pressed
                inputProvider.isKeyPressed(Input.Keys.RIGHT),   // Check if the right key is pressed
                inputProvider.isKeyPressed(Input.Keys.UP),    // Check if the up key is pressed
                inputProvider.isKeyPressed(Input.Keys.DOWN)   // Check if the down key is pressed
        );
    }

    /**
     * Update the game state.
     *
     * @param deltaTimeSeconds The time since the last frame in seconds.
     */
    @Override
    public void update(float deltaTimeSeconds) {
        hud.update(deltaTimeSeconds, cell.getCellHealth(), cell.getCellATP());
        HUDBar.update(cell.getCellHealth(), cell.getCellATP());
    }

    /**
     * Renders the start screen.
     */
    @Override
    public void draw() {
        var startBackground = assetManager.get(AssetFileNames.START_BACKGROUND, Texture.class);
        var gameBackground = assetManager.get(AssetFileNames.GAME_BACKGROUND, Texture.class);

        // Set up font
        var font = assetManager.get(AssetFileNames.DEFAULT_FONT, BitmapFont.class);

        font.getData().setScale(2);  // Set the font size

        // Set the font color to white
        font.setColor(Color.BLACK);

        // Draw the screen
        ScreenUtils.clear(0, 0, 0, 1);  // Clear the screen with a black background

        // I don't know what `viewport.apply(...)` does but when it was omitted
        // the HTML version was displaying way in the bottom-left and getting cut off.
        viewport.apply(true);
        camera.update();    // Update the camera
        batch.setProjectionMatrix(camera.combined);

        batch.begin();  // Start the batch for drawing 2d element

        // Draw the gameplay screen
        batch.draw(gameBackground, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());  // Draw the game background

        // Draw the text in white
        font.setColor(Color.WHITE);  // white for text
        font.draw(batch, MESSAGE_GAME, 100, 100);  // Regular position
        font.draw(batch, MESSAGE_SHOP, 102, 75);

        // You can start rendering other game objects (like the cell) here
        glucoseManager.draw(batch); // draws glucose beneath the cell.
        hud.draw(batch); // Draw hud
        cell.draw(batch);

        batch.end();    // End the batch

        healthBar.draw();
        ATPBar.draw();

    }

    /**
     * Get the message being displayed.
     *
     * @return The message being displayed.
     */
    public String getMessage() {
        return MESSAGE_GAME;
    }

    /**
     * Get the batch for rendering.
     *
     * @return The batch for rendering.
     */
    public SpriteBatch getBatch() {
        return batch;
    }

    /**
     * Get this screen's glucose manager.
     */
    public GlucoseManager getGlucoseManager() {
        return glucoseManager;
    }
}
