package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.hud.Bars;
import cellcorp.gameofcells.hud.ControlInstructions;
import cellcorp.gameofcells.hud.HudStats;
import cellcorp.gameofcells.notification.NotificationManager;
import cellcorp.gameofcells.objects.Stats;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HUD implements Disposable {
    /**
     * Width of the HUD view rectangle.
     */
    public static final int VIEW_RECT_WIDTH = 1920;

    /**
     * Height of the HUD view rectangle.
     */
    public static final int VIEW_RECT_HEIGHT = 1080;

    public static final float FONT_SCALE = 0.35f;

    private static final float PADDING = 20f;
    // Padding once on each edge
    private static final float COLUMN_WIDTH = (float) VIEW_RECT_WIDTH / 3 - PADDING * 2;

    private final GraphicsProvider graphicsProvider;
    private final AssetManager assetManager;
    private final Stage stage;
    private final HudStats hudStats;
    private final Bars bars;
    private final ControlInstructions controlInstructions;
    private final NotificationManager notificationManager;
    private final GlyphLayout popupLayout = new GlyphLayout();
    private final SpriteBatch popupSpriteBatch;
    private final GamePlayScreen gamePlayScreen;
    // Popup info variables
    private final Viewport viewport;
    private final Texture arrowTexture;
    private boolean shouldDrawPopup = false;
    private String popupMessage;
    private float popupX, popupY, popupWidth, popupHeight;
    private Color popupColor;

    public HUD(GraphicsProvider graphicsProvider, InputProvider inputProvider, AssetManager assetManager, GamePlayScreen gamePlayScreen, Stats stats) {
        this.graphicsProvider = graphicsProvider;
        this.assetManager = assetManager;
        this.gamePlayScreen = gamePlayScreen;
        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        this.stage = new Stage(viewport, graphicsProvider.createSpriteBatch());

        var cell = gamePlayScreen.getCell();
        this.hudStats = new HudStats(graphicsProvider, assetManager, cell, stats);
        this.bars = new Bars(graphicsProvider, assetManager, cell);
        this.controlInstructions = new ControlInstructions(graphicsProvider, inputProvider, assetManager);
        this.notificationManager = new NotificationManager(gamePlayScreen);
        var table = table(hudStats.getTable(), bars.getTable(), controlInstructions.getTable(), notificationManager.getTable());
        stage.addActor(table);

        if (GamePlayScreen.DEBUG_DRAW_ENABLED) {
            stage.setDebugAll(true);
        }

        popupSpriteBatch = graphicsProvider.createSpriteBatch();
        arrowTexture = assetManager.get(AssetFileNames.ARROW_TO_BASIC_ZONE, Texture.class);
    }

    private Table table(Table statsTable, Table barsTable, Table iconsTable, Table notificationsTable) {
        var table = new Table();
        table.setFillParent(true);
        table.row().padTop(PADDING).expand();

        var leftCol = new Table();
        leftCol.row().expand();
        statsTable.top().left();
        leftCol.add(statsTable).fill();

        var middleCol = new Table();
        middleCol.row();
        barsTable.top();
        middleCol.add(barsTable).fill();
        middleCol.row().padTop(PADDING).expand();
        notificationsTable.top();
        middleCol.add(notificationsTable).fill();

        var rightCol = new Table();
        rightCol.row().expand();
        iconsTable.top().right();
        rightCol.add(iconsTable).fill();

        table.add(leftCol).fill().width(COLUMN_WIDTH);
        table.add(middleCol).fill().width(COLUMN_WIDTH);
        table.add(rightCol).fill().width(COLUMN_WIDTH);

        return table;
    }

    public void handleInput() {
        controlInstructions.handleInput();
    }

    public void update(float deltaTime) {
        hudStats.update();
        bars.update();
        notificationManager.update(deltaTime);
    }

    public void draw() {
        stage.draw();
        drawPopup(popupSpriteBatch);
        drawArrow(Gdx.graphics.getDeltaTime(), gamePlayScreen.getSpriteBatch());
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    /**
     * For test use only.
     */
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void queuePopup(String message, float x, float y, float width, float height, Color bgColor) {
        this.shouldDrawPopup = true;
        this.popupMessage = message;
        this.popupX = x;
        this.popupY = y;
        this.popupWidth = width;
        this.popupHeight = height;
        this.popupColor = bgColor;
    }

    public void drawPopup(SpriteBatch batch) {
        if (!shouldDrawPopup) return;
        batch.setProjectionMatrix(viewport.getCamera().combined);

        BitmapFont popupFont = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        popupFont.getData().setScale(FONT_SCALE);
        popupLayout.setText(popupFont, popupMessage);

        Texture backgroudRegion = graphicsProvider.createRoundedRectangleTexture(
                (int) popupWidth, (int) popupHeight, popupColor, 20f);

        batch.begin();
        batch.draw(backgroudRegion, popupX, popupY, popupWidth, popupHeight);

        Color oldColor = popupFont.getColor().cpy();
        popupFont.setColor(Color.BLACK);
        popupFont.draw(
                batch, popupLayout, popupX + 40,
                popupY + popupHeight - 40
        );
        popupFont.setColor(oldColor);
        batch.end();

        backgroudRegion.dispose(); // Dispose of the background texture
        shouldDrawPopup = false; // Reset the flag
    }

    /**
     * Draws an arrow pointing towards the nearest basic zone.
     *
     * @param batch The sprite batch to draw with.
     */
    public void drawArrow(float deltaTime, SpriteBatch batch) {
        if (gamePlayScreen.getCell().getCellATP() > 30 || gamePlayScreen.isInBasicZone(gamePlayScreen.getCell().getX(), gamePlayScreen.getCell().getY()))
            return;

        Vector2 target = gamePlayScreen.getNearestBasicZoneCenter();
        Vector2 cellPos = new Vector2(gamePlayScreen.getCell().getX(), gamePlayScreen.getCell().getY());
        Vector2 dir = new Vector2(target).sub(cellPos);

        float distance = dir.len();
        float alpha = MathUtils.clamp(distance / 500f, 0f, 1f); // fade as you approach
        if (distance < 5f) return; // if too close, don't show arrow

        float cellRadius = gamePlayScreen.getCell().getCellSize() / 2f;
        dir.nor().scl(cellRadius + 40f); // Distance in front of the cell

        Vector2 arrowPos = cellPos.cpy().add(dir);
        float angle = dir.angleDeg();

        // Size up the arrow
        float scale = MathUtils.clamp(gamePlayScreen.getCell().getCellSize() / 50f, 1.5f, 3f);
        float arrowWidth = 16f * scale;
        float arrowHeight = 16f * scale;

        batch.begin();
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(
                arrowTexture,
                arrowPos.x - arrowWidth / 2, arrowPos.y - arrowHeight / 2, // position
                arrowWidth / 2, arrowHeight / 2, // origin
                arrowWidth, arrowHeight, // size
                1f, 1f, // scale
                angle, // rotation
                0, 0,
                arrowTexture.getWidth(), arrowTexture.getHeight(),
                false, false
        );
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    /**
     * For test use only.
     */
    public ControlInstructions getControlInstructions() {
        return controlInstructions;
    }
}
