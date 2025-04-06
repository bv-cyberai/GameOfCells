package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.providers.ConfigProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import cellcorp.gameofcells.objects.Cell;

public class PopupInfoScreen implements GameOfCellsScreen {
    // Types - Add to this as new pop-ups are desired.
    public enum Type { glucose, danger }

    /**
     * Width of the HUD view rectangle.
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_WIDTH = 400;
    /**
     * Height of the HUD view rectangle.
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_HEIGHT = 300;
    private static final float POPUP_WIDTH = 300f;
    private static final float POPUP_HEIGHT = 200f;
    private static final float PADDING = 10f;
    private static final float FONT_SCALE = 0.25f;

    private final Type type;
    private final Main game;
    private final GameOfCellsScreen previousScreen;
    private final InputProvider inputProvider;
    private final AssetManager assetManager;
    private final GraphicsProvider graphicsProvider;
    private final ConfigProvider configProvider;

    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;
    private ShapeRenderer shape;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private String message;
    private float messageX, messageY;
    private GamePlayScreen gamePlayScreen;

    public PopupInfoScreen(
            InputProvider inputProvider, 
            AssetManager assetManager, 
            GraphicsProvider graphicsProvider,
            Main game, 
            GameOfCellsScreen previousScreen,
            ConfigProvider configProvider, 
            Type type) {

        this.type = type;
        this.game = game;
        this.previousScreen = previousScreen;
        this.inputProvider = inputProvider;
        this.assetManager = assetManager;
        this.graphicsProvider = graphicsProvider;
        this.configProvider = configProvider;

        if (previousScreen instanceof GamePlayScreen) {
            this.gamePlayScreen = (GamePlayScreen) previousScreen;
        }

        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        this.spriteBatch = graphicsProvider.createSpriteBatch();

        setMessageOrDefault();
    }

    private void setMessageOrDefault() {
        try {
            message = type == Type.glucose
                    ? configProvider.getStringValue("glucosePopupMessage")
                    : configProvider.getStringValue("dangerPopupMessage");
        } catch (NullPointerException e) {
            message = type == Type.glucose
                    ? "You've collected glucose!\n\nCells convert glucose into ATP for energy.\n\nPress 'C' to continue!"
                    : "You're in danger!\n\nGradient color 'X' damages cell health.\n\nPress 'C' to continue!";
        }
    }

    @Override
    public void show() {
        if (font == null) {
            font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
            font.getData().setScale(FONT_SCALE); // Set the scale of the font
        }
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        draw();
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        if (inputProvider.isKeyPressed(Input.Keys.C)
            || inputProvider.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(previousScreen);
        }
    }

    @Override
    public void draw() {
        ScreenUtils.clear(0, 0, 0, 0);

        viewport.apply(true);
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        float popupX = VIEW_RECT_WIDTH / 2 - POPUP_WIDTH / 2;
        float popupY = VIEW_RECT_HEIGHT / 2 - POPUP_HEIGHT / 2;

        if (gamePlayScreen != null) {
            Cell cell = gamePlayScreen.getCell();
            popupX = cell.getX() - POPUP_WIDTH / 2;
            popupY = cell.getY() - cell.getHeight() + 20;
        }

        if (shape == null) shape = new ShapeRenderer();
        if (font == null) font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);

        layout.setText(font, message, Color.WHITE, POPUP_WIDTH - 2*PADDING, Align.center, true);
        messageX = popupX + POPUP_WIDTH / 2 - layout.width / 2;
        messageY = popupY + POPUP_HEIGHT / 2 - layout.height / 2;

        shape.setProjectionMatrix(viewport.getCamera().combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(new Color(0.424f, 0.553f, 0.573f, 0.9f));
        shape.rect(popupX, popupY, POPUP_WIDTH, POPUP_HEIGHT);
        shape.end();

        spriteBatch.begin();
        font.draw(spriteBatch, message, messageX, messageY);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        if (shape != null) shape.dispose();
        if (font != null) font.dispose();
        spriteBatch.dispose();
    }

    @Override
    public void pause() {
    
    }

    @Override
    public void resume() {
    
    }

    @Override
    public void hide() {

    }

    @Override
    public void update(float deltaTimeSeconds) {

    }

    /**
     * Message Setter
     *
     * Will set the message of the popup based on the given type of the popup.
     */
//    private void setMessage() {
//        switch (type) {
//            case glucose:
//                message = "You've collected glucose!" + "\n" + "\n" + "Cells convert glucose into ATP for energy."
//                        + "\n" + "\n" + "Press 'C' to continue!";
//                break;
//            case danger:
//                message = "You're in danger!" + "\n\n" + "Gradient color 'X' damages cell health" + "\n\n"
//                        + "Press 'C' to continue!";
//                break;
//            default:
//                break;
//
//        }
//    }

}
