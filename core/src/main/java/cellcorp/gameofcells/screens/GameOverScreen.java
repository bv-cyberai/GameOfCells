package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.objects.Stats;
import cellcorp.gameofcells.providers.ConfigProvider;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * GameOver Screen
 *
 * Presents the game over screen, and allows for resetting the game.
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 03/05/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class GameOverScreen implements GameOfCellsScreen {
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

    // game
    private final Main game;

    // providers
    private final InputProvider inputProvider;
    private final AssetManager assetManager;
    private final GraphicsProvider graphicsProvider;
    private ConfigProvider configProvider;

    // Batch/Camera
    private final Viewport viewport;
    private final SpriteBatch spriteBatch;

    // Font and Font Properties
    private BitmapFont font;
    // used to center text.
    private GlyphLayout layout;
    private float textWidth;
    private float textHeight;
    private float xResetCenter = 0;
    private float yCenter = 0;

    private String gameOverString;
    private final Stats stats;

    /**
     * Constructor
     *
     * Creates a game over screen.
     *
     * @param inputProvider    - The input provider.
     * @param assetManager     - The asset manager.
     * @param graphicsProvider - The graphics provider.
     * @param game             - The game.
     * @param configProvider
     */
    public GameOverScreen(InputProvider inputProvider,
                          AssetManager assetManager,
                          GraphicsProvider graphicsProvider,
                          Main game, ConfigProvider configProvider,
                          Stats stats) {

        this.game = game;

        this.inputProvider = inputProvider;
        this.assetManager = assetManager;
        this.graphicsProvider = graphicsProvider;

        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        this.spriteBatch = graphicsProvider.createSpriteBatch();
        this.configProvider = configProvider;

        layout = new GlyphLayout();

        // Change at your own risk, re-centering is painful.
        gameOverString = "               !Game Over!" + '\n' + "Press \'Space\' to try again!";
        this.stats = stats;
    }

    /**
     * Handle Input
     *
     * Resets the game with 'Space'.
     */
    @Override
    public void handleInput(float deltaTimeSeconds) {
        // This perhaps should be the main menu screen, but is most likely up to the
        // client.
        if (inputProvider.isKeyJustPressed(Input.Keys.SPACE)
            || inputProvider.isKeyJustPressed(Input.Keys.R)) {
            game.setScreen(new MainMenuScreen(inputProvider,
            graphicsProvider,
            game,
            assetManager,
            null,
            viewport,
            configProvider));
        }
    }

    /**
     * Update
     */
    @Override
    public void update(float deltaTimeSeconds) {
    }

    /**
     * Draw
     *
     * Draws the game over message.
     */
    @Override
    public void draw() {
        ScreenUtils.clear(new Color(0.08f, 0.05f, 0.10f, 1f)); // Darker purple background

        viewport.apply(true);
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        font.getData().setScale(0.5f); // Set the scale of the font

        // in draw :( but don't see another way.
        layout.setText(font, gameOverString);
        textWidth = layout.width;
        textHeight = layout.height;
        xResetCenter = (VIEW_RECT_WIDTH - textWidth) / 2;
        yCenter = 700;

        spriteBatch.begin();
        font.draw(spriteBatch, gameOverString, xResetCenter, yCenter);
        spriteBatch.end();

        drawStats();
    }

    private void drawStats() {
        var timerString = "Seconds survived: " + Math.round(stats.gameTimer);
        var glucoseString = "Glucose collected: " + stats.glucoseCollected;
        var atpString = "ATP generated: " + stats.atpGenerated;
        var distanceString = "Distance moved: " + Math.round(stats.distanceMoved / 10) + " units";
        var sizeString = "Max size: " + stats.sizeDescription();
        var organellesString = "Organelles purchased: " + stats.organellesPurchased;

        font.getData().setScale(0.25f);
        var x = 300;
        spriteBatch.begin();
        font.draw(spriteBatch, timerString, x, 500);
        font.draw(spriteBatch, glucoseString, x, 450);
        font.draw(spriteBatch, atpString, x, 400);
        font.draw(spriteBatch, distanceString, x, 350);
        font.draw(spriteBatch, sizeString, x, 300);
        font.draw(spriteBatch, organellesString, x, 250);
        spriteBatch.end();
    }

    /**
     * Show
     */
    @Override
    public void show() {
    }

    /**
     * Render
     *
     * Does not call update has there is nothing to update.
     */
    @Override
    public void render(float delta) {
        handleInput(delta);
        draw();
    }

    /**
     * Resize
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height); // Update the viewport
    }

    /**
     * Pause
     */
    @Override
    public void pause() {
    }

    /**
     * Resume
     */
    @Override
    public void resume() {
    }

    /**
     * Hide
     */
    @Override
    public void hide() {
    }

    /**
     * Dispose
     */
    @Override
    public void dispose() {
        font.dispose();
        spriteBatch.dispose();
    }

}
