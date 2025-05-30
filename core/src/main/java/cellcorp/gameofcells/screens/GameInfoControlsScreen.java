package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.providers.ConfigProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * Game Information and Controls Screen
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 04/09/2025
 * @course CIS 405
 * @assignment Game of Cells
 * @description This is the game information and controls screen. This class handles displaying
 * the game information and controls to the user. It also handles user input and
 * updates the screen accordingly. The screen is displayed when the user selects
 * the "Game Info" option from the main menu.
 */
public class GameInfoControlsScreen implements GameOfCellsScreen {
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
     * Game information and controls text.
     */
    private static final String[] GAME_INFO = {
        "Welcome to Game of Cells!",
        "",
        "Embark on a microscopic adventure",
        "where you control a living cell",
        "in a vibrant biological world.",
        "",
        "Your mission is to explore,",
        "collect glucose, and survive."
    };

    /**
     * Controls text.
     */
    private static final String[] CONTROLS = {
        "CONTROLS",
        "----------------------",
        "Movement:",
        "Arrow keys or WASD",
        "",
        "Actions:",
        "ENTER/SPACE - Select",
        "ESC/P - Pause / Go back",
        "",
        "Gameplay:",
        "Buy organelles to grow stronger",
        "Divide to create new life",
        "Stay alive and thrive!"
    };

    private static final String INSTRUCTION = "Press ENTER / SPACE to return...";

    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private final ConfigProvider configProvider;
    private final Main game;
    private final AssetManager assetManager;
    private final GameOfCellsScreen previousScreen;
    private final Viewport viewport;
    private final Particles particles;
    private final MenuSystem menuSystem;

    private float startX;
    private float startY;
    private Texture cellTexture;

    /**
     * Constructs a new GameInfoControlsScreen.
     *
     * @param inputProvider    The input provider for handling user input
     * @param graphicsProvider The graphics provider for rendering graphics
     * @param game             The main game instance
     * @param assetManager     The asset manager for loading assets
     * @param previousScreen   The previous screen to return to
     * @param camera           The camera for rendering
     * @param viewport         The viewport for rendering
     * @param configProvider   The configuration provider for loading game settings
     */
    public GameInfoControlsScreen(
        InputProvider inputProvider,
        GraphicsProvider graphicsProvider,
        Main game,
        AssetManager assetManager,
        GameOfCellsScreen previousScreen,
        Camera camera,
        Viewport viewport,
        ConfigProvider configProvider) {
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.configProvider = configProvider;
        this.game = game;
        this.assetManager = assetManager;
        this.previousScreen = previousScreen;
        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);

        this.particles = new Particles(graphicsProvider.createWhitePixelTexture());
        this.menuSystem = new MenuSystem(
            new Stage(
                graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT),
                graphicsProvider.createSpriteBatch()),
            assetManager,
            graphicsProvider
        );

        this.cellTexture = assetManager.get(AssetFileNames.CELL, Texture.class);
    }

    /**
     * Shows the screen.
     * This method is called when the screen is first displayed.
     * It initializes the menu system and sets up the layout for the game information and controls.
     * It also sets the position of the message to be displayed on the screen.
     */
    @Override
    public void show() {
        // Initialize simple back menu
        menuSystem.initializeSplitLayout(
            "GAME INFORMATION",
            GAME_INFO,
            CONTROLS,
            INSTRUCTION);
    }

    /**
     * Renders the screen.
     * This method is called every frame to update and draw the screen.
     * It handles user input, updates the game state, and draws the screen.
     *
     * @param delta The time elapsed since the last frame, in seconds
     */
    @Override
    public void render(float delta) {
        handleInput(delta);
        update(delta);
        draw();
    }

    /**
     * Resizes the screen.
     * This method is called when the screen is resized.
     * It updates the viewport to match the new screen dimensions.
     *
     * @param width  The new width of the screen
     * @param height The new height of the screen
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /**
     * Pauses the screen.
     * This method is called when the game is paused.
     * It can be used to pause any ongoing animations or processes.
     */
    @Override
    public void pause() {
    }

    /**
     * Resumes the screen.
     * This method is called when the game is resumed from a paused state.
     * It can be used to resume any paused animations or processes.
     */
    @Override
    public void resume() {
    }

    /**
     * Hides the screen.
     * This method is called when the screen is no longer visible.
     * It can be used to clean up any resources or stop any ongoing processes.
     */
    @Override
    public void hide() {
    }

    /**
     * Disposes of the screen.
     * This method is called when the screen is no longer needed.
     * It can be used to dispose of any resources or clean up any objects.
     */
    @Override
    public void dispose() {
        particles.dispose();
        menuSystem.clear();
    }

    /**
     * Handles user input.
     * This method checks for user input and updates the game state accordingly.
     * It returns to the previous screen if any key is pressed.
     *
     * @param deltaTimeSeconds The time elapsed since the last frame, in seconds
     */
    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Return to the settings screen if any key is pressed
        if (inputProvider.isKeyJustPressed(Input.Keys.SPACE) ||
            inputProvider.isKeyJustPressed(Input.Keys.ENTER) ||
            inputProvider.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(previousScreen);
        }
    }

    /**
     * Updates the game state.
     * This method updates the particles and any other game state as needed.
     *
     * @param deltaTimeSeconds The time elapsed since the last frame, in seconds
     */
    @Override
    public void update(float deltaTimeSeconds) {
        particles.update(deltaTimeSeconds, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    /**
     * Draws the screen.
     * This method draws the background, particles, and menu system to the screen.
     */
    @Override
    public void draw() {
        // Clear with a biological-themed color
        ScreenUtils.clear(Main.PURPLE); // Dark purple background

        viewport.apply(true);

        Camera camera = viewport.getCamera();

        // Draw background elements
        SpriteBatch batch = graphicsProvider.createSpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw sem-transparent cell texture
        float cellWidth = 400;
        float cellHeight = 400;
        float cellX = camera.position.x - cellWidth / 2;
        float cellY = camera.position.y - cellHeight / 2;

        batch.setColor(1, 1, 1, 0.15f);
        batch.draw(cellTexture,
            cellX,
            cellY,
            cellWidth,
            cellHeight);
        batch.setColor(1, 1, 1, 1); // Reset color to white

        // Draw particles
        particles.draw(batch);
        batch.end();

        // Draw menu (back option)
        menuSystem.getStage().act();
        menuSystem.getStage().draw();
    }

    /**
     * Get the position of the message.
     *
     * @return The x coordinate of the message.
     */
    public float getMessagePositionX() {
        return startX;
    }

    /**
     * Get the position of the message.
     *
     * @return The y coordinate of the message.
     */
    public float getMessagePositionY() {
        return startY;
    }


    /**
     * Get the particles.
     *
     * @return The particles object.
     */
    public Particles getParticles() {
        return particles;
    }

    /**
     * Get the viewport.
     *
     * @return The viewport object.
     */
    public Viewport getViewport() {
        return viewport;
    }
}
