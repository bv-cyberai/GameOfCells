package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.providers.ConfigProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        "WIP"
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

        this.cellTexture = assetManager.get(AssetFileNames.CELL, Texture.class);

        }

    @Override
    public void show() {
        // Initialize simple back menu
        menuSystem.initializeSplitLayout(
            "GAME INFORMATION", 
            GAME_INFO, 
            CONTROLS,
            INSTRUCTION);
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
        if (inputProvider.isKeyJustPressed(Input.Keys.SPACE) ||
            inputProvider.isKeyJustPressed(Input.Keys.ENTER) ||
            inputProvider.isKeyJustPressed(Input.Keys.ESCAPE)){
            game.setScreen(previousScreen);
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
        particles.update(deltaTimeSeconds, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    @Override
    public void draw() {
        // Clear with a biological-themed color
        ScreenUtils.clear(.1f, .2f, .25f, 1f);

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
        batch.end();

        // Draw particles
        particles.draw(graphicsProvider.createSpriteBatch());

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