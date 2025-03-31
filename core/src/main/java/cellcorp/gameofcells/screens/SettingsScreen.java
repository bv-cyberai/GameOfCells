package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.providers.ConfigProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * The screen for adjusting game settings and viewing game info/controls.
 */
public class SettingsScreen implements GameOfCellsScreen {

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

    // Settings options
    private static final String[] SETTINGS_OPTIONS = { "Game Info & Controls", "Back" };
    private int selectedOption = 0; // Index of the currently selected option

    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private ConfigProvider configProvider;

    private final Main game;
    private final AssetManager assetManager;
    private final Camera camera;
    private final Viewport viewport;
    private final SpriteBatch spriteBatch;
    private final Texture whitePixelTexture;

    private BitmapFont whiteFont;
    private BitmapFont yellowFont;
    private GlyphLayout layout;

    private Particles particles;


    public SettingsScreen(
            InputProvider inputProvider,
            GraphicsProvider graphicsProvider,
            Main game,
            AssetManager assetManager,
            Camera camera,
            Viewport viewport,
            ConfigProvider configProvider
    ) {
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.configProvider = configProvider;
        this.game = game;
        this.assetManager = assetManager;
        this.camera = camera;
        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        this.spriteBatch = graphicsProvider.createSpriteBatch();

        // Load white pixel texture and initialize particles
        this.whitePixelTexture = graphicsProvider.createWhitePixelTexture();
        this.particles = new Particles(whitePixelTexture);

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
        viewport.update(width, height, true);
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
        spriteBatch.dispose();
        particles.dispose();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Navigate settings options with arrow keys
        if (inputProvider.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + SETTINGS_OPTIONS.length) % SETTINGS_OPTIONS.length;
        }
        if (inputProvider.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % SETTINGS_OPTIONS.length;
        }

        // Confirm selection with Enter key
        if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (selectedOption) {
                case 0: // Game Info & Controls
                    game.setScreen(new GameInfoControlsScreen(
                            inputProvider,
                            graphicsProvider,
                            game,
                            assetManager,
                            camera,
                            viewport,configProvider ));
                    break;
                case 1: // Back
                    game.setScreen(new MainMenuScreen(
                            inputProvider,
                            graphicsProvider,
                            game,
                            assetManager,
                            camera,
                            viewport, configProvider));
                    break;
            }
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
        particles.update(deltaTimeSeconds, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    @Override
    public void draw() {
        // Clear the screen with a gradient background
        ScreenUtils.clear(.157f, .115f, .181f, 1f); // Dark blue background

        if (whiteFont == null || yellowFont == null) {
            whiteFont = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
            whiteFont.getData().setScale(0.375f); // Set the scale of the font
            yellowFont = assetManager.get(AssetFileNames.HUD_FONT_YELLOW, BitmapFont.class);
            yellowFont.getData().setScale(0.375f); // Set the scale of the font

            // used to get the width of the longest option for centering.
            layout.setText(whiteFont, SETTINGS_OPTIONS[0]);
        }

        viewport.apply(true);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Draw particles
        particles.draw(spriteBatch);

        // Draw the settings menu
        spriteBatch.begin();

        float menuX = (viewport.getWorldWidth() - layout.width) / 2; // Center the menu
        float menuY = ((viewport.getWorldHeight()) / 2) + 50 + layout.height; // Start position for the menu

        for (int i = 0; i < SETTINGS_OPTIONS.length; i++) {
            // Highlight the selected option
            if (i == selectedOption) {
                yellowFont.draw(spriteBatch, SETTINGS_OPTIONS[i], menuX, menuY - i * 50);
            } else {
                whiteFont.draw(spriteBatch, SETTINGS_OPTIONS[i], menuX, menuY - i * 50);
            }
        }

        spriteBatch.end();
    }

}
