package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.objects.Glucose;
import cellcorp.gameofcells.providers.InputProvider;
import cellcorp.gameofcells.providers.GraphicsProvider;

public class AttractScreen implements GameOfCellsScreen {

    private final InputProvider inputProvider;
    private final GraphicsProvider graphicsProvider;
    private final Main game;
    private final AssetManager assetManager;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;

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

    // Background particles
    private List<Particle> backgroundParticles;
    private static final int NUM_PARTICLES = 100; // Number of background particles

    // White pixel texture for drawing particles
    private Texture whitePixelTexture;
    

    public AttractScreen(
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
        this.random = new Random();

        // Initialize white pixel texture
        this.whitePixelTexture = new Texture(AssetFileNames.WHITE_PIXEL); 

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

        // Initialize background particles
        this.backgroundParticles = new ArrayList<>();
        for (int i = 0; i < NUM_PARTICLES; i++) {
            backgroundParticles.add(new Particle(
                    random.nextFloat() * viewport.getWorldWidth(), 
                    random.nextFloat() * viewport.getWorldHeight(), 
                    random.nextFloat() * 2 + 1, // Random size between 1 and 3
                    random.nextFloat() * 0.5f + 0.1f, // Random speed between 0.1 and 0.6
                    whitePixelTexture // Pass the white pixel texture to the particle
                ));
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
        whitePixelTexture.dispose();
        // Dispose of any resources used by the attract screen
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        // Return to the main menu if any key is pressed or the screen is touched
        if (inputProvider.isKeyPressed(Input.Keys.ANY_KEY)) {
            game.setScreen(new MainMenuScreen(inputProvider, graphicsProvider, game, assetManager, camera, viewport));
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
        if (isSimulationRunning) {
            // Update the simulation
            animationTime += deltaTimeSeconds;

            for (Particle particle : backgroundParticles) {
                particle.update(deltaTimeSeconds, viewport.getWorldWidth(), viewport.getWorldHeight());
            }

            // Humanistic movement logic
            float cellX = cell.getCellPositionX();
            float cellY = cell.getCellPositionY();

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
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1); // Dark blue background

        viewport.apply(true);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Draw background particles
        spriteBatch.begin();
        for (Particle particle : backgroundParticles) {
            particle.draw(spriteBatch);
        }
        spriteBatch.end();
        
        // Draw game objects
        spriteBatch.begin();
        cell.draw(spriteBatch);
        for (Glucose glucose : glucoseList) {
            glucose.draw(spriteBatch);
        }
        spriteBatch.end();
    }

    // Inner class for background particles
    private static class Particle {
        private float x, y; // Position
        private float size; // Size of the particle
        private float speed; // Speed of the particle
        private Texture texture; // Texture for the particle

        public Particle(float x, float y, float size, float speed, Texture texture) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.texture = texture;
        }

        public void update(float deltaTimeSeconds, float worldWidth, float worldHeight) {
            y -= speed; // Move the particle downward
            if (y < 0) { // Reset the particle if it goes off the screen
                y = worldHeight;
                x = (float) Math.random() * worldWidth;
            }
        }

        public void draw(SpriteBatch spriteBatch) {
            spriteBatch.setColor(0.5f, 0.5f, 0.8f, 0.5f); // Light blue with transparency
            spriteBatch.draw(texture, x, y, size, size); // Draw the particle
        }
    }
}