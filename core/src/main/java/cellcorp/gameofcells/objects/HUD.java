package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import cellcorp.gameofcells.Main;

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

    private final AssetManager assetManager;

    // fonts
    private BitmapFont font;
    private BitmapFont barFont;

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
    public HUD(AssetManager assetManager, int maxHealth, int maxATP) {

        this.assetManager = assetManager;
        this.maxHealth = maxHealth;
        this.maxATP = maxATP;

        timer = 0f;
        displayTime = 0;

        healthLayout = new GlyphLayout();
        ATPLayout = new GlyphLayout();

        // For now these are hardcoded/pulled form the EnergyBar Class.
        // Don't see a huge need to not do that.
        healthBarY = 770;
        healthBarHeight = 25;
        ATPBarY = 740;
        ATPBarHeight = 25;

        if (assetManager != null) {
            assetManager.load("rubik.fnt", BitmapFont.class);
            assetManager.load("rubik1.png", Texture.class);
            assetManager.load("rubik2.png", Texture.class);
            assetManager.finishLoading();
        }
    }

    /**
     * Draw
     * 
     * Draw the hud
     * 
     * @param batch - The game spritebatch.
     */
    public void draw(SpriteBatch batch) {
        if (font == null) {
            font = assetManager.get("rubik.fnt", BitmapFont.class);
            font.getData().setScale(0.25f); // Set the scale of the font
        }
        font.draw(batch, cellHealthString, 10, 790);
        font.draw(batch, atpString, 10, 770);
        font.draw(batch, timerString, 10, 750);
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
            barFont = assetManager.get("rubik.fnt", BitmapFont.class);
            barFont.getData().setScale(.20f);

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

        barFont.draw(batch, "HEALTH", (Main.SCREEN_WIDTH_WORLD - healthTextWidth) / 2, healthTextY);
        barFont.draw(batch, "ATP", (Main.SCREEN_HEIGHT_WORLD - ATPTextWidth) / 2, ATPTextY);
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
