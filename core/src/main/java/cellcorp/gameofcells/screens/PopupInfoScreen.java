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
    //Types
    public enum Type {glucose, danger, other,longTest};
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

    // Message and properties.
    private String message;
    private float messageY;
    private float messageX;
    private float padding;
    private final float popUpSize;



    public PopupInfoScreen(InputProvider inputProvider, AssetManager assetManager, GraphicsProvider graphicsProvider,
            Main game, OrthographicCamera camera, FitViewport viewport, GameOfCellsScreen previousScreen, Type type) {

        this.type = type;
        
        // Game
        this.game = game;
        this.previousScreen = previousScreen;
        //Providers
        this.inputProvider = inputProvider;
        this.assetManager = assetManager;
        this.graphicsProvider = graphicsProvider;
        
        this.camera = camera;
        this.viewport = viewport;
        this.spriteBatch = graphicsProvider.createSpriteBatch();

        //Font/Message
        layout = new GlyphLayout();

        message = "";
        messageX = 0;
        messageY = 0;

        padding = -10; // negative padding shifts y down, and x left. 
        popUpSize = 500; // Pop up is currently an n by n square.

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
        ScreenUtils.clear(Color.NAVY);

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
            layout.setText(font, cs,0,cs.length(), Color.WHITE, (popUpSize + padding), Align.center, true,null);

            messageX = ((viewport.getWorldWidth() / 2))- (popUpSize/2) - (padding +5);
            messageY = (viewport.getWorldHeight() /2) + (popUpSize/2) + padding ;
        }

        //Draw Square Popup
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.SLATE);
        shape.rect((viewport.getWorldWidth()/2)-(popUpSize/2),(viewport.getWorldHeight() /2) - (popUpSize/2) ,popUpSize,popUpSize);
        shape.end();

        //Draw message via layout.
        spriteBatch.begin();
        font.draw(spriteBatch, layout, messageX, messageY);
        spriteBatch.end();

        handleInput(delta);
    }

    /**
     * Message Setter
     * 
     * Will set the message of the popup based on the given type of the popup. 
     */
    private void setMessage() {
        switch (type) {
            case glucose:
                message = "You've found glucose!";
                break;
            case danger:
                message = "You're in danger, cell is being damaged";
                break;
            case other:
                message = "other";
                break;
            case longTest:
                message = "This an essay, it will be a really long message, it will be a really long message, how will it scale. Does it stay within the bounds of the box or does it look awful. Only Time will Tell.This an essay, it will be a really long message, it will be a really long message, how will it scale. Does it stay within the bounds of the box or does it look awful. Only Time will Tell.";
                break;
            default:
                break;
            
        }
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
        if (inputProvider.isKeyPressed(Input.Keys.ESCAPE)) {
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
