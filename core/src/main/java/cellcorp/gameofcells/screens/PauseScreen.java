package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.providers.ConfigProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader.Config;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

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
    private final AssetManager assetManager;
    private final Viewport viewport;
    private final Particles particles;
    private final MenuSystem menuSystem;
    private final SpriteBatch batch;

    public PauseScreen(
            GamePlayScreen gamePlayScreen,
            InputProvider inputProvider,
            GraphicsProvider graphicsProvider,
            Main game,
            AssetManager assetManager,
            ConfigProvider configProvider) {

        this.gamePlayScreen = gamePlayScreen;
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.configProvider = configProvider;
        this.game = game;
        this.assetManager = assetManager;
        this.viewport = graphicsProvider.createFitViewport(
            GamePlayScreen.VIEW_RECT_WIDTH,
            GamePlayScreen.VIEW_RECT_HEIGHT
        );

        this.particles = new Particles(graphicsProvider.createWhitePixelTexture());
        this.menuSystem = new MenuSystem(
            new Stage(graphicsProvider.createFitViewport(VIEW_RECT_WIDTH,VIEW_RECT_HEIGHT)),
            assetManager,
            graphicsProvider
        );
        this.batch = graphicsProvider.createSpriteBatch();
    }

    @Override
    public void show() {
        menuSystem.initialize("Paused", PAUSE_OPTIONS, INSTRUCTIONS);
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
        // Pause logic if needed
    }

    @Override
    public void resume() {
        // Resume logic if needed
    }

    @Override
    public void hide() {
        // Hide logic if needed
    }

    @Override
    public void dispose() {
        menuSystem.clear();
        particles.dispose();
        batch.dispose();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Navigate menu options
        if (inputProvider.isKeyJustPressed(Input.Keys.UP)) {
            menuSystem.updateSelection(menuSystem.getSelectedOptionIndex() - 1);
        }
        if (inputProvider.isKeyJustPressed(Input.Keys.DOWN)) {
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
        if (inputProvider.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gamePlayScreen.resumeGame();
            game.setScreen(gamePlayScreen);
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
        particles.update(deltaTimeSeconds, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    @Override
    public void draw() {
        // Semi-transparent background overlay
        ScreenUtils.clear(0, 0, 0, 0.5f); // Semi-transparent black background

        // First draw the gameplay screen (paused)
        gamePlayScreen.draw();

        // Then draw our pause menu overlay
        viewport.apply(true);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        // Draw the particles
        particles.draw(batch);

        // Draw the menu
        menuSystem.getStage().act();
        menuSystem.getStage().draw();
    }
}
