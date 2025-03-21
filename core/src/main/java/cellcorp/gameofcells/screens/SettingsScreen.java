package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
 * The screen for adjusting game settings and viewing game info/controls.
 */
public class SettingsScreen implements GameOfCellsScreen {

    // Settings options
    private static final String[] SETTINGS_OPTIONS = {"Game Info & Controls", "Back"};
    private int selectedOption = 0; // Index of the currently selected option

    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;

    private final Main game;
    private final AssetManager assetManager;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;

    private Particles particles;

    public SettingsScreen(
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
                            viewport
                    ));
                    break;
                case 1: // Back
                    game.setScreen(new MainMenuScreen(
                            inputProvider,
                            graphicsProvider,
                            game,
                            assetManager,
                            camera,
                            viewport
                    ));
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
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1); // Dark blue background

        viewport.apply(true);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Draw particles
        particles.draw(spriteBatch);

        // Draw the settings menu
        spriteBatch.begin();
        var font = assetManager.get(AssetFileNames.DEFAULT_FONT, BitmapFont.class);
        font.getData().setScale(2);  // Set the font size

        float menuX = viewport.getWorldWidth() / 2 - 100; // Center the menu
        float menuY = viewport.getWorldHeight() / 2 + 50; // Start position for the menu

        for (int i = 0; i < SETTINGS_OPTIONS.length; i++) {
            // Highlight the selected option
            if (i == selectedOption) {
                font.setColor(Color.YELLOW); // Highlight color
            } else {
                font.setColor(Color.WHITE); // Default color
            }

            // Draw the settings option
            font.draw(spriteBatch, SETTINGS_OPTIONS[i], menuX, menuY - i * 50);
        }

        spriteBatch.end();
    }

}