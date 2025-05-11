package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import cellcorp.gameofcells.providers.GameLoaderSaver;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * Main Menu Screen
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 04/09/2025
 * @course CIS 405
 * @assignment Game of Cells
 * @description This is the main menu screen of the game. This class handles displaying
 *              the main menu options, handling user input, and transitioning to the game
 *              screen or game info screen based on user selection.
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
    private static final String[] MENU_OPTIONS = {
        "NEW GAME",
        "RESUME GAME",
        "GAME INFO",
        "EXIT"
    };
    /**
     * The instructions for the main menu
     */
    private static final String INSTRUCTIONS = "Use arrow keys or WASD to navigate\nPress Enter or Space to select";

    private static final float INACTIVITY_TIMEOUT = 20f; // 20 seconds of inactivity

    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private final ConfigProvider configProvider;
    private final Main game;
    private final AssetManager assetManager;
    private final Viewport viewport;

    private final Particles particles;
    private final MenuSystem menuSystem;

    // Textures for the main menu
    private final Texture cellTexture;
    private final Texture wasdArrowsIcon;
    private final Texture spaceEnterIcon;

    // Inactivity timer
    // This timer tracks the time since the last user input
    private float inactivityTimer = 0f;

    /**
     * Constructs a new MainMenuScreen.
     *
     * @param inputProvider The input provider for handling user input
     * @param graphicsProvider The graphics provider for rendering graphics
     * @param game The main game instance
     * @param assetManager The asset manager for loading and managing assets
     * @param camera The camera used for rendering
     * @param viewport The viewport for rendering
     * @param configProvider The configuration provider for managing game settings
     */
    public MainMenuScreen(
            InputProvider inputProvider,
            GraphicsProvider graphicsProvider,
            Main game,
            AssetManager assetManager,
            Camera camera,
            Viewport viewport,
            ConfigProvider configProvider) {

        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.configProvider = configProvider;
        this.game = game;
        this.assetManager = assetManager;
        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);

        // Load white pixel texture and initialize particles
        this.particles = new Particles(graphicsProvider.createWhitePixelTexture());
        this.menuSystem = new MenuSystem(
                new Stage(graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT),
                        graphicsProvider.createSpriteBatch()),
                        assetManager,
                        graphicsProvider
        );

        this.cellTexture = assetManager.get(AssetFileNames.ATTRACT_SCREEN_CELL, Texture.class);
        this.wasdArrowsIcon = assetManager.get(AssetFileNames.WASD_ARROWS_ICON, Texture.class);
        this.spaceEnterIcon = assetManager.get(AssetFileNames.SPACE_ENTER_ICON, Texture.class);

        //Config provider can be 'constructed' anywhere, this is useful as game objects will need access
        //to it.
//        configProvider = ConfigProvider.getInstance();
        //Config is loaded here to avoid issue with GDX files, it is also the first possible
        //location that would use any user defined values.
