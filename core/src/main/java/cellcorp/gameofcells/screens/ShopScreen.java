package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Shop Screen
 * <p>
 * This screen allows players to evolve their cell into a new form using the shop
 *  
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */


/**
 * First screen of the application. Displayed after the application is created.
 */
public class ShopScreen implements GameOfCellsScreen {
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

    private Main game;

    /// Gets information about inputs, like held-down keys.
    /// Use this instead of `Gdx.input`, to avoid crashing tests.
    private final InputProvider inputProvider;

    private final AssetManager assetManager;

    // Camera/Viewport
    private final Viewport viewport;

    // Keeps track of the initial screen prior to transition
    private final GameOfCellsScreen previousScreen;

    // For rendering text
    private final SpriteBatch batch;  // Define the batch for drawing text

    // Shop state
    private boolean hasMitochondria = false; // Whether the player has evolved into a mitochondria
    private int evolutionCost = 40; // The cost to evolve into a mitochondria

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
            GameOfCellsScreen previousScreen
    ) {
        this.game = game;
        this.inputProvider = inputProvider;
        this.assetManager = assetManager;
        this.previousScreen = previousScreen;

        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        this.batch = graphicsProvider.createSpriteBatch();
    }

    /**
     * Show the screen.
     */
    @Override
    public void show() {
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
        viewport.update(width, height);
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
        if (inputProvider.isKeyJustPressed(Input.Keys.E)) {
            game.setScreen(previousScreen);
        }

        // Evolve into a mitochondria when 'M' is pressed
        if (inputProvider.isKeyJustPressed(Input.Keys.M) && !hasMitochondria) {
            // Deduct cost and evolve
            hasMitochondria = true;
            // TODO: Deduct evolutionCost from player's energy
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
        // Update shop logic (check if player has enough energy to evolve)
    }


    @Override
    public void draw() {
        var font = assetManager.get(AssetFileNames.DEFAULT_FONT, BitmapFont.class);
        var shopBackground = assetManager.get(AssetFileNames.SHOP_BACKGROUND, Texture.class);

        // Set up font
        font.getData().setScale(1.5f);  // Set the font size

        // Set the font color to white
        font.setColor(Color.WHITE);

        ScreenUtils.clear(0, 0, 0, 1);  // Clear the screen with a black background

        viewport.apply(true);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();  // Start the batch for drawing 2d elements

        // Draw the shop screen
        batch.draw(shopBackground, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());  // Draw the game background

        // Draw shop title
        font.draw(batch, "Cell Evolution Shop", viewport.getWorldWidth() / 2 - 200, viewport.getWorldHeight() - 50);

        // Draw mitochondria evolution option
        float optionY = viewport.getWorldHeight() / 2 + 50;
        if (!hasMitochondria) {
            font.draw(batch, "Evolve into a Mitochondria", 100, optionY);
            font.draw(batch, "Cost: " + evolutionCost + " ATP", 100, optionY - 30);
            font.draw(batch, "Press M to evolve", 100, optionY - 60);
        } else {
            font.draw(batch, "You are already a Mitochondria", 100, optionY);
        }

        // Draw exit instructions
        font.draw(batch, "Press E to exit", viewport.getWorldWidth() / 2 - 80, 50);

        // End drawing
        batch.end();
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
