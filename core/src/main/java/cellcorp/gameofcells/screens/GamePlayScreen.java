package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.*;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * GamePlay Screen
 * <p>
 * Contains the main gameplay loop.
 *
 * @author Brendon Vineyard / vineyabn207
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
    public static final String MESSAGE_PAUSE = "Press ESC to pause";
    /**
     * Set to true to enable debug drawing.
     */
    public static final boolean DEBUG_DRAW_ENABLED = false;
    private static final float LOW_ENERGY_COOLDOWN = 10f; // 10 seconds cooldown for low energy warning
    public final Stats stats = new Stats();
    private final Stage stage;
    private final Main game;
    /// Loads assets during game creation,
    /// then provides loaded assets to draw code, using [AssetManager#get(String)]
    private final AssetManager assetManager;
    /// Gets information about inputs, like held-down keys.
    /// Use this instead of `Gdx.input`, to avoid crashing tests.
    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private final ConfigProvider configProvider;
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
    private final Cell playerCell;
    private final GlucoseManager glucoseManager;
    private final ZoneManager zoneManager;
    private final SpawnManager spawnManager;
    //private final HUD hud;
    private final HUD hud;
    private final PopupInfoScreen infoPopup;
    private final boolean wasInBasicZone = false; // Whether the cell was in a basic zone last frame
    // Part of game state.
    // Closing the shop and re-opening makes a new one,
    // so if these are in the shop, they won't persist.
    public boolean sizeUpgradePurchased = false;
    public boolean hasMitochondria = false;
    private boolean wasInAcidZone = false; // Whether the cell was in an acid zone last frame
    private boolean hasShownEnergyWarning = false; // Tracks if the energy warning has been shown
    private float lowEnergyWarningCooldown = 0; // Cooldown for low energy warning
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

        this.playerCell = new Cell(this, assetManager, configProvider);
        this.zoneManager = new ZoneManager(assetManager, playerCell);
        this.glucoseManager = new GlucoseManager(assetManager, this, zoneManager, playerCell);
        this.spawnManager = new SpawnManager(playerCell, zoneManager, glucoseManager);

        this.shapeRenderer = graphicsProvider.createShapeRenderer();
        this.batch = graphicsProvider.createSpriteBatch();
        this.stage = new Stage(graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT), graphicsProvider.createSpriteBatch());
        this.hud = new HUD(graphicsProvider, assetManager, this, stats);
        this.infoPopup = new PopupInfoScreen(graphicsProvider, assetManager, configProvider, inputProvider, viewport, hud, this::resumeGame);
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

        if (infoPopup.isVisible()) {
            infoPopup.render(deltaTimeSeconds);
        }
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
        infoPopup.resize(screenWidth, screenHeight);
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
        playerCell.dispose(); // dispose cell
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
                playerCell
            ));
        }
        if (inputProvider.isKeyJustPressed(Input.Keys.G)) {
            endGame();
        }

        if (inputProvider.isKeyJustPressed(Input.Keys.E)) {
            playerCell.addCellATP(20);
        }

        if (inputProvider.isKeyJustPressed(Input.Keys.F)) {
            playerCell.removeCellATP(20);
        }

        if (inputProvider.isKeyJustPressed(Input.Keys.H)) {
            if (playerCell.hasMitochondria()) {
                playerCell.healDamage();
            }
        }

        // Only move the cell if the game is not paused
        if (!isPaused && !infoPopup.isVisible()) {
            playerCell.move(
                deltaTimeSeconds,
                (inputProvider.isKeyPressed(Input.Keys.LEFT) || inputProvider.isKeyPressed(Input.Keys.A)), // Check if the left key is pressed
                (inputProvider.isKeyPressed(Input.Keys.RIGHT) || inputProvider.isKeyPressed(Input.Keys.D)), // Check if the right key is pressed
                (inputProvider.isKeyPressed(Input.Keys.UP) || inputProvider.isKeyPressed(Input.Keys.W)), // Check if the up key is pressed
                (inputProvider.isKeyPressed(Input.Keys.DOWN) || inputProvider.isKeyPressed(Input.Keys.S)) // Check if the down key is pressed

            );
        }
        // Returns the player to the main Menu Screen
        if (inputProvider.isKeyJustPressed(Input.Keys.M)) {
            game.setScreen(new MainMenuScreen(inputProvider, graphicsProvider, game, assetManager, camera, viewport, configProvider));
        }

        // Pause the game when the ESC key is pressed
        if (inputProvider.isKeyJustPressed(Input.Keys.ESCAPE) || inputProvider.isKeyJustPressed(Input.Keys.P)) {
            pauseGame();
            game.setScreen(new PauseScreen(
                this,
                inputProvider,
                graphicsProvider,
                game,
                assetManager,
                camera,
                configProvider
            ));
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
            hud.update(deltaTimeSeconds);
            zoneManager.update(deltaTimeSeconds);
            glucoseManager.update(deltaTimeSeconds);
            spawnManager.update();

            playerCell.update(deltaTimeSeconds);
            // Existing out-of-energy checking
            if (playerCell.getCellATP() == 0 && !hasShownEnergyWarning) {
//                hud.showEnergyEqualsZeroWarning();
                hasShownEnergyWarning = true;
            } else if (playerCell.getCellATP() > 20) {
                hasShownEnergyWarning = false; // Reset the warning if ATP is above 0
            }
            if (playerCell.hasMitochondria() && !infoPopup.hasShownHealAvailablePopup()) {
                reportHealAvailable();
            }
            stats.gameTimer += deltaTimeSeconds;

            boolean inBasicZone = isInBasicZone(playerCell.getX(), playerCell.getY());
            if (inBasicZone) {
                reportBasicZoneCollision();
            }

            // Check for acid zone
            boolean inAcidZone = isInAcidZone(playerCell.getX(), playerCell.getY());
            if (inAcidZone) {
                // We want to show the warning only once when entering the acid zone
                reportAcidZoneCollision();
            }
        }
    }

    /**
     * Renders the start screen.
     */
    @Override
    public void draw() {
        ScreenUtils.clear(Main.PURPLE);

        centerCameraOnCell();
        viewport.apply();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        if (DEBUG_DRAW_ENABLED) {
            drawBackground(shapeRenderer);
        }
        drawGameObjects(batch, shapeRenderer);
        hud.draw();

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
        camera.position.set(playerCell.getX(), playerCell.getY(), 0);
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
        glucoseManager.draw(batch, shapeRenderer);
        playerCell.draw(batch, shapeRenderer);
    }

    /**
     * Draw chunk borders.
     */
    private void drawChunks(ShapeRenderer shapeRenderer) {
        // Get current chunk. Draw it and all adjacent chunks.
        var currentChunk = Chunk.fromWorldCoords(playerCell.getX(), playerCell.getY());
        var adjacentChunks = currentChunk.adjacentChunks();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        for (var chunk : adjacentChunks) {
            var rect = chunk.toRectangle();
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.end();
    }

    public void showPopup(PopupInfoScreen.Type type) {
        // Pause the game when showing the popup
        pauseGame();
        infoPopup.show(type);
    }

    /**
     * For test use only.
     */
    public Cell getCell() {
        return this.playerCell;
    }

    public void endGame() {
        game.setScreen(new GameOverScreen(
            inputProvider,
            assetManager,
            graphicsProvider,
            game, configProvider, stats
        ));
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
        if (infoPopup.isVisible()) {
            infoPopup.setVisible(false);
        }
    }

    /**
     * Reports a collision to this GamePlayScreen.
     * If this is the first collision, shows an info screen.
     */
    public void reportGlucoseCollision() {
        if (!infoPopup.hasShownGlucosePopup()) {
            showPopup(PopupInfoScreen.Type.glucose);
            infoPopup.setHasShownGlucosePopup(true); // Mark the popup as shown
        }
    }

    /**
     * Reports the cell entering an acid zone.
     * This is used for displaying the acid zone warning.
     */
    public void reportAcidZoneCollision() {
        if (!infoPopup.hasShownAcidZonePopup()) {
            showPopup(PopupInfoScreen.Type.danger);
            infoPopup.setHasShownAcidZonePopup(true); // Mark the popup as shown
        }
    }

    public void reportHealAvailable() {
        if (!infoPopup.hasShownHealAvailablePopup()) {
            showPopup(PopupInfoScreen.Type.heal);
            infoPopup.setHasShownHealAvailablePopup(true);
        }
    }

    /**
     * Reports the cell entering a basic zone.
     * This is used for displaying the basic zone warning.
     */
    public void reportBasicZoneCollision() {
        if (!infoPopup.hasShownBasicZonePopup()) {
            showPopup(PopupInfoScreen.Type.basic);
            infoPopup.setHasShownBasicZonePopup(true); // Mark the popup as shown
        }
    }

    /**
     * Check if the cell is in an acid zone.
     * This is used for checking if the cell is in an acid zone.
     * For example, if the cell is in an acid zone, it will take damage.
     *
     * @param x
     * @param y
     * @return
     */
    protected boolean isInAcidZone(float x, float y) {
        return zoneManager.distanceToNearestAcidZone(x, y)
            .map(d -> d <= Zone.ZONE_RADIUS)
            .orElse(false);
    }

    /**
     * Check if the cell is in a basic zone.
     * This is used for checking if the cell is in a basic zone.
     *
     * @param x
     * @param y
     * @return
     */
    protected boolean isInBasicZone(float x, float y) {
        return zoneManager.distanceToNearestBasicZone(x, y)
            .map(d -> d <= Zone.ZONE_RADIUS)
            .orElse(false);
    }

    /**
     * Get the wasInAcidZone flag.
     * This is used for checking if the cell was in an acid zone last frame.
     * For example, if the cell was in an acid zone last frame, it will take damage.
     *
     * @return true if the cell was in an acid zone last frame, false otherwise.
     * @see #wasInAcidZone
     */
    public boolean getWasInAcidZone() {
        return wasInAcidZone;
    }

    /**
     * Set the wasInAcidZone flag.
     * This is used for checking if the cell was in an acid zone last frame.
     * For example, if the cell was in an acid zone last frame, it will take damage.
     *
     * @param wasInAcidZone
     */
    public void setWasInAcidZone(boolean wasInAcidZone) {
        this.wasInAcidZone = wasInAcidZone;
    }

    /**
     * Get the isPaused flag.
     * This is used for checking if the game is paused.
     * For example, if the game is paused, it will not update the game state.
     *
     * @return true if the game is paused, false otherwise.
     * @see #isPaused
     */
    public boolean getIsPaused() {
        return isPaused;
    }

    /**
     * Set the isPaused flag.
     * This is used for checking if the game is paused.
     * For example, if the game is paused, it will not update the game state.
     *
     * @param isPaused
     * @see #isPaused
     */
    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    /**
     * Get the zone manager.
     * This is used for getting the zone manager.
     * For example, if the zone manager is not null, it will be used to spawn
     * objects.
     *
     * @return the zone manager.
     */
    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    /**
     * Get the spawn manager.
     * This is used for getting the spawn manager.
     * For example, if the spawn manager is not null, it will be used to spawn
     * objects.
     *
     * @return the spawn manager.
     */
    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    /**
     * Get the asset manager.
     * This is used for getting the asset manager.
     * For example, if the asset manager is not null, it will be used to load
     * assets.
     *
     * @return the asset manager.
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * Get the info popup.
     * This is used for getting the info popup.
     * For example, if the info popup is not null, it will be used to show
     * information to the user.
     *
     * @return the info popup.
     */
    public PopupInfoScreen getInfoPopup() {
        return infoPopup;
    }

    /**
     * Get the hasShownEnergyWarning flag.
     * This is used for checking if the energy warning has been shown.
     * For example, if the energy warning has been shown, it will not show it again.
     *
     * @return
     * @see #hasShownEnergyWarning
     */
    public boolean isHasShownEnergyWarning() {
        return hasShownEnergyWarning;
    }

    /**
     * Set the hasShownEnergyWarning flag.
     * This is used for checking if the energy warning has been shown.
     * For example, if the energy warning has been shown, it will not show it again.
     *
     * @param hasShownEnergyWarning
     * @see #hasShownEnergyWarning
     */
    public void setHasShownEnergyWarning(boolean hasShownEnergyWarning) {
        this.hasShownEnergyWarning = hasShownEnergyWarning;
    }

    /**
     * Get the lowEnergyWarningCooldown.
     * This is used for checking if the low energy warning cooldown is active.
     * For example, if the low energy warning cooldown is active, it will not show
     * the energy warning again.
     *
     * @return the low energy warning cooldown
     * @see #lowEnergyWarningCooldown
     */
    public float getLowEnergyWarningCooldown() {
        return lowEnergyWarningCooldown;
    }

    /**
     * Set the lowEnergyWarningCooldown.
     * This is used for checking if the low energy warning cooldown is active.
     * For example, if the low energy warning cooldown is active, it will not show
     * the energy warning again.
     *
     * @param lowEnergyWarningCooldown
     * @see #lowEnergyWarningCooldown
     */
    public void setLowEnergyWarningCooldown(float lowEnergyWarningCooldown) {
        this.lowEnergyWarningCooldown = lowEnergyWarningCooldown;
    }

    /**
     * Get the isPaused flag.
     * This is used for checking if the game is paused.
     * For example, if the game is paused, it will not update the game state.
     *
     * @return true if the game is paused, false otherwise.
     * @see #isPaused
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Set the paused state of the game.
     * This is used for checking if the game is paused.
     *
     * @param paused
     * @return true if the game is paused, false otherwise.
     * @see #isPaused
     */
    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    /**
     * Get the viewport.
     * This is used for getting the viewport.
     * For example, if the viewport is not null, it will be used to set the camera
     * position.
     *
     * @return the viewport.
     */
    public Viewport getViewport() {
        return viewport;
    }

    /**
     * For test use only.
     */
    public HUD getHUD() {
        return this.hud;
    }

    public GraphicsProvider getGraphicsProvider() {
        return this.graphicsProvider;
    }

    /**
     * Get the GamePlayScreen height.
     *
     * @return the GamePlayScreen height.
     */
    public int getWorldHeight() {
        return VIEW_RECT_HEIGHT;
    }


    /**
     * Stats Getter
     * Returns the current stats class
     * @return the game stats
     */
    public Stats getStats() {
        return stats;
    }

}
