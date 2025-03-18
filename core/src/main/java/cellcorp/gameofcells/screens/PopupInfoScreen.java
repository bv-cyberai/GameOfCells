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

    public enum Type {glucose, danger, other,longTest};

    private String message;
    private float messageWidth;
    private float messageHeight;
    private float messageY;
    private float messageX;

    private Type type;

    public PopupInfoScreen(InputProvider inputProvider, AssetManager assetManager, GraphicsProvider graphicsProvider,
            Main game, OrthographicCamera camera, FitViewport viewport, GameOfCellsScreen previousScreen, Type type) {

        this.game = game;

        this.inputProvider = inputProvider;
        this.assetManager = assetManager;
        this.graphicsProvider = graphicsProvider;

        this.camera = camera;
        this.viewport = viewport;
        this.spriteBatch = graphicsProvider.createSpriteBatch();

        this.previousScreen = previousScreen;
        this.type = type;

        layout = new GlyphLayout();

        message = "";
        messageHeight = 0;
        messageWidth = 0;
        messageX = 0;
        messageY = 0;

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
        // ScreenUtils.clear(Color.BLACK);

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
            layout.setText(font, cs,0,cs.length(), Color.WHITE, 490, Align.center, true,null);

            messageWidth = layout.width * font.getScaleX(); // actually scale the width
            messageHeight = layout.height * font.getScaleY(); //actually scale height
            System.out.println("MW: " + messageWidth + " MH: " + messageHeight + " LW: " + layout.width + " LH: " + layout.height);
            // messageX = ((viewport.getWorldWidth() - messageWidth)/2) -210;

            //box is 500 so subtract by 250
            messageX = ((viewport.getWorldWidth() / 2))- (layout.width / 2);
            messageY = (viewport.getWorldHeight() /2) + (layout.height) - 15;
            System.out.println("MY: " + messageY + " MX: " + messageX);
            System.out.println(layout.toString());
            System.out.println(viewport.getWorldWidth());


        }



        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.SLATE);
        shape.rect((viewport.getWorldWidth()/2)-250,(viewport.getWorldHeight() /2) - 250 ,500,500);
        shape.end();

        spriteBatch.begin();
        font.draw(spriteBatch, layout, messageX, messageY);
        spriteBatch.end();

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

                // message = "This an essay, it will be a really long message, it will be a really long message, how will it scale.";
                break;
            default:
                break;
            
        }
    }

}
