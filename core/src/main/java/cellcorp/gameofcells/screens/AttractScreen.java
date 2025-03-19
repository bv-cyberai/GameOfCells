package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

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
    private Glucose glucose;

    // Add variables to simulate gameplay or animation
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
        this.glucose = new Glucose(assetManager, 400, 300, 50); // Example position and radisus
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
        glucose.dispose();
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

            // Simulate gameplay logic here
            float moveX = (float) Math.sin(animationTime) * 100;
            float moveY = (float) Math.cos(animationTime) * 100;
            
            // Example: Update positions of cells, glucose, or other entities
            cell.move(deltaTimeSeconds, moveX < 0, moveX > 0, moveY > 0, moveY < 0);
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
        Texture animationTexture = assetManager.get(AssetFileNames.START_BACKGROUND, Texture.class);
        float x = (float) Math.sin(animationTime) * 100 + viewport.getWorldWidth() / 2 - animationTexture.getWidth() / 2;
        float y = viewport.getWorldHeight() / 2 - animationTexture.getHeight() / 2;
        spriteBatch.draw(animationTexture, x, y);

        spriteBatch.end();
    }
}