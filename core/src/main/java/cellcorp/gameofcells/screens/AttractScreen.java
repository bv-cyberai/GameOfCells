package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Glucose;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Attract Screen
 *
 * Runs a simulation of the game displayed when the player
 * is idle on the main menu screen
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 03/05/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class AttractScreen implements GameOfCellsScreen {
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
    private static final int NUM_GLUCOSE = 10;
    private static final String INSTRUCTION_TEXT = "Press any key to start";

    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private final ConfigProvider configProvider;
    private final Main game;
    private final AssetManager assetManager;
    private final GameOfCellsScreen previousScreen;
    private final Camera camera;
    private final Viewport viewport;
    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final MenuSystem menuSystem;
    private final Particles particles;

    private Cell cell;
    private List<Glucose> glucoseList;
    private final Random random;
    private float animationTime = 0f;
    private boolean isSimulationRunning = true;
    private float targetX, targetY;
    private float cellSpeed = 100f;

    /**
     * Create an attract screen.
     */
    public AttractScreen(
        InputProvider inputProvider,
        GraphicsProvider graphicsProvider,
        Main game,
        AssetManager assetManager,
        GameOfCellsScreen previousScreen,
        ConfigProvider configProvider) {

        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.configProvider = configProvider;
        this.game = game;
        this.assetManager = assetManager;
        this.previousScreen = previousScreen;

        this.camera = graphicsProvider.createCamera();
        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT, camera);

        this.spriteBatch = graphicsProvider.createSpriteBatch();
        this.shapeRenderer = graphicsProvider.createShapeRenderer();
        this.random = new Random();

        this.particles = new Particles(graphicsProvider.createWhitePixelTexture());
        this.menuSystem = new MenuSystem(
            new Stage(graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT), graphicsProvider.createSpriteBatch()),
            assetManager,
            graphicsProvider
        );

        initializeGameObjects();
        setupUI();
    }

    private void initializeGameObjects() {
        this.cell = new Cell(
            new GamePlayScreen(inputProvider, graphicsProvider, game, assetManager, configProvider, 2),
            assetManager,
            configProvider);

        this.glucoseList = new ArrayList<>();
        for (int i = 0; i < NUM_GLUCOSE; i++) {
            float x = random.nextFloat() * viewport.getWorldWidth(); // Random x position
            float y = random.nextFloat() * viewport.getWorldHeight(); // Random y position
            glucoseList.add(new Glucose(assetManager, x, y));
        }

        // Set initial target position for the cell
        targetX = random.nextFloat() * viewport.getWorldWidth();
        targetY = random.nextFloat() * viewport.getWorldHeight();
    }

    private void setupUI() {
        menuSystem.initialize("", new String[]{}, INSTRUCTION_TEXT);
    }

    @Override
    public void show() {
        // Initialize any resources needed for the attract screen
        isSimulationRunning = true;
        animationTime = 0f;
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
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        menuSystem.getStage().getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        isSimulationRunning = false; // Stop the simulation when the screen is hidden
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        cell.dispose();
        particles.dispose();
        menuSystem.clear();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Return to the main menu if any key is pressed or the screen is touched
        if (inputProvider.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            game.setScreen(previousScreen);
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
        if (isSimulationRunning) {
            // Update the simulation
            animationTime += deltaTimeSeconds;

            particles.update(deltaTimeSeconds, viewport.getWorldWidth(), viewport.getWorldHeight());

            updateCellMovement(deltaTimeSeconds);
        }
        menuSystem.getStage().act(deltaTimeSeconds);
    }

    private void updateCellMovement(float deltaTimeSeconds) {
        // Humanistic movement logic
        float cellX = cell.getX();
        float cellY = cell.getY();

        // Calculate direction to target
        float dx = targetX - cellX;
        float dy = targetY - cellY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Normalize direction
        if (distance > 0) {
            dx /= distance;
            dy /= distance;
        }

        // Move the cell towards the target
        cellX += dx * cellSpeed * deltaTimeSeconds;
        cellY += dy * cellSpeed * deltaTimeSeconds;


        // Update cell position
        cell.moveTo(cellX, cellY);

        // Check if the cell has reached the target
        if (distance < 10) { // Close enough to the target
            // Set a new random target
            targetX = random.nextFloat() * viewport.getWorldWidth();
            targetY = random.nextFloat() * viewport.getWorldHeight();

            // Randomize speed occasionally
            if (random.nextFloat() < 0.1) { // 10% chance to change speed
                cellSpeed = 50 + random.nextFloat() * 150; // Random speed between 50 and 200
            }
        }
    }

    @Override
    public void draw() {
        // Clear the screen with a gradient background
        ScreenUtils.clear(Main.PURPLE); // Darker purple background

        viewport.apply(true);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Draw game objects
        cell.draw(spriteBatch, shapeRenderer);
        spriteBatch.begin();
        for (Glucose glucose : glucoseList) {
            glucose.draw(spriteBatch);
        }

        particles.draw(spriteBatch);
        spriteBatch.end();
        menuSystem.getStage().draw();
    }

    public float getAnimationTime() {
        return animationTime;
    }

    public boolean isSimulationRunning() {
        return isSimulationRunning;
    }

    public Cell getCell() {
        return cell;
    }

    public List<Glucose> getGlucoseList() {
        return glucoseList;
    }

    public float getTargetX() {
        return targetX;
    }

    public float getTargetY() {
        return targetY;
    }

}
