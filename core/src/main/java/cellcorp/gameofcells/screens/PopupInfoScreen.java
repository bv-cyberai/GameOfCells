package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

public class PopupInfoScreen implements GameOfCellsScreen {

    // game
    private final Main game;
    private GameOfCellsScreen previousScreen;

    // providers
    private final InputProvider inputProvider;
    private final AssetManager assetManager;
    private final GraphicsProvider graphicsProvider;

    // Batch/Camera
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;
    private ShapeRenderer shape;

    // Font and Font Properties
    private BitmapFont font;
    // used to center text.
    private GlyphLayout layout;
    private float textWidth;
    private float textHeight;
    private float xResetCenter = 0;
    private float yCenter = 0;

    public PopupInfoScreen(InputProvider inputProvider, AssetManager assetManager, GraphicsProvider graphicsProvider,
            Main game, OrthographicCamera camera, FitViewport viewport, GameOfCellsScreen previousScreen) {

        this.game = game;

        this.inputProvider = inputProvider;
        this.assetManager = assetManager;
        this.graphicsProvider = graphicsProvider;

        this.camera = camera;
        this.viewport = viewport;
        this.spriteBatch = graphicsProvider.createSpriteBatch();

        this.previousScreen = previousScreen;

        layout = new GlyphLayout();
    }

    @Override
    public void show() {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'show'");
    }

    @Override
    public void render(float delta) {
        // ScreenUtils.clear(Color.BLACK);

        viewport.apply(true);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        if (shape == null) {
            shape = new ShapeRenderer();
        }
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.GREEN);
        shape.rect(viewport.getWorldWidth()/2,viewport.getWorldHeight() /2,500,500);
        shape.end();

        handleInput(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // Update the viewport
        camera.viewportWidth = width; // Update the camera viewport width
        camera.viewportHeight = height; // Update the camera viewport height
    }

    @Override
    public void pause() {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public void resume() {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'resume'");
    }

    @Override
    public void hide() {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'hide'");
    }

    @Override
    public void dispose() {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'dispose'");
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        if (inputProvider.isKeyPressed(Input.Keys.I)) {
            game.setScreen(previousScreen);
        }
    }

    @Override
    public void update(float deltaTimeSeconds) {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void draw() {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'draw'");
    }

}
