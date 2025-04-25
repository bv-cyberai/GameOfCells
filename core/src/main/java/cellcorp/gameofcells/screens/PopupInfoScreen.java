package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class PopupInfoScreen {
    // I feel this is not a _great_ place for these, but Idk where else to put them.
    // Maybe in `assets/defaults.timl` or something.
    public static final String DEFAULT_GLUCOSE_POPUP_MESSAGE =
            "You've collected glucose!\n\nCells convert glucose into ATP for energy.";
    public static final String DEFAULT_ACID_ZONE_POPUP_MESSAGE =
            "DANGER ZONE!\n\nHealth drains in pink areas. Move to safety quickly!";
    public static final String DEFAULT_BASIC_ZONE_POPUP_MESSAGE =
            "GLUCOSE ZONE!\n\nBlue areas contains lots of glucose.";
    public static final String DEFAULT_HEAL_AVAILABLE_MESSAGE =
            "Congrats! You can heal damage now.\n\nPress the h button to heal damage.\n\nPress 'Space' to continue!";
    public static final String DEFAULT_CELL_MEMBRANE_MESSAGE =
            "Congrats! You have a cell membrane now.\n\nYou wil take less damage from acid zones.\n\nPress 'Space' to continue!";

    private static final float FONT_SCALE = 0.45f;
    private static final float HORIZONTAL_PADDING = (float) HUD.VIEW_RECT_WIDTH / 5;
    private static final float VERTICAL_PADDING = (float) HUD.VIEW_RECT_HEIGHT / 5;
    private static final float SCROLL_PANE_WIDTH = HUD.VIEW_RECT_WIDTH - 2 * HORIZONTAL_PADDING;
    private static final float SCROLL_PANE_CORNER_RADIUS = SCROLL_PANE_WIDTH / 16;
    private static final float TEXT_HORIZONTAL_PADDING = SCROLL_PANE_WIDTH / 20;
    private static final float SCROLL_PANE_HEIGHT = HUD.VIEW_RECT_HEIGHT - 2 * VERTICAL_PADDING;
    private static final float TEXT_VERTICAL_PADDING = SCROLL_PANE_HEIGHT / 15;

    private static final float SCROLL_DISTANCE_PER_SECOND = 1600;

    private final AssetManager assetManager;
    /**
     * Name of the key for this message's popup in the config file.
     */
    private final Color backgroundColor;
    private final Runnable onHide;
    private final ScrollPane scrollPane;
    private final Stage stage;
    private boolean isVisible = false;
    private boolean wasShown = false;

    public PopupInfoScreen(ConfigProvider configProvider,
                           GraphicsProvider graphicsProvider,
                           AssetManager assetManager,
                           String messageConfigKey,
                           String defaultMessage,
                           Color backgroundColor,
                           Runnable onHide) {
        this.assetManager = assetManager;
        var message = configProvider.getStringOrDefault(messageConfigKey, defaultMessage);
        this.backgroundColor = backgroundColor;
        this.onHide = onHide;

        this.stage = graphicsProvider.createStage(HUD.VIEW_RECT_WIDTH, HUD.VIEW_RECT_HEIGHT);
        this.scrollPane = scrollPane(background(graphicsProvider), label(graphicsProvider, message));
        stage.addActor(table(scrollPane));

        if (GamePlayScreen.DEBUG_DRAW_ENABLED) {
            stage.setDebugAll(true);
        }
    }

    private Label label(GraphicsProvider graphicsProvider, String message) {
        var font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);

        var style = new Label.LabelStyle(font, Color.WHITE);
        var label = graphicsProvider.createLabel(message, style);
        label.setWrap(true);

        return label;
    }

    private Drawable background(GraphicsProvider graphicsProvider) {
        Texture backgroundTexture = graphicsProvider.createRoundedRectangleTexture(
                (int) SCROLL_PANE_WIDTH,
                (int) SCROLL_PANE_HEIGHT,
                backgroundColor,
                SCROLL_PANE_CORNER_RADIUS
        );
        Drawable background = new TextureRegionDrawable(backgroundTexture);
        background.setLeftWidth(TEXT_HORIZONTAL_PADDING);
        background.setRightWidth(TEXT_HORIZONTAL_PADDING);
        background.setTopHeight(TEXT_VERTICAL_PADDING);
        background.setBottomHeight(TEXT_VERTICAL_PADDING);

        return background;
    }

    private ScrollPane scrollPane(Drawable background, Label label) {
        var style = new ScrollPane.ScrollPaneStyle(background, null, null, null, null);

        var labelTable = new Table();
        labelTable.row().expand();
        labelTable.add(label).fill();

        return new ScrollPane(labelTable, style);
    }

    private Table table(ScrollPane scrollPane) {
        var table = new Table();
        table.setFillParent(true);
        table.row().expand();
        table.add(scrollPane).width(SCROLL_PANE_WIDTH).height(SCROLL_PANE_HEIGHT).fill().expand();

        return table;
    }

    public void show() {
        this.isVisible = true;
        this.wasShown = true;
    }

    public void hide() {
        isVisible = false;
        if (onHide != null) {
            onHide.run();
        }
    }

    public void handleInput(InputProvider inputProvider, float deltaTimeSeconds) {
        if (!isVisible) return;

        if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)
                || inputProvider.isKeyJustPressed(Input.Keys.SPACE)) {
            hide();
        }

        if (inputProvider.isKeyPressed(Input.Keys.UP)) {
            scrollUp(deltaTimeSeconds);
        } else if (inputProvider.isKeyPressed(Input.Keys.DOWN)) {
            scrollDown(deltaTimeSeconds);
        }
    }

    public void draw() {
        if (!isVisible) return;

        BitmapFont font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        var callerScaleX = font.getScaleX();
        var callerScaleY = font.getScaleY();
        font.getData().setScale(FONT_SCALE);

        stage.draw();

        font.getData().setScale(callerScaleX, callerScaleY);
    }

    /**
     * Returns whether this info popup has ever been shown.
     */
    public boolean wasShown() {
        return wasShown;
    }

    private void scrollUp(float deltaTimeSeconds) {
        if (!scrollPane.isScrollY()) return;

        var scrollPercent = scrollPane.getScrollPercentY();

        var scrollPercentChange = (-1 * SCROLL_DISTANCE_PER_SECOND * deltaTimeSeconds) / scrollPane.getHeight();
        var newScrollPercent = Math.max(scrollPercent + scrollPercentChange, 0);
        scrollPane.setScrollPercentY(newScrollPercent);
        scrollPane.updateVisualScroll();
    }

    private void scrollDown(float deltaTimeSeconds) {
        if (!scrollPane.isScrollY()) return;

        var scrollPercent = scrollPane.getScrollPercentY();
        var scrollPercentChange = (SCROLL_DISTANCE_PER_SECOND * deltaTimeSeconds) / scrollPane.getHeight();
        var newScrollPercent = Math.min(scrollPercent + scrollPercentChange, 100);
        scrollPane.setScrollPercentY(newScrollPercent);
        scrollPane.updateVisualScroll();
    }

    /**
     * For test use only.
     */
    public boolean isVisible() {
        return isVisible;
    }
}
