package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.providers.ConfigProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * Pause Screen
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 04/09/2025
 * @course CIS 405
 * @assignment Game of Cells
 * @description This is the pause screen for the game. It is displayed when the game is paused.
 * It allows the player to resume the game, view controls, or quit to the main menu.
 * The pause screen is displayed over the game screen, and the game screen is paused
 * while the pause screen is displayed.
 * The pause screen is implemented as a singleton, so that there is only one instance
 * of the pause screen at a time. This is to prevent multiple pause screens from
 * being displayed at the same time, which could cause confusion for the player.
 */

public class PauseScreen implements GameOfCellsScreen {
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

    // The pause menu options
    private static final String[] PAUSE_OPTIONS = {
        "Resume",
        "Controls",
        "Quit to Menu"
    };
    private static final String INSTRUCTIONS = "Arrow keys to navigate | Enter to select";

    private final GamePlayScreen gamePlayScreen;
    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private final ConfigProvider configProvider;
    private final Main game;
    private final ShapeRenderer shapeRenderer;
    private final AssetManager assetManager;
    private final Viewport viewport;
    private final Particles particles;
    private final MenuSystem menuSystem;
    private final SpriteBatch batch;

    /**
     * Constructor for the PauseScreen class.
     *
     * @param gamePlayScreen   The game play screen to pause
     * @param inputProvider    The input provider for handling user input
     * @param graphicsProvider The graphics provider for rendering
     * @param game             The main game instance
     * @param assetManager     The asset manager for loading assets
     * @param camera           The camera for rendering
     * @param configProvider   The configuration provider for loading game settings
     */
    public PauseScreen(
        GamePlayScreen gamePlayScreen,
        InputProvider inputProvider,
        GraphicsProvider graphicsProvider,
        Main game,
        AssetManager assetManager,
        Camera camera,
        ConfigProvider configProvider) {

        this.gamePlayScreen = gamePlayScreen;
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.configProvider = configProvider;
        this.game = game;
        this.assetManager = assetManager;
        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);

        this.shapeRenderer = graphicsProvider.createShapeRenderer();
        this.particles = new Particles(graphicsProvider.createWhitePixelTexture());
        this.menuSystem = new MenuSystem(
            new Stage(graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT), graphicsProvider.createSpriteBatch()),
            assetManager,
            graphicsProvider
        );
        this.batch = graphicsProvider.createSpriteBatch();
    }

    /**
     * Initializes the pause screen.
     * This method is called when the pause screen is shown.
     */
    @Override
    public void show() {
        menuSystem.initializePauseMenu("Paused", PAUSE_OPTIONS, INSTRUCTIONS);
    }

    /**
     * Handles input for the pause screen.
     * This method is called every frame to handle user input.
     *
     * @param deltaTimeSeconds The time since the last frame
     */
    @Override
    public void render(float delta) {
        handleInput(delta);
        update(delta);
        draw();
    }

    /**
     * Resizes the pause screen.
     * This method is called when the window is resized.
     *
     * @param width  The new width of the window
     * @param height The new height of the window
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        gamePlayScreen.resize(width, height);
        menuSystem.getStage().getViewport().update(width, height, true);
    }

    /**
     * Pauses the pause screen.
     * This method is called when the pause screen is paused.
     */
    @Override
    public void pause() {
        // Pause logic if needed
    }

    /**
     * Resumes the pause screen.
     * This method is called when the pause screen is resumed.
     */
    @Override
    public void resume() {
        // Resume logic if needed
    }

    /**
     * Hides the pause screen.
     * This method is called when the pause screen is hidden.
     */
    @Override
    public void hide() {
        // Hide logic if needed
    }

    /**
     * Disposes of the pause screen.
     * This method is called when the pause screen is disposed.
     */
    @Override
    public void dispose() {
        menuSystem.clear();
        particles.dispose();
        batch.dispose();
    }

    /**
     * Handles input for the pause screen.
     * This method is called every frame to handle user input.
     *
     * @param deltaTimeSeconds The time since the last frame
     */
    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Navigate menu options
        if (inputProvider.isKeyJustPressed(Input.Keys.UP) || inputProvider.isKeyJustPressed(Input.Keys.W)) {
            menuSystem.updateSelection(menuSystem.getSelectedOptionIndex() - 1);
        }
        if (inputProvider.isKeyJustPressed(Input.Keys.DOWN) || inputProvider.isKeyJustPressed(Input.Keys.S)) {
            menuSystem.updateSelection(menuSystem.getSelectedOptionIndex() + 1);
        }

        // Confirm selection
        if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)
            || inputProvider.isKeyJustPressed(Input.Keys.SPACE)) {
            switch (menuSystem.getSelectedOptionIndex()) {
                case 0: // Resume
                    gamePlayScreen.resumeGame();
                    game.setScreen(gamePlayScreen);
                    break;
                case 1: // Controls
                    game.setScreen(new GameInfoControlsScreen(
                        inputProvider,
                        graphicsProvider,
                        game,
                        assetManager,
                        this,
                        null,
                        viewport,
                        configProvider
                    ));
                    break;
                case 2: // Quit to Menu
                    gamePlayScreen.resumeGame();
                    game.setScreen(new MainMenuScreen(
                        inputProvider,
                        graphicsProvider,
                        game,
                        assetManager,
                        null,
                        viewport,
                        configProvider
                    ));
                    break;
            }
        }

        // Handle ESC key to resume game
        if (inputProvider.isKeyJustPressed(Input.Keys.ESCAPE) ||
            inputProvider.isKeyJustPressed(Input.Keys.P)) {
            gamePlayScreen.resumeGame();
            game.setScreen(gamePlayScreen);
        }
    }

    /**
     * Updates the pause screen.
     * This method is called every frame to update the game state.
     *
     * @param deltaTimeSeconds The time since the last frame
     */
    @Override
    public void update(float deltaTimeSeconds) {
        particles.update(deltaTimeSeconds, viewport.getWorldWidth(), viewport.getWorldHeight());
        menuSystem.getStage().act(deltaTimeSeconds);
    }

    /**
     * Draws the pause screen.
     * This method is called every frame to draw the game state.
     */
    @Override
    public void draw() {
        // First draw the gameplay screen (paused)
        gamePlayScreen.draw();

        // Then draw our pause menu overlay
        ScreenUtils.clear(Main.PURPLE); // Darker purple background
        viewport.apply(true);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        // Draw the particles
        particles.draw(batch);
        batch.end();

        // Draw the menu
        menuSystem.getStage().act();
        menuSystem.getStage().draw();
    }

    /**
     * Get the menu system.
     *
     * @return The menu system used for the pause screen
     */
    public MenuSystem getMenuSystem() {
        return menuSystem;
    }

    /**
     * Get the particles object.
     *
     * @return The particles object used for rendering
     */
    public Particles getParticles() {
        return particles;
    }
}
