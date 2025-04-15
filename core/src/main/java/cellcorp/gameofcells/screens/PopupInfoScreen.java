package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import cellcorp.gameofcells.providers.ConfigProvider;

public class PopupInfoScreen implements GameOfCellsScreen {
    // Types - Add to this as new pop-ups are desired.
    public enum Type { glucose, danger, basic}

    // Mark set these to be the previous `WORLD_WIDTH` and `WORLD_HEIGHT`.
    // Change as is most convenient.
    /**
     * Width of the HUD view rectangle.
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_WIDTH = 1280;
    /**
     * Height of the HUD view rectangle.
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_HEIGHT = 800;

    private final Stage stage;
    private final AssetManager assetManager;
    private final GraphicsProvider graphicsProvider;
    private final ConfigProvider configProvider;
    private final InputProvider inputProvider;
    private final Runnable onClose;

    private boolean visible = false;
    private boolean hasShownGlucosePopup = false; // If the glucose popup has been shown
    private boolean hasShownAcidZonePopup = false; // If the acid zone popup has been shown
    private boolean hasShownBasicZonePopup = false; // If the basic zone popup has been shown

    public PopupInfoScreen(
            GraphicsProvider graphicsProvider,
            AssetManager assetManager,
            ConfigProvider configProvider,
            InputProvider inputProvider,
            Viewport viewport,
            Runnable onClose) {
        this.assetManager = assetManager;
        this.graphicsProvider = graphicsProvider;
        this.configProvider = configProvider;
        this.inputProvider = inputProvider;
        this.onClose = onClose;

        this.stage = new Stage(graphicsProvider.createFitViewport(
            GamePlayScreen.VIEW_RECT_WIDTH, 
            GamePlayScreen.VIEW_RECT_HEIGHT), 
            graphicsProvider.createSpriteBatch());
    }

    public void show(Type type) {
        stage.clear(); // Remove any previous popups
        visible = true;

        String message = getMessageFromConfig(type);
        BitmapFont font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label messageLabel = graphicsProvider.createLabel(message, labelStyle);
        messageLabel.setColor(new Color(1f, 1f, 1f, 0.95f));
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);

        Table borderTable = new Table();
        borderTable.setBackground(new TextureRegionDrawable(graphicsProvider.createWhitePixelTexture()));
        borderTable.setColor(Color.LIGHT_GRAY);


        Table inner = new Table();
        inner.setColor(new Color(.1f, .2f, .25f, 1f));
        inner.setBackground(new TextureRegionDrawable(graphicsProvider.createWhitePixelTexture()));
        inner.add(messageLabel).width(460).pad(30).expandY().fillY();
        inner.pack();

        borderTable.add(inner).pad(3);

        Table outer = new Table();
        outer.setFillParent(true);
        outer.center();
        outer.add(borderTable).width(500).height(20);

        stage.addActor(outer);
    }

    public void render(float delta) {
        if (!visible) return;

        stage.act(delta);
        stage.draw();
        
        if(inputProvider.isKeyJustPressed(Input.Keys.ENTER)
            || inputProvider.isKeyJustPressed(Input.Keys.SPACE)) {
            visible = false;
            if (onClose != null) {
                onClose.run();
            }
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public boolean isVisible() {
        return visible;
    }

    private String getMessageFromConfig(Type type) {
        try {
            switch (type) {
                case glucose:
                    return configProvider.getStringValue("glucosePopupMessage");
                case danger:
                    return configProvider.getStringValue("dangerPopupMessage");
                case basic:
                    return configProvider.getStringValue("basicPopupMessage");
                default:
                    throw new IllegalArgumentException("Unknown type: " + type);
            }
        } catch (Exception e) {
            switch (type) {
                case glucose:
                    return "You've collected glucose!\n\nCells convert glucose into ATP for energy.";
                case danger:
                    return "Danger! You are in a dangerous area.\n\nGradient color pink damages cell health.";
                case basic:
                    return "Nutrient-rich zone detected!\n\nThis area contains abundant glucose resources.";
                default:
                    return "Unknown type: " + type;
            }
        }
    }

    /**
     * Check if the glucose popup has been shown
     */
    public boolean hasShownGlucosePopup() {
        return hasShownGlucosePopup;
    }

    /**
     * Set the glucose popup as shown
     */
    public void setHasShownGlucosePopup(boolean hasShownGlucosePopup) {
        this.hasShownGlucosePopup = hasShownGlucosePopup;
    }

    /**
     * Check if the acid zone popup has been shown
     */
    public boolean hasShownAcidZonePopup() {
        return hasShownAcidZonePopup;
    }

    /**
     * Set the acid zone popup as shown
     */
    public void setHasShownAcidZonePopup(boolean hasShownAcidZonePopup) {
        this.hasShownAcidZonePopup = hasShownAcidZonePopup;
    }

    /**
     * Check if the basic zone popup has been shown
     */
    public boolean hasShownBasicZonePopup() {
        return hasShownBasicZonePopup;
    }

    /**
     * Set the basic zone popup as shown
     */
    public void setHasShownBasicZonePopup(boolean hasShownBasicZonePopup) {
        this.hasShownBasicZonePopup = hasShownBasicZonePopup;
    }

    @Override
    public void handleInput(float deltaTimeSeconds) {
        // No input handling needed for this screen
    }
    @Override
    public void update(float deltaTimeSeconds) {
        // No updates needed for this screen
    }
    @Override
    public void draw() {
        // No drawing needed for this screen
    }
    @Override
    public void dispose() {
        stage.dispose();
        assetManager.unload(AssetFileNames.HUD_FONT);
    }
    @Override
    public void show() {
        // No action needed when showing this screen
    }
    @Override
    public void hide() {
        // No action needed when hiding this screen
    }
    @Override
    public void pause() {
        // No action needed when pausing this screen
    }

    @Override
    public void resume() {
        // No action needed when resuming this screen
    }
}