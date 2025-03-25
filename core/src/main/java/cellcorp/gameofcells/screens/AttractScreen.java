package cellcorp.gameofcells.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Glucose;
import cellcorp.gameofcells.objects.Particles;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

public class AttractScreen implements GameOfCellsScreen {

    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private final Main game;
    private final AssetManager assetManager;
    private final Camera camera;
    private final Viewport viewport;
    private final SpriteBatch spriteBatch;

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

    // Game objects
    private Cell cell;
    private List<Glucose> glucoseList;
    
    // Simulation state
    private float animationTime = 0f; // Track time for simulation
    private boolean isSimulationRunning = true;

    // Humanistic movement variables
    private float targetX, targetY; // Target position for the cell
    private float cellSpeed = 100f; // Speed of the cell
    private Random random; // For randomizing movement

    // Number of glucose objects to generate
    private static final int NUM_GLUCOSE = 10; // Adjust this number as needed

    private Particles particles;

    public AttractScreen(
            InputProvider inputProvider,
            GraphicsProvider graphicsProvider,
            Main game,
            AssetManager assetManager
    ) {
        this.inputProvider = inputProvider;
        this.graphicsProvider = graphicsProvider;
        this.game = game;
        this.assetManager = assetManager;

        this.camera = graphicsProvider.createCamera();
        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT, camera);

        this.spriteBatch = graphicsProvider.createSpriteBatch();
        this.random = new Random();

        Texture whitePixelTexture = new Texture(AssetFileNames.WHITE_PIXEL);
        this.particles = new Particles(whitePixelTexture);

        // Initialize game objects
        this.cell = new Cell(assetManager);

        // Initialize glucose objects randomly
        this.glucoseList = new ArrayList<>();
        for (int i = 0; i < NUM_GLUCOSE; i++) {
            float x = random.nextFloat() * viewport.getWorldWidth(); // Random x position
            float y = random.nextFloat() * viewport.getWorldHeight(); // Random y position
            float radius = 20; // Smaller radius for glucose
            glucoseList.add(new Glucose(assetManager, x, y, radius));
        }

        // Set initial target position for the cell
        targetX = random.nextFloat() * viewport.getWorldWidth();
        targetY = random.nextFloat() * viewport.getWorldHeight();
    }

    @Override
    public void show() {
        // Initialize any resources needed for the attrack screen
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
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        isSimulationRunning = false; // Stop the simulation when the screen is hidden
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        cell.dispose();
        for (Glucose glucose : glucoseList) {
            glucose.dispose();
        }
        particles.dispose();
        // Dispose of any resources used by the attract screen
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Return to the main menu if any key is pressed or the screen is touched
        if (inputProvider.isKeyJustPressed(Input.Keys.ANY_KEY) && !inputProvider.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new MainMenuScreen(inputProvider, graphicsProvider, game, assetManager, camera, viewport));
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
        if (isSimulationRunning) {
            // Update the simulation
            animationTime += deltaTimeSeconds;

            particles.update(deltaTimeSeconds, viewport.getWorldWidth(), viewport.getWorldHeight());

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
    }

    @Override
    public void draw() {
        // Clear the screen with a gradient background
        ScreenUtils.clear(.157f, .115f, .181f, 1f); // Dark purple background

        viewport.apply(true);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        particles.draw(spriteBatch);
        
        // Draw game objects
        spriteBatch.begin();
        cell.draw(spriteBatch);
        for (Glucose glucose : glucoseList) {
            glucose.draw(spriteBatch);
        }
        spriteBatch.end();
    }

}