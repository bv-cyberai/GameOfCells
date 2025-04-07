package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * The screen for the main menu.
 * When the game starts this screen is loaded first.
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
        "START GAME", 
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
    private final Texture cellTexture;
    private final Texture wasdArrowsIcon;
    private final Texture spaceEnterIcon;

    private float inactivityTimer = 0f;

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
                new Stage(graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT)),
                assetManager,
                graphicsProvider
        );

        this.cellTexture = assetManager.get(AssetFileNames.CELL, Texture.class);
        this.wasdArrowsIcon = assetManager.get(AssetFileNames.WASD_ARROWS_ICON, Texture.class);
        this.spaceEnterIcon = assetManager.get(AssetFileNames.SPACE_ENTER_ICON, Texture.class);

        //Config provider can be 'constructed' anywhere, this is useful as game objects will need access
        //to it.
//        configProvider = ConfigProvider.getInstance();
        //Config is loaded here to avoid issue with GDX files, it is also the first possible
        //location that would use any user defined values.
//        configProvider.loadConfig();

    }

    @Override
    public void show() {
        menuSystem.initializeMainMenu(
            "GAME OF CELLS", 
            MENU_OPTIONS, 
            INSTRUCTIONS,
            wasdArrowsIcon,
            spaceEnterIcon
        );
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
        viewport.update(width, height, true); // Update the viewport
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
        particles.dispose();
        menuSystem.clear();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
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
            switch (menuSystem.getSelectedOptionIndex()) {
                case 0:
                    // Start the game
                    game.setScreen(new GamePlayScreen(
                            inputProvider,
                            graphicsProvider,
                            game,
                            assetManager, configProvider ));
                    break;
                case 1: // Settings
                    // Open the settings screen
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
                case 2:
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
                    assetManager,configProvider ));
        }

        // Update the particles system
        particles.update(deltaTimeSeconds, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    @Override
    public void draw() {
        // New background color
        ScreenUtils.clear(.05f, .15f, .2f, 1f); // Deep teal background
        viewport.apply(true);

        // Draw background elements
        SpriteBatch batch = graphicsProvider.createSpriteBatch();
        batch.begin();
        // Draw sem-transparent cell texture
        batch.setColor(1, 1, 1, 0.15f);
        batch.draw(cellTexture,
                VIEW_RECT_WIDTH / 2 - 250,
                VIEW_RECT_HEIGHT / 2 - 250,
                500, 500);
        batch.setColor(1, 1, 1, 1); // Reset color to white
        batch.end();

        // Draw the particles
        particles.draw(graphicsProvider.createSpriteBatch());

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
}
