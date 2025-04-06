package cellcorp.gameofcells.screens;


import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.objects.Cell;

public class PopupInfoScreen implements GameOfCellsScreen {
    // Types - Add to this as new pop-ups are desired.
    public enum Type { glucose, danger }

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
    private static final float POPUP_WIDTH = 300f;
    private static final float POPUP_HEIGHT = 200f;
    private static final float PADDING = 15f;
    private static final float FONT_SCALE = 0.2f;
    private static final float CELL_OFFSET = 20f;

    private final Type type;
    private final Main game;
    private final GameOfCellsScreen previousScreen;
    private final InputProvider inputProvider;
    private final AssetManager assetManager;
    private final GraphicsProvider graphicsProvider;
    private final ConfigProvider configProvider;
    private final GamePlayScreen gamePlayScreen;

    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;
    private ShapeRenderer shape;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private String message;
    
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
        this.gamePlayScreen = (previousScreen instanceof GamePlayScreen)
                ? (GamePlayScreen) previousScreen
                : null;

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
        if (previousScreen != null) {
            previousScreen.render(delta);
        }

        draw();
        handleInput(delta);
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
        if (gamePlayScreen == null) return;

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        Cell playerCell = gamePlayScreen.getCell();
        float popupX = playerCell.getX() + CELL_OFFSET;
        float popupY = playerCell.getY() + CELL_OFFSET;

        Vector2 screenPos = viewport.project(new Vector2(popupX, popupY));

        popupX = Math.max(0, Math.min(screenPos.x, VIEW_RECT_WIDTH - POPUP_WIDTH));
        popupY = Math.max(0, Math.min(screenPos.y, VIEW_RECT_HEIGHT - POPUP_HEIGHT));

        if (shape == null) shape = graphicsProvider.createShapeRenderer();
        if (font == null) {
            font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
            font.getData().setScale(FONT_SCALE); // Set the scale of the font
        }

        layout.setText(font, message, Color.WHITE, POPUP_WIDTH - 2*PADDING, Align.center, true);
        float textX = popupX + (POPUP_WIDTH - layout.width) / 2;
        float textY = popupY + (POPUP_HEIGHT + layout.height) / 2;

        shape.setProjectionMatrix(viewport.getCamera().combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(new Color(0.424f, 0.553f, 0.573f, 0.7f));
        shape.rect(popupX, popupY, POPUP_WIDTH, POPUP_HEIGHT);
        shape.end();

        spriteBatch.begin();
        font.draw(spriteBatch, layout, textX, textY);
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
