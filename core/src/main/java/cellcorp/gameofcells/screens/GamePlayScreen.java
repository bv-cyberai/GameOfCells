package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.*;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.providers.GameLoaderSaver;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

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

    // ==== Popup info screens ====
    private final PopupInfoScreen glucoseCollisionPopup;
    private final PopupInfoScreen acidZonePopup;
    private final PopupInfoScreen basicZonePopup;
    private final PopupInfoScreen healAvailablePopup;
    private final PopupInfoScreen cellMembranePopup;
    private final PopupInfoScreen splitCellPopup;

    // ==== Minimap ====
    private final MinimapRenderer minimapRenderer;

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
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    // Objects for rendering the game
    private final Cell playerCell;
    private final GlucoseManager glucoseManager;
    private final ZoneManager zoneManager;
    private final SpawnManager spawnManager;
    //private final HUD hud;
    private final HUD hud;
    // Zoom fields
    private final float originalZoom = 1.2f; // Original zoom level
    private final float targetZoom = 0.8f; // Target zoom level
    private final GameLoaderSaver gameLoaderSaver;
    // Background textures
    private final Texture parallaxFar;
    private final Texture parallaxMid;
    private final Texture parallaxNear;
    private final Texture floatingOverlay; // Texture for simulating fluid game movement
    private final Texture vignetteLowHealth; // Texture for low health warning
    int popupsAllowed = 1;
    private int loadSave;
    private float overlayTime = 0f; // Time for the floating overlay animation
    // Part of game state.
    // Closing the shop and re-opening makes a new one,
    // so if these are in the shop, they won't persist.
    private boolean wasInAcidZone = false; // Whether the cell was in an acid zone last frame
    private float lowEnergyWarningCooldown = 0; // Cooldown for low energy warning
    private boolean isPaused = false; // Whether the game is paused
    // Shake fields
    private float shakeTime = 0; // Time remaining for the shake effect
    private float shakeDuration = 3.0f; // Duration of the shake effect
    private float shakeIntensity = 15f; // Intensity of the shake effect


    /**
     * Constructs the GamePlayScreen.
     *
     * @param inputProvider    Provides user input information.
     * @param graphicsProvider Provide graphics information.
     * @param game             The main game instance.
     * @param configProvider
     * @param loadSave;
     */
    public GamePlayScreen(
        InputProvider inputProvider,
        GraphicsProvider graphicsProvider,
        Main game,
        AssetManager assetManager, ConfigProvider configProvider, int loadSave) {

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
        this.hud = new HUD(graphicsProvider, inputProvider, assetManager, this, stats);
        this.minimapRenderer = new MinimapRenderer(graphicsProvider, 8000f, 8000f, 200f, 200f, camera);
        this.loadSave = loadSave;
        parallaxFar = assetManager.get(AssetFileNames.PARALLAX_FAR, Texture.class);
        parallaxMid = assetManager.get(AssetFileNames.PARALLAX_MID, Texture.class);
        parallaxNear = assetManager.get(AssetFileNames.PARALLAX_NEAR, Texture.class);
        floatingOverlay = assetManager.get(AssetFileNames.FLOATING_OVERLAY, Texture.class);
        vignetteLowHealth = assetManager.get(AssetFileNames.VIGNETTE_LOW_HEALTH, Texture.class);


        try {
            popupsAllowed = configProvider.getIntValue("popupsAllowed");
        } catch (NumberFormatException e) {
            popupsAllowed = 1;
        }
        this.glucoseCollisionPopup = new PopupInfoScreen(
            configProvider,
            graphicsProvider,
            assetManager,
            "glucosePopupMessage",
            PopupInfoScreen.DEFAULT_GLUCOSE_POPUP_MESSAGE,
            new Color(0.8f, 0.33f, 0.0f, 1f),
            this::resumeGame
        );
        this.acidZonePopup = new PopupInfoScreen(
            configProvider,
            graphicsProvider,
            assetManager,
            "dangerPopupMessage",
            PopupInfoScreen.DEFAULT_ACID_ZONE_POPUP_MESSAGE,
            new Color(0.8f, 0.0f, 0.4f, 1f),
            this::resumeGame
        );
        this.basicZonePopup = new PopupInfoScreen(
            configProvider,
            graphicsProvider,
            assetManager,
            "basicPopupMessage",
            PopupInfoScreen.DEFAULT_BASIC_ZONE_POPUP_MESSAGE,
            new Color(0.0f, 0.0f, 0.25f, 1f),
            this::resumeGame
        );
        this.healAvailablePopup = new PopupInfoScreen(
            configProvider,
            graphicsProvider,
            assetManager,
            "healAvailableMessage",
            PopupInfoScreen.DEFAULT_HEAL_AVAILABLE_MESSAGE,
            Color.BLACK,
            this::resumeGame
        );
        this.cellMembranePopup = new PopupInfoScreen(
            configProvider,
            graphicsProvider,
            assetManager,
            "sizeUpgrade1Message",
            PopupInfoScreen.DEFAULT_SIZE_UPGRADE_1_MESSAGE,
            Color.BLACK,
            this::resumeGame
        );
        this.splitCellPopup = new PopupInfoScreen(
            configProvider,
            graphicsProvider,
            assetManager,
            "splitCellMessage",
            PopupInfoScreen.DEFAULT_SPLIT_CELL_MESSAGE,
            Color.BLACK,
            this::resumeGame
        );
        gameLoaderSaver = new GameLoaderSaver(this);
    }

    public void triggerShake(float duration, float intensity) {
        this.shakeTime = duration;
        this.shakeDuration = duration;
        this.shakeIntensity = intensity;
    }

    /**
     * Show the screen.
     */
    @Override
    public void show() {
        if (loadSave == 1 && gameLoaderSaver != null) {
            gameLoaderSaver.loadState();
            loadSave = -1;
        }
        if (loadSave == 0 && gameLoaderSaver != null) {
            GameLoaderSaver.clearSaveFile();
            loadSave = -1;
        }
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

        //"L"oad at any time
        if (inputProvider.isKeyJustPressed(Input.Keys.L)) {
            gameLoaderSaver.loadState();
        }
        //"O"verwrite at any time.
        if (inputProvider.isKeyJustPressed(Input.Keys.O)) {
            gameLoaderSaver.saveState();
        }

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
        if (inputProvider.isKeyJustPressed(Input.Keys.T)) {
            playerCell.applyDamage(20);
        }

        if (inputProvider.isKeyJustPressed(Input.Keys.Y)) {
            reportHealAvailable();
        }

        if (inputProvider.isKeyJustPressed(Input.Keys.N)) {
            playerCell.setHasNucleus(true);
        }
        if (inputProvider.isKeyJustPressed(Input.Keys.NUM_0)) {
            playerCell.setCellHealth(0);
        }

        if (inputProvider.isKeyJustPressed(Input.Keys.U)
            && playerCell.hasNucleus()
            && !playerCell.hasSplit()) {
            split();
        }

        // Only move the cell if the game is not paused
        if (!isPaused && !playerCell.isDying()) {
            playerCell.move(
                deltaTimeSeconds,
                (inputProvider.isKeyPressed(Input.Keys.LEFT) || inputProvider.isKeyPressed(Input.Keys.A)), // Check if the left key is pressed
                (inputProvider.isKeyPressed(Input.Keys.RIGHT) || inputProvider.isKeyPressed(Input.Keys.D)), // Check if the right key is pressed
                (inputProvider.isKeyPressed(Input.Keys.UP) || inputProvider.isKeyPressed(Input.Keys.W)), // Check if the up key is pressed
                (inputProvider.isKeyPressed(Input.Keys.DOWN) || inputProvider.isKeyPressed(Input.Keys.S)) // Check if the down key is pressed

            );
            hud.handleInput();
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

        glucoseCollisionPopup.handleInput(inputProvider, deltaTimeSeconds);
        acidZonePopup.handleInput(inputProvider, deltaTimeSeconds);
        basicZonePopup.handleInput(inputProvider, deltaTimeSeconds);
        healAvailablePopup.handleInput(inputProvider, deltaTimeSeconds);
        cellMembranePopup.handleInput(inputProvider, deltaTimeSeconds);
        splitCellPopup.handleInput(inputProvider, deltaTimeSeconds);
    }

    /**
     * Update the game state.
     *
     * @param deltaTimeSeconds The time since the last frame in seconds.
     */
    @Override
    public void update(float deltaTimeSeconds) {
        if (!isPaused) {

            hud.update(deltaTimeSeconds, playerCell.hasMitochondria());
            zoneManager.update(deltaTimeSeconds);
            glucoseManager.update(deltaTimeSeconds);
            spawnManager.update();

            playerCell.update(deltaTimeSeconds);
            if (playerCell.hasMitochondria() && !healAvailablePopup.wasShown() && (popupsAllowed == 1)) {
                reportHealAvailable();
            }
            if (playerCell.hasSmallSizeUpgrade() && !cellMembranePopup.wasShown() && (popupsAllowed == 1)) {
                reportCellMembrane();
            }
            if (playerCell.hasSplit() && !splitCellPopup.wasShown()) {
                reportSplitCell();
            }
            stats.gameTimer += deltaTimeSeconds;
            overlayTime += deltaTimeSeconds;

            boolean inBasicZone = isInBasicZone(playerCell.getX(), playerCell.getY());
            if (inBasicZone && (popupsAllowed == 1)) {
                reportBasicZoneCollision();
            }

            // Check for acid zone
            boolean inAcidZone = isInAcidZone(playerCell.getX(), playerCell.getY());
            if (inAcidZone && (popupsAllowed == 1)) {
                // We want to show the warning only once when entering the acid zone
                reportAcidZoneCollision();
            }
            //Checks for nucleus, and will autosave if detected.
            gameLoaderSaver.update();
        }
    }

    /**
     * Renders the start screen.
     */
    @Override
    public void draw() {
        setUpDraw();
        drawBackground();
        playerCell.draw(batch, shapeRenderer);
        drawHUD();
        drawMinimap();
    }

    public void setUpDraw() {
        ScreenUtils.clear(Main.PURPLE);

        centerCameraOnCell();
        viewport.apply();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
    }

    public void drawBackground() {

        // Draw parallax background layers
        drawParallax();

        // Draw the floating overlay
        drawFloatingOverlay();

        // Draw core game objects
        zoneManager.draw(batch, shapeRenderer);
        glucoseManager.draw(batch, shapeRenderer);

        if (DEBUG_DRAW_ENABLED) {
            drawChunks(shapeRenderer);
        }
    }

    public void drawHUD() {
        // Draw low ATP warning
        drawLowHealthVignette();

        // Draw the HUD
        hud.draw();

        glucoseCollisionPopup.draw();
        acidZonePopup.draw();
        basicZonePopup.draw();
        healAvailablePopup.draw();
        cellMembranePopup.draw();
        splitCellPopup.draw();
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

    public ShapeRenderer getShapeRenderer() {
        return this.shapeRenderer;
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
        float offsetX = 0;
        float offsetY = 0;

        if (shakeTime > 0) {
            float currentIntensity = (shakeTime / shakeDuration) * shakeIntensity;
            offsetX = MathUtils.random(-currentIntensity, currentIntensity);
            offsetY = MathUtils.random(-currentIntensity, currentIntensity);
            shakeTime -= Gdx.graphics.getDeltaTime();
        }

        // Here we apply the zoom effect
        // The zoom effect is based on the player's cell health
        // If the cell is dead, we zoom out to a maximum of 1.2f
        // If the cell is alive, we zoom in to a minimum of 0.8f
        float zoomProgress = 1f;
        if (playerCell.getCellHealth() <= 0) {
            zoomProgress = Math.min(1f, playerCell.getDeathAnimationTime() / playerCell.getDeathAnimationDuration());
        }

        float currentZoom = MathUtils.lerp(originalZoom, targetZoom, zoomProgress);
        applyZoomToViewport(currentZoom); // Apply the zoom to the viewport


        camera.position.set(playerCell.getX() + offsetX, playerCell.getY() + offsetY, 0);
    }

    /**
     * Applies the zoom to the viewport.
     *
     * @param zoom The zoom level to apply.
     */
    private void applyZoomToViewport(float zoom) {
        float newWidth = VIEW_RECT_WIDTH * zoom;
        float newHeight = VIEW_RECT_HEIGHT * zoom;
        viewport.setWorldSize(newWidth, newHeight);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    /**
     * Draws the parallax background layers.
     * The layers are drawn in order of distance from the camera.
     */
    private void drawParallax() {
        float camX = camera.position.x;
        float camY = camera.position.y;

        batch.begin();

        // Far layer - slowest movement (deep background)
        batch.setColor(1f, 1f, 1f, 0.6f);
        batch.draw(
            parallaxFar,
            camX - viewport.getWorldWidth() * 0.6f,
            camY - viewport.getWorldHeight() * 0.6f,
            viewport.getWorldWidth() * 1.2f,
            viewport.getWorldHeight() * 1.2f
        );

        // Mid layer - moderate movement
        batch.setColor(1f, 1f, 1f, 0.3f);
        batch.draw(
            parallaxMid,
            camX - viewport.getWorldWidth() * 0.55f,
            camY - viewport.getWorldHeight() * 0.55f,
            viewport.getWorldWidth() * 1.1f,
            viewport.getWorldHeight() * 1.1f
        );

        // Near layer - fastest movement (foreground)
        batch.setColor(1f, 1f, 1f, 0.15f);
        batch.draw(
            parallaxNear,
            camX - viewport.getWorldWidth() * 0.52f,
            camY - viewport.getWorldHeight() * 0.52f,
            viewport.getWorldWidth() * 1.04f,
            viewport.getWorldHeight() * 1.04f
        );

        batch.setColor(1f, 1f, 1f, 1f); // Reset to full opacity
        batch.end();
    }

    /**
     * Draws the floating overlay.
     * The overlay simulates fluid game movement.
     */
    private void drawFloatingOverlay() {
        float camX = camera.position.x;
        float camY = camera.position.y;

        // Calculate player movement-based drift
        Vector2 cellVelocity = playerCell.getVelocity(); // Assuming you have a getVelocity()

        // Movement-based drift
        float movementOffsetX = -cellVelocity.x * 2f;
        float movementOffsetY = -cellVelocity.y * 2f;

        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.3f); // 0.2f alpha = subtle
        batch.draw(
            floatingOverlay,
            camX - viewport.getWorldWidth() / 2 + movementOffsetX,
            camY - viewport.getWorldHeight() / 2 + movementOffsetY,
            viewport.getWorldWidth(),
            viewport.getWorldHeight()
        );
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    private void drawLowHealthVignette() {
        if (playerCell.getCellHealth() > 20) return;

        float camX = camera.position.x;
        float camY = camera.position.y;

        float pulse = 0.25f + 0.1f * MathUtils.sin(overlayTime * 2.5f); // use existing time var

        batch.begin();
        batch.setColor(1f, 1f, 1f, pulse);
        batch.draw(
            vignetteLowHealth,
            camX - viewport.getWorldWidth() / 2,
            camY - viewport.getWorldHeight() / 2,
            viewport.getWorldWidth(),
            viewport.getWorldHeight()
        );
        batch.setColor(1f, 1f, 1f, 1f); // reset
        batch.end();
    }

    private void drawMinimap() {
        minimapRenderer.render(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            playerCell.getX(),
            playerCell.getY(),
            zoneManager.getAcidZones().values(),
            zoneManager.getBasicZones().values(),
            glucoseManager.getGlucoseArray()
        );
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

    /**
     * Get the nearest basic zone center.
     * This is used for displaying the basic zone warning.
     */
    protected Vector2 getNearestBasicZoneCenter() {
        ZoneManager zoneManager = spawnManager.getZoneManager(); // assuming you already have spawnManager

        float cellX = playerCell.getX();
        float cellY = playerCell.getY();

        double closestDistance = Double.MAX_VALUE;
        Zone closestZone = null;

        for (Zone zone : zoneManager.getBasicZones().values()) {
            double distance = zone.distanceFrom(cellX, cellY);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestZone = zone;
            }
        }

        if (closestZone != null) {
            return new Vector2(closestZone.x(), closestZone.y());
        }

        // Fallback if no zones found
        return new Vector2(cellX, cellY); // fallback to cell position = no arrow
    }

    /**
     * For test use only.
     */
    public Cell getCell() {
        return this.playerCell;
    }

    /**
     * Ends the game.
     */
    public void endGame() {
        endOfGameUpdateRespawns();
        game.setScreen(new GameOverScreen(
            inputProvider,
            assetManager,
            graphicsProvider,
            game, configProvider, stats
        ));
    }

    /**
     * If the cell has no respawns, clear any existing save files.
     * Otherwise, decrement respawns and save that.
     */
    private void endOfGameUpdateRespawns() {
        var respawns = playerCell.getRespawns();
        if (respawns == 0) {
            GameLoaderSaver.clearSaveFile();
        } else {
            playerCell.setRespawns(respawns - 1);
            gameLoaderSaver.saveRespawns();
        }
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

    /**
     * Reports a collision to this GamePlayScreen.
     * If this is the first collision, shows an info screen.
     */
    public void reportGlucoseCollision() {
        if (!glucoseCollisionPopup.wasShown() && (popupsAllowed == 1)) {
            pauseGame();
            glucoseCollisionPopup.show();
        }
    }

    /**
     * Reports the cell entering an acid zone.
     * This is used for displaying the acid zone warning.
     */
    public void reportAcidZoneCollision() {
        if (!acidZonePopup.wasShown() && playerCell.hasSmallSizeUpgrade() && (popupsAllowed == 1)) {
            pauseGame();
            acidZonePopup.show();
        }
    }

    /**
     * Reports the cell entering a basic zone.
     * This is used for displaying the basic zone warning.
     */
    public void reportBasicZoneCollision() {
        if (!basicZonePopup.wasShown() && (popupsAllowed == 1)) {
            pauseGame();
            basicZonePopup.show();
        }
    }

    public void reportHealAvailable() {
        if (!healAvailablePopup.wasShown() && (popupsAllowed == 1)) {
            pauseGame();
            healAvailablePopup.show();
        }
    }

    public void reportCellMembrane() {
        if (!cellMembranePopup.wasShown() && (popupsAllowed == 1)) {
            pauseGame();
            cellMembranePopup.show();
        }
    }

    public void reportSplitCell() {
        if (!splitCellPopup.wasShown()) {
            pauseGame();
            splitCellPopup.show();
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
     * Check if the basic zone arrow is visible.
     * This is used for checking if the basic zone arrow is visible.
     * For example, if the cell is in a basic zone, it will not show the arrow.
     *
     * @return true if the basic zone arrow is visible, false otherwise.
     */
    public boolean isBasicZoneArrowVisible() {
        return playerCell.getCellATP() <= 30 && !isInBasicZone(playerCell.getX(), playerCell.getY());
    }

    /**
     * Check if the low health warning is visible.
     * This is used for checking if the low health warning is visible.
     * For example, if the cell is in a basic zone, it will not show the arrow.
     *
     * @return true if the low health warning is visible, false otherwise.
     */
    public boolean isLowHealthWarningVisible() {
        return playerCell.getCellHealth() <= 20;
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
    public FitViewport getViewport() {
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
     *
     * @return the game stats
     */
    public Stats getStats() {
        return stats;
    }

    /**
     * For test use only.
     */
    public PopupInfoScreen getGlucoseCollisionPopup() {
        return glucoseCollisionPopup;
    }

    /**
     * For test use only.
     */
    public PopupInfoScreen getAcidZonePopup() {
        return acidZonePopup;
    }

    /**
     * For test use only.
     */
    public PopupInfoScreen getBasicZonePopup() {
        return basicZonePopup;
    }

    /**
     * For test use only.
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * For test use only.
     */
    public SpriteBatch getSpriteBatch() {
        return batch;
    }

    /**
     * For test use only.
     */
    public float getOverlayTime() {
        return overlayTime;
    }

    /**
     * Heal Popup Getter
     *
     * @return the heal Popup
     */
    public PopupInfoScreen getHealAvailablePopup() {
        return healAvailablePopup;
    }

    /**
     * Cell membrane popup getter
     *
     * @return the cell membrane popup
     */
    public PopupInfoScreen getCellMembranePopup() {
        return cellMembranePopup;
    }

    /**
     * Get Split Cell Popup()
     *
     * @return The cell popup
     */
    public PopupInfoScreen getSplitCellPopup() {
        return splitCellPopup;
    }

    /**
     * GameLoaderSaver Getter
     *
     * @return The GameLoaderSaver
     */
    public GameLoaderSaver getGameLoaderSaver() {
        return gameLoaderSaver;
    }

    /**
     * Splits the cell. Transitions to `SplitCellScreen` to play the animation.
     */
    private void split() {
        var splitCellScreen = new SplitCellScreen(game, this);
        game.setScreen(splitCellScreen);
    }

    public ConfigProvider getConfigProvider() {
        return this.configProvider;
    }

    public int getLoadType() {
        return loadSave;
    }
}
