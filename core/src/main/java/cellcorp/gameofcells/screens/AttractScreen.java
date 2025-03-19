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

        // Initialize game objects
        this.cell = new Cell(assetManager);

        // Initialize glucose objects
        this.glucoseList = new ArrayList<>();
        glucoseList.add(new Glucose(assetManager, 200, 300, 50)); // Glucose 1
        glucoseList.add(new Glucose(assetManager, 300, 600, 50)); // Glucose 2
        glucoseList.add(new Glucose(assetManager, 350, 450, 50)); // Glucose 3
        glucoseList.add(new Glucose(assetManager, 500, 500, 50)); // Glucose 4
        glucoseList.add(new Glucose(assetManager, 600, 200, 50)); // Glucose 5
        glucoseList.add(new Glucose(assetManager, 700, 400, 50)); // Glucose 6
        glucoseList.add(new Glucose(assetManager, 900, 350, 50)); // Glucose 7
        glucoseList.add(new Glucose(assetManager, 950, 550, 50)); // Glucose 8
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

            // Simulate cell movement in a circular pattern
            float centerX = viewport.getWorldWidth() / 2;
            float centerY = viewport.getWorldHeight() / 2;
            float radius = 200; // Radius of the circular path

            // Calculate the new position of the cell
            float newX = centerX + (float) Math.cos(animationTime) * radius;
            float newY = centerY + (float) Math.sin(animationTime) * radius;

            // Update the cell position
            cell.moveTo(newX, newY);
        }
    }

    @Override
    public void draw() {
        ScreenUtils.clear(Color.BLACK); // Clear the screen with a black background

        viewport.apply(true);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();

        // Draw your animation or gameplay preview here
        // Example: Draw a moving texture
        cell.draw(spriteBatch);
        for (Glucose glucose : glucoseList) {
            glucose.draw(spriteBatch);
        }

        spriteBatch.end();
    }
}