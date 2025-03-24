package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.GraphicsProvider;

/**
 * Hud Class
 *
 * Provides HUD functionality
 * 
 * @author Brendon Vinyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 *
 * @date 03/04/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class HUD {
    // Mark set these to be the previous `WORLD_WIDTH` and `WORLD_HEIGHT`.
    // Change as is most convenient.
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

    private final AssetManager assetManager;

    // HUD gets its own viewport (with its own internal camera)
    // It's position is never moved, so draw calls should always
    // take values in the range:
    // `(0, 0) .. (VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT)`
    // See Main for more information.
    private final Viewport viewport;
    // To avoid having to reset the projection matrices after each draw call,
    // let's give HUD its own sprite batch and shape renderer.
    // Draw calls will be less efficient (I think?), but it shouldn't matter much.
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    private final EnergyBars energyBars;

    // fonts
    private BitmapFont font;
    private BitmapFont barFont;
    private float hudFontScale;
    private float barFontScale;

    // glyph
    private GlyphLayout healthLayout;
    private GlyphLayout ATPLayout;

    // Energy Bar Values for storing and calculating position of text.
    private final float healthBarY;
    private final float healthBarHeight;
    private final float ATPBarY;
    private final float ATPBarHeight;
    private float healthTextWidth;
    private float healthTextHeight;
    private float ATPTextWidth;
    private float ATPTextHeight;
    private float healthTextY;
    private float ATPTextY;

    // Timer Values
    private Float timer;
    private int displayTime;

    // Cell Values
    private final int maxHealth;
    private final int maxATP; // placeholder will likely be needed.

    // HUD Strings
    private String timerString;
    private String atpString;
    private String cellHealthString;

    /**
     * HUD Class
     * 
     * Provides HUD Functionality for the game.
     * 
     * This font is built using size 100 font. Scaling down looks nice,
     * scaling up is ugly and should be avoided.
     * 
     * This font cant be changed if you have other preferences.
     */
    public HUD(GraphicsProvider graphicsProvider, AssetManager assetManager, int maxHealth, int maxATP) {

        this.assetManager = assetManager;
        this.maxHealth = maxHealth;
        this.maxATP = maxATP;

        this.viewport = graphicsProvider.createFitViewport(VIEW_RECT_WIDTH, VIEW_RECT_HEIGHT);
        this.batch = graphicsProvider.createSpriteBatch();
        this.shapeRenderer = graphicsProvider.createShapeRenderer();

        this.energyBars = new EnergyBars(assetManager, maxHealth, maxATP);

        timer = 0f;
        displayTime = 0;

        healthLayout = new GlyphLayout();
        ATPLayout = new GlyphLayout();
        hudFontScale = 0.25f;
        barFontScale = 0.2f;

        // For now these are hardcoded/pulled form the EnergyBar Class.
        // Don't see a huge need to not do that.
        healthBarY = 770;
        healthBarHeight = 25;
        ATPBarY = 740;
        ATPBarHeight = 25;
    }

    /**
     * Draw
     * Draw the hud. Re-applies caller's viewport after drawing.
     */
    public void draw(Viewport callerViewport) {
        viewport.apply(true);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        drawHudText(batch);
        drawEnergyBars(shapeRenderer);
        drawBarText(batch);

        callerViewport.apply();
    }

    private void drawHudText(SpriteBatch batch) {
        batch.begin();
        if (font == null) {
            font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
            font.getData().setScale(hudFontScale); // Set the scale of the font
        }
        if (font.getScaleX() != hudFontScale || font.getScaleY() != hudFontScale) {
            // Fonts are shared, and scale needs to be reset on Screen changes.
            font.getData().setScale(hudFontScale);
        }
        font.draw(batch, cellHealthString, 10, 790);
        font.draw(batch, atpString, 10, 770);
        font.draw(batch, timerString, 10, 750);
        batch.end();
    }

    private void drawEnergyBars(ShapeRenderer shapeRenderer) {
        energyBars.draw(shapeRenderer);
    }

    /**
     * Draw EnergyBarText
     * 
     * This draws "Health" and "ATP" over the energy bars.
     * 
     * NOTE: This is separated due HUD utilizing Spritebatch, and
     * Energybars using ShapeRenderer. Additionally it felt correct to keep
     * text rendering within the HUD class.
     * 
     * @param batch - The Game SpriteBatch
     */
    public void drawBarText(SpriteBatch batch) {
        if (barFont == null) {
            // setup font
            barFont = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
            barFont.getData().setScale(barFontScale);

            // setup glyphs
            healthLayout.setText(barFont, "HEALTH");
            ATPLayout.setText(barFont, "ATP");

            // setup position
            healthTextWidth = healthLayout.width;
            healthTextHeight = healthLayout.height;

            ATPTextWidth = ATPLayout.width;
            ATPTextHeight = ATPLayout.height;

            healthTextY = healthBarY + (healthBarHeight / 2) + (healthTextHeight / 2);
            ATPTextY = ATPBarY + (ATPBarHeight / 2) + (ATPTextHeight / 2);
        }

        if (barFont.getScaleX() != barFontScale || font.getScaleY() != barFontScale) {
            // Fonts are shared, and scale needs to be reset on Screen changes.
            font.getData().setScale(barFontScale);
        }

        batch.begin();
        barFont.draw(batch, "HEALTH", (VIEW_RECT_WIDTH - healthTextWidth) / 2, healthTextY);
        barFont.draw(batch, "ATP", (VIEW_RECT_WIDTH - ATPTextWidth) / 2, ATPTextY);
        batch.end();
    }

    /**
     * Updater
     * 
     * Updates time, and associated cell attributes (Health/ATP) for the HUD.
     * 
     * @param delta - time since last render.
     */
    public void update(float delta, int cellHealth, int cellATP) {
        timer += delta;

        roundTime();
        timerString = "Timer: " + displayTime;
        cellHealthString = "HEALTH: " + cellHealth + "/" + maxHealth;
        atpString = "ATP: " + cellATP;

        energyBars.update(cellHealth, cellATP);
    }

    /**
     * Time Rounder
     * 
     * Rounds time to 1 second intervals
     */
    private void roundTime() {
        int timeRounded = (int) (timer * 10);
        displayTime = timeRounded / 10;
    }

    /**
     * Resize the HUD in response to a screen resize.
     */
    public void resize(int screenWidth, int screenHeight) {
        viewport.update(screenWidth, screenHeight);
    }

    /**
     * Dispose
     */
    public void dispose() {
        font.dispose();
    }

    /**
     * Timer String getter
     * 
     * @return The timing string
     */
    public String getTimerString() {
        return timerString;
    }

    /**
     * ATP String getter
     * 
     * @return the ATP String
     */
    public String getAtpString() {
        return atpString;
    }

    /**
     * Health String Getter
     * 
     * @return - the Health String
     */
    public String getCellHealthString() {
        return cellHealthString;
    }

}
