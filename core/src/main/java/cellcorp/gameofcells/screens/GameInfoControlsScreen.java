package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.providers.ConfigProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
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

    private static final String[] CONTROL_MESSAGE = {"Welcome to Game of Cells!\n" + "Control a cell and explore the microscopic world.\n" +
                "Controls:\n" +
                "Arrow / WASD Keys - Move the cell\n" +
                "Enter / Space - Select/Confirm\n" +
                "Escape / P - Pause/Return to Menu"};
    private static final String INSTRUCTION = "Press any key to return...";

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
    private GlyphLayout layout;

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
            new Stage(graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT)),
            assetManager,
            graphicsProvider
        );

        this.layout = new GlyphLayout();
    }

    @Override
    public void show() {
        // Initialize simple back menu
        menuSystem.initialize("Game Info and Contols", CONTROL_MESSAGE, INSTRUCTION);
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
        particles.dispose();
        menuSystem.clear();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Return to the settings screen if any key is pressed
        if (inputProvider.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            game.setScreen(previousScreen);
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

        // Draw particles
        particles.draw(graphicsProvider.createSpriteBatch());

        // Draw info text
        menuSystem.getStage().getBatch().begin();
        BitmapFont font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        font.getData().setScale(0.375f);
        font.draw(menuSystem.getStage().getBatch(), layout, startX, startY);
        menuSystem.getStage().getBatch().end();

        // Draw menu (back option)
        menuSystem.getStage().act();
        menuSystem.getStage().draw();
    }

    /**
     * Get the position of the message.
     * @return The x coordinate of the message.
     */
    public float getMessagePositionX() {
        return startX;
    }

    /**
     * Get the position of the message.
     * @return The y coordinate of the message.
     */
    public float getMessagePositionY() {
        return startY;
    }
}