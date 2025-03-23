package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.objects.*;
import cellcorp.gameofcells.providers.ConfigProvider;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.GlucoseManager;
import cellcorp.gameofcells.objects.HUD;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

import java.util.ArrayList;

/**
 * GamePlay Screen
 * <p>
 * Contains the main gameplay loop.
 *
 * @author Brendon Vinyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 03/05/2025
 * @course CIS 405
 * @assignment GameOfCells
 */

/**
 * First screen of the application. Displayed after the application is created.
 */
public class GamePlayScreen implements GameOfCellsScreen {
    /**
     * Width of the view rectangle
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_WIDTH = 1920;
    /**
     * Width of the view rectangle
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_HEIGHT = 1080;

    public static final String MESSAGE_GAME = "Game is now playing..."; // Message after starting the screen
    public static final String MESSAGE_SHOP = "Press Q to access the shop screen.";

    /**
     * Set to true to enable debug drawing.
     */
    public static final boolean DEBUG_DRAW_ENABLED = false;

    private final Stage stage;
    private final Main game;

    /// Loads assets during game creation,
    /// then provides loaded assets to draw code, using [AssetManager#get(String)]
    private final AssetManager assetManager;

    /// Gets information about inputs, like held-down keys.
    /// Use this instead of `Gdx.input`, to avoid crashing tests.
    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private ConfigProvider configProvider;

    // ==== The Camera / Viewport Regime ====
    // (Mark is 95% sure the following is correct, from research and review of the
    // classes' code):
    // The LibGDX 2d (= orthographic) `Camera` is responsible for:
    // - Holding a "view rectangle":
    // A rectangle defined in world units, which is the region of the world that is
    // drawn.
    // Stored in the camera as a center-point `position`
    // and a pair `viewportWidth, viewportHeight`
    // The names `viewportWidth` and `viewportHeight` are historical, and somewhat
    // misleading.
    // https://stackoverflow.com/questions/40059360/difference-between-viewport-and-camera-in-libgdx
    // - Providing a `projectionMatrix` to `SpriteBatch` and `ShapeRenderer`
    // If the view rectangle is `(0, 0) .. (1000, 1000)`,
    // the `SpriteBatch` can draw anywhere in that range, and it will be drawn to
    // screen.
    //
    // The LibGDX `Viewport` (_not_ the same as OpenGL viewport) is responsible for:
    // - Storing a `worldWidth` and `worldHeight`
    // These are also questionably-named. They represent the same thing as the
    // camera's view rectangle.
    // When `viewport.apply()` is called in each `draw()` call,
    // the viewport updates the width and height of the camera's view rectangle
    // to match these values.
    // - Fitting the camera's view rectangle to whatever the actual screen size is
    // To do this, it uses the `screenX, screenY, screenWidth, screenHeight` fields,
    // which are updated by calling `viewport.update()`
    //
    // Takeaways:
    // - We shouldn't use `viewport.setWorldWidth()` or `viewport.setWorldHeight()`,
    // unless we want to change the _size_ of the camera's view rectangle.
    // We usually don't want to do that.
    // - Because `viewport.worldWidth` overrides `camera.viewportWidth` every time
    // `viewport.apply()` is called,
    // if we _do_ want to change it, we should always call
    // `viewport.setWorld____()`,
    // instead of changing it directly
    // - To move the position of the camera's view rectangle,
    // we should call `camera.position.set(...)`
    // - For drawing HUD and menu screens, we can use a separate (viewport, camera)
    // pair from the game screen.
    // If we construct the viewport with a certain width and height (say 1920, 1080)
    // and never change _that viewport_'s `worldWidth` / `worldHeight`,
    // we can just draw GUI elements in the range `(0, 0) .. (1920, 1080)`
    // and it will work no matter what we resize the screen to.
    // - Every class that owns a viewport should call `viewport.apply()`
    // at the start of their draw method.
    // Classes that aren't screens should take in the caller's viewport, and
    // re-apply it after.
    // - Classes with a fixed camera position (like HUD and menus)
    // should call `camera.apply(centerCamera = true)`. Others should leave it
    // false.
    private final Camera camera;
    private final Viewport viewport;

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;

    // Objects for rendering the game
    private final Cell cell;
    private final GlucoseManager glucoseManager;
    private final ZoneManager zoneManager;
    private final SpawnManager spawnManager;
    private final HUD hud;

    // Part of game state.
    // Closing the shop and re-opening makes a new one,
    // so if these are in the shop, they won't persist.
    // We'll fix it next week as part of unifying game state.
    public boolean sizeUpgradePurchased = false;
    public boolean hasMitochondria = false;
    private boolean isPaused = false; // Whether the game is paused


    /**
     * Constructs the GamePlayScreen.
     *
     * @param inputProvider    Provides user input information.
     * @param graphicsProvider Provide graphics information.
     * @param game             The main game instance.
     * @param configProvider
     */
    public GamePlayScreen(
        InputProvider inputProvider,
        GraphicsProvider graphicsProvider,
        Main game,
        AssetManager assetManager, ConfigProvider configProvider) {

        this.assetManager = assetManager;
        this.game = game;
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.configProvider = configProvider;

        this.camera = graphicsProvider.createCamera();
        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT, camera);

