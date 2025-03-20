package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * The screen for displaying game info and controls.
 */
public class GameInfoControlsScreen implements GameOfCellsScreen {

    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;

    private final Main game;
    private final AssetManager assetManager;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;

    private Particles particles;

    public GameInfoControlsScreen(
            InputProvider inputProvider,
            GraphicsProvider graphicsProvider,
            Main game,
            AssetManager assetManager,
            OrthographicCamera camera,
            FitViewport viewport
    ) {
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.game = game;
        this.assetManager = assetManager;
        this.camera = camera;
        this.viewport = viewport;
        this.spriteBatch = graphicsProvider.createSpriteBatch();

        Texture whitePixelTexture = new Texture(AssetFileNames.WHITE_PIXEL);
        particles = new Particles(whitePixelTexture);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        handleInput(delta);
        update(delta);
        draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        spriteBatch.dispose();
        particles.dispose();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Return to the settings screen if any key is pressed
        if (inputProvider.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new SettingsScreen(
                    inputProvider,
                    graphicsProvider,
                    game,
                    assetManager,
                    camera,
                    viewport
            ));
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
        particles.update(deltaTimeSeconds, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    @Override
    public void draw() {
        // Clear the screen with a gradient background
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1); // Dark blue background

        viewport.apply(true);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        particles.draw(spriteBatch);

        // Draw game info and controls
        spriteBatch.begin();
        var font = assetManager.get(AssetFileNames.DEFAULT_FONT, BitmapFont.class);
        font.getData().setScale(1.5f);  // Set the font size

        float startX = viewport.getWorldWidth() / 2 - 200; // Center the text
        float startY = viewport.getWorldHeight() / 2 + 100; // Start position for the text

        // Game Info
        font.draw(spriteBatch, "Game Info:", startX, startY);
        font.draw(spriteBatch, "Welcome to Game of Cells!", startX, startY - 30);
        font.draw(spriteBatch, "Control a cell and explore the microscopic world.", startX, startY - 60);

        // Controls
        font.draw(spriteBatch, "Controls:", startX, startY - 120);
        font.draw(spriteBatch, "Arrow Keys - Move the cell", startX, startY - 150);
        font.draw(spriteBatch, "Enter - Select/Confirm", startX, startY - 180);
        font.draw(spriteBatch, "Escape - Pause/Return to Menu", startX, startY - 210);

        // Press any key to return
        font.draw(spriteBatch, "Press Escape key to return...", startX, startY - 270);

        spriteBatch.end();
    }
}