//        configProvider.loadConfig();
    }

    /**
     * Show the main menu screen.
     * This method is called when the screen is set as the current screen.
     */
    @Override
    public void show() {
        boolean hasSave = !GameLoaderSaver.isSaveFileEmpty();

        menuSystem.initializeMainMenu(
            "GAME OF CELLS",
            MENU_OPTIONS,
            INSTRUCTIONS,
            wasdArrowsIcon,
            spaceEnterIcon,
            new boolean[] {true, hasSave, true, true} // enables for each item
        );
    }

    /**
     * Render the main menu screen.
     * This method is called every frame to update and draw the screen.
     *
     * @param delta The time elapsed since the last frame
     */
    @Override
    public void render(float delta) {
        handleInput(delta);
        update(delta);
        draw();
    }

    /**
     * Resize the main menu screen.
     * This method is called when the window is resized.
     *
     * @param width The new width of the window
     * @param height The new height of the window
     */
    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        viewport.update(width, height, true); // Update the viewport
    }

    /**
     * Pause the main menu screen.
     * This method is called when the screen is paused.
     */
    @Override
    public void pause() {

    }

    /**
     * Resume the main menu screen.
     * This method is called when the screen is resumed.
     */
    @Override
    public void resume() {

    }

    /**
     * Hide the main menu screen.
     * This method is called when the screen is no longer visible.
     */
    @Override
    public void hide() {

    }

    /**
     * Dispose of the main menu screen.
     * This method is called when the screen is no longer needed.
     */
    @Override
    public void dispose() {
        particles.dispose();
        menuSystem.clear();
    }

    /**
     * Handle user input for the main menu.
     * This method processes user input and updates the menu selection accordingly.
     *
     * @param deltaTimeSeconds The time elapsed since the last frame
     */
    @Override
    public void handleInput(float deltaTimeSeconds) {
        if (inputProvider.isKeyJustPressed(Input.Keys.L)) {
            // For testing purposes
            game.setScreen(new AttractScreen(inputProvider, graphicsProvider, game, assetManager, this, configProvider));
        }

        // Navigate menu options with arrow keys
        if (inputProvider.isKeyJustPressed(Input.Keys.UP) || inputProvider.isKeyJustPressed(Input.Keys.W)) {
            menuSystem.updateSelection(menuSystem.getSelectedOptionIndex() - 1);
        }
        if (inputProvider.isKeyJustPressed(Input.Keys.DOWN)|| inputProvider.isKeyJustPressed(Input.Keys.S)) {
            menuSystem.updateSelection(menuSystem.getSelectedOptionIndex() + 1);
        }

        // Confirm selection with Enter or Space key
        if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)
            || inputProvider.isKeyJustPressed(Input.Keys.SPACE)) {
            int selectedOption = menuSystem.getSelectedOptionIndex();
            if (!menuSystem.isOptionEnabled(selectedOption)) {
                return; // Ignore if the selected option is disabled
            }

            switch (selectedOption) {
                case 0:
                    // Start the game
                    game.setScreen(new GamePlayScreen(
                            inputProvider,
                            graphicsProvider,
                            game,
                            assetManager, configProvider, 0));
                    break;
                case 1: 
                    // Load game
                    game.setScreen(new GamePlayScreen(
                        inputProvider,
                        graphicsProvider,
                        game,
                        assetManager, configProvider, 1));
                    break;
                case 2:
                    // Show game info screen
                    game.setScreen(new GameInfoControlsScreen(
                                inputProvider,
                                graphicsProvider,
                                game,
                                assetManager,
                                this,
                                null,
                                viewport,
                                configProvider ));
                    break;
                case 3:
                    // Exit the game
                    Gdx.app.exit();
                    break;
            }
        }

        // Reset the inactivity timer if any key is pressed
        if (inputProvider.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            inactivityTimer = 0f;
        }
    }

    /**
     * Update the main menu screen.
     * This method updates the particles and any other game state as needed.
     *
     * @param deltaTimeSeconds The time elapsed since the last frame
     */
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
                    this,
                    configProvider ));

            inactivityTimer = 0f; // Reset the inactivity timer
        }

        // Update the particles system
        particles.update(deltaTimeSeconds, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    /**
     * Draw the main menu screen.
     * This method draws the background, particles, and menu system to the screen.
     */
    @Override
    public void draw() {
        // New background color
        ScreenUtils.clear(Main.PURPLE); // Darker purple background
        viewport.apply(true);

        Camera camera = viewport.getCamera();

        // Draw background elements
        SpriteBatch batch = graphicsProvider.createSpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        // Draw sem-transparent cell texture
        float cellWidth = 779;
        float cellHeight = 397;
        float cellX = camera.position.x - cellWidth / 2;
        float cellY = camera.position.y - cellHeight / 2;

        batch.begin();
        batch.setColor(1, 1, 1, 0.15f);
        batch.draw(cellTexture,
                cellX,
                cellY,
                cellWidth,
                cellHeight);
        batch.setColor(1, 1, 1, 1); // Reset color to white

        // Draw the particles
        particles.draw(batch);
        batch.end();

        // Draw the menu system
        menuSystem.getStage().act();
        menuSystem.getStage().draw();
    }

    /**
     * Get the inactivity timer.
     * @return
     */
    public float getInactivityTimer() {
        return inactivityTimer;
    }

    /**
     * Get the index of the currently selected option.
     *
     * @return the index of the selected option
     */
    public int getSelectedOption() {
        return menuSystem.getSelectedOptionIndex();
    }

    /**
     * Get the number of menu options.
     *
     * @return the number of menu options
     */
    public int getMenuOptionCount() {
        return menuSystem.getMenuOptionCount();
    }

    /**
     * Get the inactivity timeout.
     * This method returns the inactivity timeout value in seconds.
     *
     * @return the inactivity timeout in seconds
     */
    public int getInactivityTimeout() {
        return (int) INACTIVITY_TIMEOUT;
    }

    /**
     * Get the particles object.
     * This method returns the particles object used for rendering particles.
     *
     * @return the particles object
     */
    public Particles getParticles() {
        return particles;
    }

    /**
     * Get the viewport object.
     * This method returns the viewport object used for rendering.
     *
     * @return the viewport object
     */
    public Viewport getViewport() {
        return viewport;
    }

    /**
     * Get the input provider.
     * This method returns the input provider used for handling user input.
     *
     * @return the input provider
     */
    public InputProvider getInputProvider() {
        return inputProvider;
    }

    /**
     * Get the main game instance.
     * This method returns the main game instance.
     *
     * @return the main game instance
     */
    public Main getGame() {
        return game;
    }

    /**
     * Get the asset manager.
     * This method returns the asset manager used for loading and managing assets.
     *
     * @return the asset manager
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }
}