        this.cell = new Cell(this, assetManager, configProvider);
        this.zoneManager = new ZoneManager(assetManager, cell);
        this.glucoseManager = new GlucoseManager(assetManager, cell);
        this.spawnManager = new SpawnManager(cell, zoneManager, glucoseManager);

        this.shapeRenderer = graphicsProvider.createShapeRenderer();
        this.batch = graphicsProvider.createSpriteBatch();
        this.stage = new Stage(graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT), graphicsProvider.createSpriteBatch());

        this.hud = new HUD(graphicsProvider, assetManager, cell.getMaxHealth(), cell.getMaxATP());

        this.isPaused = false;
    }

    /**
     * Show the screen.
     */
    @Override
    public void show() {
        // This method should be "test-safe".
        // When run, if the game has been constructed with properly-mocked providers,
        // it should not crash test code.

        // Fade in the gameplay screen when returning from the shop
        stage.getRoot().getColor().a = 0; // Start transparent
        stage.addAction(Actions.fadeIn(2f)); // Fade in over 2 seconds
        }

    /// Move the game state forward a tick, handling input, performing updates, and
    /// rendering.
    /// LibGDX combines these into a single method call, but we separate them out
    /// into public methods,
    /// to let us write tests where we call only [GamePlayScreen#handleInput] and
    /// [GamePlayScreen#update]
    @Override
    public void render(float deltaTimeSeconds) {
        handleInput(deltaTimeSeconds);
        update(deltaTimeSeconds);
        draw();
    }

    /**
     * Called when screen size changes.
     * Tells the viewport that the screen size has been changed.
     *
     * @param screenWidth  The new width of the screen in pixels.
     * @param screenHeight The new height of the screen in pixels.
     */
    @Override
    public void resize(int screenWidth, int screenHeight) {
        // Update the viewport with the new screen size.
        viewport.update(screenWidth, screenHeight);
        hud.resize(screenWidth, screenHeight);
    }

    /**
     * Pause the screen.
     */
    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    /**
     * Resume the screen.
     */
    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    /**
     * Hide the screen.
     */
    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    /**
     * Dispose of the screen's assets.
     */
    @Override
    public void dispose() {
        // Destroy screen's assets here.
        cell.dispose(); // dispose cell
        glucoseManager.dispose();
        hud.dispose();
        batch.dispose(); // Dispose of the batch
    }

    /**
     * Handle input from the user and transitions to the GamePlayScreen when Enter
     * is pressed.
     */
    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Move to `ShopScreen` when 's' is pressed.
        if (inputProvider.isKeyJustPressed(Input.Keys.Q)) {
            pauseGame();
            game.setScreen(new ShopScreen(
                    game,
                    inputProvider,
                    graphicsProvider,
                    assetManager,
                    this, // Pass the current screen to the shop screen
                    cell));
        }
        if (inputProvider.isKeyJustPressed(Input.Keys.G)) {
            endGame();
        }

        if (inputProvider.isKeyJustPressed(Input.Keys.E)) {
            cell.addCellATP(20);
        }
        // Will eventually be triggered by cell state
        if (inputProvider.isKeyJustPressed(Input.Keys.Y)) {
            game.setScreen(new PopupInfoScreen(
                    inputProvider, assetManager,
                    graphicsProvider, game,
                    this, configProvider, PopupInfoScreen.Type.danger));
        }

        // Only move the cell if the game is not paused
        if (!isPaused) {
            cell.move(
                deltaTimeSeconds,
                (inputProvider.isKeyPressed(Input.Keys.LEFT )|| inputProvider.isKeyPressed(Input.Keys.A )), // Check if the left key is pressed
                (inputProvider.isKeyPressed(Input.Keys.RIGHT) || inputProvider.isKeyPressed(Input.Keys.D )), // Check if the right key is pressed
                (inputProvider.isKeyPressed(Input.Keys.UP)||inputProvider.isKeyPressed(Input.Keys.W) ), // Check if the up key is pressed
                (inputProvider.isKeyPressed(Input.Keys.DOWN)|| inputProvider.isKeyPressed(Input.Keys.S )) // Check if the down key is pressed

            );
        }
    }

    /**
     * Update the game state.
     *
     * @param deltaTimeSeconds The time since the last frame in seconds.
     */
    @Override
    public void update(float deltaTimeSeconds) {
        if (!isPaused) {
            hud.update(deltaTimeSeconds, cell.getCellHealth(), cell.getCellATP());
            zoneManager.update(deltaTimeSeconds);
            spawnManager.update();
            handleCollisions();
        }
    }

    private void handleCollisions() {
        var glucoseToRemove = new ArrayList<Glucose>();
        for (int i = 0; i < getGlucoseManager().getGlucoseArray().size(); i++) {
            var glucose = getGlucoseManager().getGlucoseArray().get(i);
            if (cell.getcellCircle().overlaps(glucose.getCircle())) {
                glucoseToRemove.add(glucose);
                cell.addCellATP(Glucose.ATP_PER_GLUCOSE);

                // Show the popup on the first glucose collision
                if (!cell.hasShownGlucosePopup()) {
                    game.setScreen(new PopupInfoScreen(
                            inputProvider, assetManager,
                            graphicsProvider, game,
                            this, configProvider,PopupInfoScreen.Type.glucose));
                    cell.setHasShownGlucosePopup(true); // Mark the popup as shown
                }
            }
        }

        getGlucoseManager().getGlucoseArray().removeAll(glucoseToRemove);
    }

    /**
     * Renders the start screen.
     */
    @Override
    public void draw() {
        var font = assetManager.get(AssetFileNames.DEFAULT_FONT, BitmapFont.class);
        font.getData().setScale(2); // Set the font size

        ScreenUtils.clear(Main.PURPLE);

        centerCameraOnCell();
        viewport.apply();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        drawBackground(shapeRenderer);
        drawGameObjects(batch, shapeRenderer);
        hud.draw(viewport);

        if (DEBUG_DRAW_ENABLED) {
            drawChunks(shapeRenderer);
        }
    }

    /**
     * Get the message being displayed.
     *
     * @return The message being displayed.
     */
    public String getMessage() {
        return MESSAGE_GAME;
    }

    /**
     * Get the batch for rendering.
     *
     * @return The batch for rendering.
     */
    public SpriteBatch getBatch() {
        return batch;
    }

    /**
     * Get this screen's glucose manager.
     */
    public GlucoseManager getGlucoseManager() {
        return glucoseManager;
    }

    /**
     * Center's the camera's view rectangle on the cell.
     */
    private void centerCameraOnCell() {
        camera.position.set(cell.getX(), cell.getY(), 0);
    }

    /**
     * Draw a background with grid lines.
     */
    private void drawBackground(ShapeRenderer shapeRenderer) {
        // It makes ShapeRenderer alpha work. Don't ask. I hate libgdx.
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        var callerColor = shapeRenderer.getColor();

        shapeRenderer.setColor(1, 1, 1, 0.5f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        var distanceBetweenGridLines = 100f;
        int rows = (int) Math.ceil(VIEW_RECT_HEIGHT / distanceBetweenGridLines) + 1;
        int cols = (int) Math.ceil(VIEW_RECT_WIDTH / distanceBetweenGridLines) + 1;

        float rowWidth = VIEW_RECT_WIDTH + 2f;
        float rowHeight = VIEW_RECT_HEIGHT / 500f;
        float colWidth = VIEW_RECT_WIDTH / 500f;
        float colHeight = VIEW_RECT_HEIGHT + 2f;

        float xMin = camera.position.x - (float) VIEW_RECT_WIDTH / 2 - 1;
        float yMin = camera.position.y - (float) VIEW_RECT_HEIGHT / 2 - 1;

        float yStart = yMin - yMin % distanceBetweenGridLines;
        for (int row = 0; row < rows; row++) {
            float yOffset = row * distanceBetweenGridLines;
            float y = yStart + yOffset;
            shapeRenderer.rect(xMin, y, rowWidth, rowHeight);
        }

        float xStart = xMin - xMin % distanceBetweenGridLines;
        for (int col = 0; col < cols; col++) {
            float xOffset = col * distanceBetweenGridLines;
            float x = xStart + xOffset;
            shapeRenderer.rect(x, yMin, colWidth, colHeight);
        }
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND); // Idk.
        shapeRenderer.setColor(callerColor);
    }

    private void drawGameObjects(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        zoneManager.draw(batch, shapeRenderer);
        batch.begin();
        glucoseManager.draw(batch);
        cell.draw(batch);
        batch.end();
    }

    /**
     * Draw chunk borders.
     */
    private void drawChunks(ShapeRenderer shapeRenderer) {
        // Get current chunk. Draw it and all adjacent chunks.

        var currentChunk = Chunk.fromWorldCoords(cell.getX(), cell.getY());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        for (int row = currentChunk.row() - 1; row < currentChunk.row() + 2; row++) {
            for (int col = currentChunk.col() - 1; col < currentChunk.col() + 2; col++) {
                var rect = new Chunk(row, col).toRectangle();
                shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            }
        }
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.end();
    }

    /**
     * Hud Getter (Testing method)
     *
     * @return The Screen Hud.
     */
    public HUD getHud() {
        return hud;
    }

    /**
     * For test use only.
     */
    public Cell getCell() {
        return this.cell;
    }

    public void endGame() {
        game.setScreen(new GameOverScreen(
                inputProvider,
                assetManager,
                graphicsProvider,
                game,configProvider));
    }

    /**
     * Pause the game.
     */
    public void pauseGame() {
        // Pause game logic (e.g., stop updating entities)
        isPaused = true;
    }

    /**
     * Resume the game.
     */
    public void resumeGame() {
        // Resume game logic (e.g., start updating entities)
        isPaused = false;
    }
}
