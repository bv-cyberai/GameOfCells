package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
    // private ShapeRenderer shape;

    private BitmapFont font;
    private Float timer;
    private int displayTime;

    private final int maxHealth;
    private final int maxATP;

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
    public HUD(AssetManager assetManager,int maxHealth, int maxATP) {
        this.assetManager = assetManager;

        timer = 0f;
        displayTime = 0;
        this.maxHealth = maxHealth;
        this.maxATP = maxATP;

        if (assetManager != null) { 
            assetManager.load("rubik.fnt", BitmapFont.class);
            assetManager.load("rubik1.png", Texture.class);
            assetManager.load("rubik2.png", Texture.class);
            assetManager.finishLoading();

            // font = assetManager.get("rubik.fnt", BitmapFont.class);
            // font.getData().setScale(0.25f);
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

        // figure out how to load once.
        // assetManager.load("rubik.fnt", BitmapFont.class);
        // assetManager.load("rubik1.png", Texture.class); // Texture for font characters
        // assetManager.load("rubik2.png", Texture.class); // Texture for font characters
        // assert (font != null);
        // batch.begin();
        font = assetManager.get("rubik.fnt", BitmapFont.class);
        font.getData().setScale(0.25f); // Set the scale of the font

        font.draw(batch, cellHealthString, 10, 790);
        font.draw(batch, atpString, 10, 770);
        font.draw(batch, timerString, 10, 750);

    }

    /**
     * Updater
     * 
     * Updates time, and will need health and ATP to be passed into
     * to display these values as well.
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
