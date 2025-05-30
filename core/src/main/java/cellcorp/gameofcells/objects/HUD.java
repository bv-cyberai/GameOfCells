//package cellcorp.gameofcells.objects;
//
//import cellcorp.gameofcells.providers.GraphicsProvider;
//import cellcorp.gameofcells.screens.ShopScreen;
//import cellcorp.gameofcells.objects.NotificationManager;
//
//import org.w3c.dom.Text;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.assets.AssetManager;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.Pixmap;
//import com.badlogic.gdx.graphics.g2d.GlyphLayout;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//import com.badlogic.gdx.utils.viewport.Viewport;
//import com.badlogic.gdx.utils.Array;
//import cellcorp.gameofcells.objects.Cell;
//import cellcorp.gameofcells.AssetFileNames;
//import cellcorp.gameofcells.providers.GraphicsProvider;
//
/// **
// * Hud Class
// *
// * Provides HUD functionality
// *
// * @author Brendon Vineyard / vineyabn207
// * @author Andrew Sennoga-Kimuli / sennogat106
// * @author Mark Murphy / murphyml207
// * @author Tim Davey / daveytj206
// *
// * @date 03/04/2025
// * @course CIS 405
// * @assignment GameOfCells
// */
//public class HUD {
//    // Mark set these to be the previous `WORLD_WIDTH` and `WORLD_HEIGHT`.
//    // Change as is most convenient.
//    /**
//     * Width of the HUD view rectangle.
//     * (the rectangular region of the world which the camera will display)
//     */
//    public static final int VIEW_RECT_WIDTH = 1200;
//    /**
//     * Height of the HUD view rectangle.
//     * (the rectangular region of the world which the camera will display)
//     */
//    public static final int VIEW_RECT_HEIGHT = 800;
//
//    private final AssetManager assetManager;
//
//    // HUD gets its own viewport (with its own internal camera)
//    // It's position is never moved, so draw calls should always
//    // take values in the range:
//    // `(0, 0) .. (VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT)`
//    // See Main for more information.
//    private final Viewport viewport;
//    // To avoid having to reset the projection matrices after each draw call,
//    // let's give HUD its own sprite batch and shape renderer.
//    // Draw calls will be less efficient (I think?), but it shouldn't matter much.
//    private final SpriteBatch batch;
//
//    // ShapeRenderer shapeRenderer;
//    // ShapeRenderer is used to draw the energy bars.
//    // It is not used for the HUD text, which is drawn using a SpriteBatch.
//    // This is because the energy bars are drawn using a shape renderer,
//    // which requires a different projection matrix than the sprite batch.
//    private final ShapeRenderer shapeRenderer;
//
//    // Energy Bars
//    // This is a separate class that handles the drawing of the energy bars.
//    private final EnergyBars energyBars;
//
//    // Notification Manager
//    // This is a separate class that handles the drawing of notifications.
//    // It is not used in the HUD class, but it is included here for completeness.
//    private final NotificationManager notificationManager;
//
//    // Graphics Provider
//    private final GraphicsProvider graphicsProvider;
//
//    // fonts
//    private BitmapFont font;
//    private BitmapFont barFont;
//    private BitmapFont notificationFont;
//    private float hudFontScale = 0.25f;
//    private float barFontScale = 0.2f;
//
//    // glyph
//    private GlyphLayout healthLayout;
//    private GlyphLayout atpLayout;
//    private GlyphLayout notificationLayout;
//    private GlyphLayout popupLayout;
//
//    // Energy Bar Values for storing and calculating position of text.
//    private String timerString = "Timer: 0";
//    private String atpString;
//    private String cellHealthString;
//    private final int maxHealth;
//    private final int maxATP;
//    private float timer = 0;
//
//    // Text game control buttons
//    private String moveControl = "Move";
//    private String pauseButton = "Pause";
//    private String quitbutton = "Quit";
//    private String shopButton = "Upgrade";
//    private String healButton = "Heal";
//
//    // Popup info variables
//    private boolean shouldDrawPopup = false;
//    private String popupMessage;
//    private float popupX, popupY, popupWidth, popupHeight;
//    private Color popupColor;
//
//    // Positioning Constants
//    private static final float HEALTH_BAR_Y = 770f;
//    private static final float ATP_BAR_Y = 740f;
//    private static final float NOTIFICATION_DURATION = 3f;
//    private static final float NOTIFICATION_VERTICAL_SPACING = 30f;
//    private static final float NOTIFICATION_Y_POSITION = 700f;
//
//
//
//    /**
//     * HUD Class
//     *
//     * Provides HUD Functionality for the game.
//     *
//     * This font is built using size 100 font. Scaling down looks nice,
//     * scaling up is ugly and should be avoided.
//     *
//     * This font cant be changed if you have other preferences.
//     */
/// /    public HUD(GraphicsProvider graphicsProvider, AssetManager assetManager, int maxHealth, int maxATP) {
/// /
/// /        this.assetManager = assetManager;
/// /        this.maxHealth = maxHealth;
/// /        this.maxATP = maxATP;
/// /
/// /        this.graphicsProvider = graphicsProvider;
/// /        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
/// /        this.batch = graphicsProvider.createSpriteBatch();
/// /        this.shapeRenderer = graphicsProvider.createShapeRenderer();
/// /        this.energyBars = new EnergyBars(assetManager, maxHealth, maxATP);
/// /
/// /        this.healthLayout = new GlyphLayout();
/// /        this.atpLayout = new GlyphLayout();
/// /        this.notificationLayout = new GlyphLayout();
/// /        this.popupLayout = new GlyphLayout();
/// /
/// /        this.notificationFont = graphicsProvider.createBitmapFont();
/// /        this.notificationManager = new NotificationManager(notificationFont, NOTIFICATION_VERTICAL_SPACING, NOTIFICATION_Y_POSITION);
/// /
/// /        // Initialize strings
/// /        this.atpString = "ATP: " + maxATP + "/" + maxATP;
/// /        this.cellHealthString = "HEALTH: " + maxHealth + "/" + maxHealth;
/// /
/// /        loadHudElements();
/// /
/// /    }
/// /
/// /    /*
/// /     * loadHudElements
/// /     *
/// /     * Loads the icons and elements for the hud
/// /     */
/// /
/// /    private void loadHudElements() {
/// /        if (assetManager != null) {
/// /            assetManager.load("rubik.fnt", BitmapFont.class);
/// /            assetManager.load("rubik1.png", Texture.class);
/// /            assetManager.load("rubik2.png", Texture.class);
/// /            assetManager.load("space_enter_stroke.png", Texture.class);
/// /            assetManager.load("key_start_2_stroke_cropped.png", Texture.class);
/// /            assetManager.load("p_key_2px_cropped.png", Texture.class);
/// /            assetManager.load("q_key_2px_cropped.png", Texture.class);
/// /            assetManager.load("esc_key_2px_cropped.png", Texture.class);
/// /            assetManager.load("H_KEY_2px.png",  Texture.class);
/// /            assetManager.finishLoading();
/// /
/// /        }
/// /    }
/// /
/// /    public void queuePopup(String message, float x, float y, float width, float height, Color bgColor) {
/// /        this.shouldDrawPopup = true;
/// /        this.popupMessage = message;
/// /        this.popupX = x;
/// /        this.popupY = y;
/// /        this.popupWidth = width;
/// /        this.popupHeight = height;
/// /        this.popupColor = bgColor;
/// /    }
/// /
/// /    /**
/// /     * Draw
/// /     * Draw the hud. Re-applies caller's viewport after drawing.
/// /     */
/// /    public void draw(Viewport callerViewport) {
/// /        viewport.apply(true);
/// /        this.notificationFont.getData().setScale(hudFontScale);
/// /
/// /
/// /        // Set up rendering
/// /        batch.setProjectionMatrix(viewport.getCamera().combined);
/// /        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
/// /
/// /        // Draw components in proper order
/// /        drawEnergyBars(shapeRenderer);
/// /        drawHudText(batch);
/// /        drawBarText(batch);
/// /        drawNotifications(batch);
/// /        drawHudIcons(batch);
/// /        drawPopup(batch);
/// /
/// /        // Reset the projection matrix to the caller's viewport
/// /        // This is important to avoid breaking the caller's viewport.
/// /        // This is done by re-applying the caller's viewport.
/// /        // This is a bit of a hack, but it works.
/// /        callerViewport.apply();
/// /    }
//
//    private void drawHudText(SpriteBatch batch) {
//        batch.begin();
//        if (font == null) {
//            font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
//            font.getData().setScale(hudFontScale); // Set the scale of the font
//        }
//        font.getData().setScale(hudFontScale); // Ensure the font scale is correct
//
//        font.draw(batch, cellHealthString, 10, 790);
//        font.draw(batch, atpString, 10, 760);
//        font.draw(batch, timerString, 10, 730);
//        font.draw(batch, pauseButton, 850, 740);
//        font.draw(batch, moveControl, 850, 698);
//        font.draw(batch, quitbutton, 850, 658);
//        font.draw(batch, shopButton, 850, 630);
//        font.draw(batch, healButton, 850, 599);
//        //Was using to track time, but will likely be useful when tracking down our stutter bug.
/// /        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
//        batch.end();
//    }
//    /*
//     * Draws Hud Icons
//     *
//     * Draws icons for game controls on the hud
//     */
//    //Draws icons for game controls
//    private void drawHudIcons(SpriteBatch batch) {
//    var shopTexture = assetManager.get(AssetFileNames.SHOP_BUTTON, Texture.class);
//    var moveTexture = assetManager.get(AssetFileNames.MOVE_KEY, Texture.class);
//    var pauseTexture = assetManager.get(AssetFileNames.PAUSE_BUTTON, Texture.class);
//    var quitTexture = assetManager.get(AssetFileNames.QUIT_BUTTON, Texture.class);
//    var healTexture = assetManager.get(AssetFileNames.HEAL_ICON, Texture.class);
//    batch.begin();
//    batch.draw(shopTexture, 980, 605,30, 30);
//    batch.draw(moveTexture, 961, 664,70, 50);
//    batch.draw(pauseTexture, 982,715,30,30);
//    batch.draw(quitTexture, 980,633,30,30);
//    batch.draw(healTexture, 980,575,30,30);
//    batch.end();
//    }
//
//
//    private void drawEnergyBars(ShapeRenderer shapeRenderer) {
//        energyBars.draw(shapeRenderer);
//    }
//
//    /**
//     * Draw EnergyBarText
//     *
//     * This draws "Health" and "ATP" over the energy bars.
//     *
//     * NOTE: This is separated due HUD utilizing Spritebatch, and
//     * Energybars using ShapeRenderer. Additionally it felt correct to keep
//     * text rendering within the HUD class.
//     *
//     * @param batch - The Game SpriteBatch
//     */
//    public void drawBarText(SpriteBatch batch) {
//        if (barFont == null) {
//            // setup font
//            barFont = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
//            barFont.getData().setScale(barFontScale);
//
//            // Pre-calculate text dimensions
//            healthLayout.setText(barFont, "HEALTH");
//            atpLayout.setText(barFont, "ATP");
//        }
//        barFont.getData().setScale(barFontScale); // Ensure the font scale is correct
//
//        batch.begin();
//        barFont.draw(batch, "HEALTH", (VIEW_RECT_WIDTH - healthLayout.width) / 2,
//                    HEALTH_BAR_Y + 20);
//        barFont.draw(batch, "ATP", (VIEW_RECT_WIDTH - atpLayout.width) / 2, ATP_BAR_Y + 20);
//        batch.end();
//    }
//
//    private void drawNotifications(SpriteBatch batch) {
//        batch.begin();
//        Array<Notification> notifications = notificationManager.getNotifications();
//        for (int i = 0; i < notifications.size; i++) {
//            Notification notification = notifications.get(i);
//            notificationLayout.setText(notificationFont, notification.getMessage());
//            float x = (VIEW_RECT_WIDTH - notificationLayout.width) / 2;
//            float y = notificationManager.getNotificationY(i);
//
//            Color oldColor = notificationFont.getColor();
//
//            // Set the color and alpha for the notification
//            Color notificationColor = notification.getColor();
//
//            notificationFont.setColor(notificationColor.r,
//                notificationColor.g,
//                notificationColor.b,
//                notification.getAlpha());
//            notificationFont.draw(batch, notification.getMessage(), x, y);
//            notificationFont.setColor(oldColor);
//        }
//        batch.end();
//    }
//
//    public void drawPopup(SpriteBatch batch) {
//        if (!shouldDrawPopup) return;
//
//        BitmapFont popupFont = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
//        popupFont.getData().setScale(hudFontScale);
//        popupLayout.setText(popupFont, popupMessage);
//
//        Texture backgroudRegion = graphicsProvider.createRoundedRectangleTexture(
//            (int) popupWidth, (int) popupHeight, popupColor, 20f);
//
//        batch.begin();
//        batch.draw(backgroudRegion, popupX, popupY, popupWidth, popupHeight);
//
//        Color oldColor = popupFont.getColor().cpy();
//        popupFont.setColor(Color.BLACK);
//        popupFont.draw(batch, popupLayout, popupX + 40,
//                        popupY + popupHeight - 40);
//        popupFont.setColor(oldColor);
//        batch.end();
//
//        backgroudRegion.dispose(); // Dispose of the background texture
//        shouldDrawPopup = false; // Reset the flag
//    }
//
//    /**
//     * Updater
//     *
//     * Updates time, and associated cell attributes (Health/ATP) for the HUD.
//     *
//     * @param delta - time since last render.
//     */
//    public void update(float delta, int cellHealth, int cellATP) {
//        energyBars.update(cellHealth, cellATP);
//        notificationManager.update(delta);
//
//        // Update HUD Strings & timer
//        timer += delta;
//        timerString = "Timer: " + (int)timer;
//        cellHealthString = "HEALTH: " + cellHealth + "/" + maxHealth;
//        atpString = "ATP: " + cellATP + "/" + maxATP;
//    }
//
//    /**
//     * Resize the HUD in response to a screen resize.
//     */
//    public void resize(int screenWidth, int screenHeight) {
//        viewport.update(screenWidth, screenHeight);
//    }
//
//    /**
//     * Dispose
//     */
//    public void dispose() {
//        batch.dispose();
//        shapeRenderer.dispose();
//        if (font != null) font.dispose();
//        if (barFont != null) barFont.dispose();
//    }
//
//
//    /**
//     * Show Energy Below Twenty Warning
//     */
//    public void showEnergyBelowTwentyWarning() {
//        addNotification("WARNING: ATP low!", NOTIFICATION_DURATION, new Color(1, 0.5f, 0, 1));
//    }
//
//    /**
//     * Show Energy Equals Zero Warning
//     */
//    public void showEnergyEqualsZeroWarning() {
//        addNotification("WARNING: Out of energy! Losing health!", NOTIFICATION_DURATION, Color.YELLOW);
//    }
//
//    /**
//     * Show Acid Zone Warning
//     */
//    public void showAcidZoneWarning() {
//        // Check if warning already exists
//        boolean warningExists = false;
//        for (Notification notification : notificationManager.getNotifications()) {
//            if (notification.getMessage().equals("DANGER: Acid zone! You're taking damage!")) {
//                warningExists = true;
//                break;
//            }
//        }
//
//        if (!warningExists) {
//            // Add the notification with a very long duration (effectively infinite)
//            addNotification("DANGER: Acid zone! You're taking damage!", Float.MAX_VALUE, Color.GOLD);
//        }
//    }
//
//    public void clearAcidZoneWarning() {
//        // Find all acid zone warnings
//        for (Notification notification : notificationManager.getNotifications()) {
//            if (notification.getMessage().equals("DANGER: Acid zone! You're taking damage!")) {
//                notification.expire(); // Trigger manual fade out
//            }
//        }
//    }
//
//    /**
//     * Add Notification
//     * @param message
//     * @param duration
//     * @param color
//     */
//    private void addNotification(String message, float duration, Color color) {
//        notificationManager.addNotification(message, duration, color);
//    }
//
//    /**
//     * Timer String getter
//     *
//     * @return The timing string
//     */
//    public String getTimerString() {
//        return timerString;
//    }
//
//    /**
//     * ATP String getter
//     *
//     * @return the ATP String
//     */
//    public String getAtpString() {
//        return atpString;
//    }
//
//    /**
//     * Health String Getter
//     *
//     * @return - the Health String
//     */
//    public String getCellHealthString() {
//        return cellHealthString;
//    }
//
//    /**
//     * Get the Notification Manager
//     *
//     * @return - the Notification Manager
//     */
//    public NotificationManager getNotificationManager() {
//        return notificationManager;
//    }
//
//    /**
//     * Get the Viewport
//     *
//     * @return - the Viewport
//     */
//    public Viewport getViewport() {
//        return viewport;
//    }
//}
