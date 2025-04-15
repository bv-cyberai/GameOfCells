package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.providers.GraphicsProvider;
import cellcorp.gameofcells.providers.InputProvider;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PopupInfoScreen implements GameOfCellsScreen {
    /**
     * Width of the HUD view rectangle.
     * (the rectangular region of the world which the camera will display)
     */
    public static final int VIEW_RECT_WIDTH = 1280;

    // Mark set these to be the previous `WORLD_WIDTH` and `WORLD_HEIGHT`.
    // Change as is most convenient.
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
    private boolean hasShownHealAvailablePopup = false; // If the heal available popup has been shown

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

        this.stage = new Stage(
                graphicsProvider.createFitViewport(
                        GamePlayScreen.VIEW_RECT_WIDTH,
                        GamePlayScreen.VIEW_RECT_HEIGHT
                ),
                graphicsProvider.createSpriteBatch()
        );
    }

    public void show(Type type) {
        stage.clear(); // Remove any previous popups
        visible = true;

        BitmapFont font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        font.getData().setScale(0.25f); // Match HUD scale

        String message = getMessageFromConfig(type);
        GlyphLayout layout = new GlyphLayout(font, message);

        float padding = 40f;
        float popupWidth = layout.width + 2 * padding;
        float popupHeight = layout.height + 2 * padding;
        float x = (stage.getViewport().getWorldWidth() - layout.width) / 2;
        float y = (stage.getViewport().getWorldHeight() - layout.height) / 2;

        Color backgroundColor;
        switch (type) {
            case glucose:
                backgroundColor = Color.ORANGE;
                break;
            case danger:
                backgroundColor = Color.PINK;
                break;
            case basic:
                backgroundColor = Color.BLUE;
                break;
            default:
                backgroundColor = Color.WHITE;
                break;
        }

        Texture backgroundRegion = graphicsProvider.createRoundedRectangleTexture(
                (int) popupWidth, (int) popupHeight, backgroundColor, 20f);

        // Clear actors and draw directly using SpriteBatch just like HUD
        stage.addActor(new Actor() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.draw(backgroundRegion, x, y, popupWidth, popupHeight);
                font.setColor(Color.WHITE);
                font.draw(batch, message, x + padding, y + popupHeight - padding);
            }
        });
    }

    public void render(float delta) {
        if (!visible) return;

        stage.act(delta);
        stage.draw();

        if (inputProvider.isKeyJustPressed(Input.Keys.ENTER)
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
                case heal:
                    return configProvider.getStringValue("healAvailableMessage");
                default:
                    throw new IllegalArgumentException("Unknown type: " + type);
            }
        } catch (Exception e) {
            switch (type) {
                case glucose:
                    return "You've collected glucose!\n\nCells convert glucose into ATP for energy.";
                case danger:
                    return "DANGER ZONE!\n\nHealth drains in pink areas. Move to safety quickly!";
                case basic:
                    return "GLUCOSE ZONE!\n\nBlue areas contains lots of glucose.";
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

    /**
     * Check if the heal avaible popup has been shown
     */
    public boolean hasShownHealAvailablePopup() {
        return hasShownHealAvailablePopup;
    }

    public void setHasShownHealAvailablePopup(boolean hasShownHealAvailablePopup) {
        this.hasShownHealAvailablePopup = hasShownHealAvailablePopup;
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

    // Types - Add to this as new pop-ups are desired.
    public enum Type {glucose, danger, basic, heal}
}
