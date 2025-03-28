package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.providers.ConfigProvider;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.providers.GraphicsProvider;
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
    private static final String[] MENU_OPTIONS = { "Start Game", "Settings", "Exit" };
    private int selectedOption = 0; // Index of the currently selected option

    private static final float INACTIVITY_TIMEOUT = 10f; // 5 seconds of inactivity

    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private final Camera camera;
    private ConfigProvider configProvider;

    private final Main game;
    private final AssetManager assetManager;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;

    private BitmapFont whiteFont;
    private BitmapFont yellowFont;
    private GlyphLayout layout;

    private float inactivityTimer = 0f;

    // Particles system
    private Particles particles;

    public MainMenuScreen(
        InputProvider inputProvider,
        GraphicsProvider graphicsProvider,
        Main game,
        AssetManager assetManager,
        Camera camera,
        Viewport viewport, ConfigProvider configProvider) {
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.configProvider = configProvider;
        this.game = game;
        this.assetManager = assetManager;
        this.camera = camera;
        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        this.spriteBatch = graphicsProvider.createSpriteBatch();

        // Load white pixel texture and initialize particles
        Texture whitePixelTexture = assetManager.get(AssetFileNames.WHITE_PIXEL, Texture.class);
        this.particles = new Particles(whitePixelTexture);

        //Config provider can be 'constructed' anywhere, this is useful as game objects will need access
        //to it.
//        configProvider = ConfigProvider.getInstance();
        //Config is loaded here to avoid issue with GDX files, it is also the first possible
        //location that would use any user defined values.
//        configProvider.loadConfig();


        layout = new GlyphLayout();
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
        this.spriteBatch.dispose();
        this.particles.dispose();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Navigate menu options with arrow keys
        if (inputProvider.isKeyJustPressed(Input.Keys.UP) || inputProvider.isKeyJustPressed(Input.Keys.W)) {
            selectedOption = (selectedOption - 1 + MENU_OPTIONS.length) % MENU_OPTIONS.length;
        }
        if (inputProvider.isKeyJustPressed(Input.Keys.DOWN)|| inputProvider.isKeyJustPressed(Input.Keys.S)) {
            selectedOption = (selectedOption + 1) % MENU_OPTIONS.length;
        }

        // Confirm selection with Enter key
        if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (selectedOption) {
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
                    game.setScreen(new SettingsScreen(
                            inputProvider,
                            graphicsProvider,
                            game,
                            assetManager,
                            camera,
                            viewport,configProvider ));
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
        // Clear the screen with a gradient background
        ScreenUtils.clear(.157f, .115f, .181f, 1f); // Dark purple background

        if (whiteFont == null || yellowFont == null) {
            whiteFont = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
            whiteFont.getData().setScale(0.375f); // Set the scale of the font
            yellowFont = assetManager.get(AssetFileNames.HUD_FONT_YELLOW, BitmapFont.class);
            yellowFont.getData().setScale(0.375f); // Set the scale of the font
            layout.setText(whiteFont, MENU_OPTIONS[0]);
        }

        viewport.apply(true);
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        // Draw the particles system
        particles.draw(spriteBatch);

        // Draw the instructional message
        spriteBatch.begin();
        float menuX = (viewport.getWorldWidth() - layout.width) / 2; // Center the menu
        float menuY = ((viewport.getWorldHeight()) / 2) + 50 + layout.height; // Start position for the menu

        for (int i = 0; i < MENU_OPTIONS.length; i++) {
            // Highlight the selected option
            if (i == selectedOption) {
                yellowFont.draw(spriteBatch, MENU_OPTIONS[i], menuX, menuY - i * 50); // Yellow for selected option
            } else {
                whiteFont.draw(spriteBatch, MENU_OPTIONS[i], menuX, menuY - i * 50); // Default color
            }
        }

        spriteBatch.end();
    }
}
