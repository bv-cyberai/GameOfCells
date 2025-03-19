package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;

/**
 * PopupInfoScreen
 * 
 * Provides the ability to create a popup with informational text.
 *
 * @author Brendon Vinyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 03/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */

public class PopupInfoScreen implements GameOfCellsScreen {

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

    // Types - Add to this has new pop-ups are desired.
    public enum Type {
        glucose, danger
    };

    private final Type type;

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
    private final GlyphLayout layout;
    private float popUpSize;

    // Message and properties.
    private String message;
    private float messageY;
    private float messageX;
    private float padding;

    /**
     * Popup Screen Constructor
     * 
     * Creates a new popup with the given type.
     * 
     * @param inputProvider    - The input provider
     * @param assetManager     - The Asset manager
     * @param graphicsProvider - The graphics provider
     * @param game             - The game instance
     * @param camera           - The game camera
     * @param viewport         - The game viewport
     * @param previousScreen   - The previous screen
     * @param type             - The type of popup to create -- See type enum.
     */
    public PopupInfoScreen(InputProvider inputProvider, AssetManager assetManager, GraphicsProvider graphicsProvider,
            Main game, OrthographicCamera camera, FitViewport viewport, GameOfCellsScreen previousScreen, Type type) {

        this.type = type;

        // Game
        this.game = game;
        this.previousScreen = previousScreen;
        // Providers
        this.inputProvider = inputProvider;
        this.assetManager = assetManager;
        this.graphicsProvider = graphicsProvider;

        this.camera = camera;
        this.viewport = viewport;
        this.spriteBatch = graphicsProvider.createSpriteBatch();

        // Font/Message
        layout = new GlyphLayout();

        message = "";
        messageX = 0;
        messageY = 0;

        padding = -10; // negative padding shifts y down, and x left. Should not need to be changed.
        popUpSize = 500f;

        // Load Font
        if (assetManager != null) {
            assetManager.load("rubik.fnt", BitmapFont.class);
            assetManager.load("rubik1.png", Texture.class);
            assetManager.load("rubik2.png", Texture.class);
            assetManager.finishLoading();
        }
        setMessage();
    }

    @Override
    public void show() {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'show'");
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        draw();
    }

    /**
     * Message Setter
     * 
     * Will set the message of the popup based on the given type of the popup.
     */
    private void setMessage() {
        switch (type) {
            case glucose:
                message = "You've collected glucose!" + "\n" + "\n" + "Cells convert glucose into ATP for energy."
                        + "\n" + "\n" + "Press 'C' to continue!";
                break;
            case danger:
                message = "You're in danger!" + "\n\n" + "Gradient color 'X' damages cell health" + "\n\n"
                        + "Press 'C' to continue!";
                break;
            default:
                break;

        }
    }

    /**
     * Resize
     * 
     * @param width  - the new width
     * @param height - the new height
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // Update the viewport
        camera.viewportWidth = width; // Update the camera viewport width
        camera.viewportHeight = height; // Update the camera viewport height
    }

    /**
     * pause
     */
    @Override
    public void pause() {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    /**
     * resume
     */
    @Override
    public void resume() {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'resume'");
    }

    /**
     * hide
     */
    @Override
    public void hide() {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'hide'");
    }

    /**
     * Dispose
     */
    @Override
    public void dispose() {
        shape.dispose();
        font.dispose();
        spriteBatch.dispose();
    }

    /**
     * Handle Input
     * 
     * Exits the popup screen on escape
     */
    @Override
    public void handleInput(float deltaTimeSeconds) {
        if (inputProvider.isKeyPressed(Input.Keys.C)) {
            game.setScreen(previousScreen);
        }
    }

    /**
     * Update
     */
    @Override
    public void update(float deltaTimeSeconds) {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Draw
     */
    @Override
    public void draw() {
        ScreenUtils.clear(new Color(.157f, .115f, .181f, 1.0f)); // purple

        viewport.apply(true);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        if (shape == null) {
            shape = new ShapeRenderer();
        }

        if (font == null) {
            font = assetManager.get("rubik.fnt", BitmapFont.class);
            font.getData().setScale(0.25f); // Set the scale of the font

            CharSequence cs = message;
            layout.setText(font, cs, 0, cs.length(), Color.WHITE, (popUpSize + padding), Align.center, true, null);

            // Align the message to the top center of the popup.
            messageX = ((viewport.getWorldWidth() / 2)) - (popUpSize / 2) - (padding + 5);
            messageY = (viewport.getWorldHeight() / 2) + (popUpSize / 2) + padding;
        }
        shape.setProjectionMatrix(camera.combined);
        // Draw Square Popup
        shape.begin(ShapeRenderer.ShapeType.Filled);

        shape.setColor(new Color(.424f, .553f, .573f, 1.0f)); // blue

        shape.rect((viewport.getWorldWidth() / 2) - (popUpSize / 2),
                (viewport.getWorldHeight() / 2) - (popUpSize / 2),
                popUpSize,
                popUpSize);
        shape.end();

        // Draw message via layout.
        spriteBatch.begin();
        font.draw(spriteBatch, layout, messageX, messageY);
        spriteBatch.end();
    }

}
