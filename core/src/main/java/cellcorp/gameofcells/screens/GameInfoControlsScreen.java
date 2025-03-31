package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.providers.ConfigProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * The screen for displaying game info and controls.
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

    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private ConfigProvider configProvider;

    private final Main game;
    private final AssetManager assetManager;
    private final Camera camera;
    private final Viewport viewport;
    private final SpriteBatch spriteBatch;
    private final Texture whitePixelTexture;

    private Particles particles;

    private BitmapFont whiteFont;

    private final GlyphLayout layout;
    private String controlMessage;

    private float startX;
    private float startY;

    public GameInfoControlsScreen(
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

        this.whitePixelTexture = graphicsProvider.createWhitePixelTexture();
        this.particles = new Particles(whitePixelTexture);

        layout = new GlyphLayout();
        controlMessage = "Game Info:\n" +
                "Welcome to Game of Cells!\n" +
                "Control a cell and explore the microscopic world.\n" +
                "Controls:\n" +
                "Arrow Keys - Move the cell\n" +
                "Enter - Select/Confirm\n" +
                "Escape - Pause/Return to Menu\n\n" +
                "Press any key to return...";
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
        viewport.update(width, height);
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
        // Return to the settings screen if any key is pressed
        if (inputProvider.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            game.setScreen(new SettingsScreen(
                    inputProvider,
                    graphicsProvider,
                    game,
                    assetManager,
                    camera,
                    viewport, configProvider ));
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

        viewport.apply(true);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        particles.draw(spriteBatch);

        // Font Setup
        if (whiteFont == null) {
            whiteFont = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
            whiteFont.getData().setScale(0.375f);
            CharSequence cs = controlMessage;
            layout.setText(whiteFont, cs, 0, cs.length(), Color.WHITE, 800, Align.left, true, null);
            // Center the text
            startX = (VIEW_RECT_WIDTH / 2) - (layout.width / 2);
            startY = (VIEW_RECT_HEIGHT / 2) + (layout.height / 2);

        }

        // Draw game info and controls
        spriteBatch.begin();
        whiteFont.draw(spriteBatch, layout, startX, startY);
        spriteBatch.end();
    }
}