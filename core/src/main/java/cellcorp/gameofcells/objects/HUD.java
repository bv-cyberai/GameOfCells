package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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

    private AssetManager assetManager;
    private ShapeRenderer shape;
    private HUDBar healthBar;

    private BitmapFont font;
    private Float timer;
    private int displayTime;

    private int cellHealth;
    private int cellATP;
    private int maxHealth;
    private int maxATP;

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
    public HUD(AssetManager assetManager, ShapeRenderer shape) {
        this.assetManager = assetManager;
        healthBar = new HUDBar();

        timer = 0f;
        displayTime = 0;

        // int values that should be updated in update.
        // cellHealth = 100;
        // cellATP = 0;


        cellHealth = 0; // should be set by update
        cellATP = 100; // should be set by update 
        maxHealth = 100;
        maxATP = 99; // This hurts should just be 100, but alpha is 99.

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
        font = assetManager.get("rubik.fnt", BitmapFont.class);
        font.getData().setScale(0.25f); // Set the scale of the font

        font.draw(batch, cellHealthString, 10, 790);
        font.draw(batch, atpString, 10, 770);
        font.draw(batch, timerString, 10, 750);

        // healthBar.draw(batch);

        if (shape == null) { 
            shape = new ShapeRenderer();
        }
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.RED);
        shape.rect(400, 770, 400, 25);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.RED);
        shape.rect(400, 770, 400/2, 25);
        shape.end();

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
        // grab health - waiting cell class function.
        cellHealthString = "HEALTH: " + cellHealth + "/100";
        // grab ATP - waiting cell class function
        atpString = "ATP: " + cellATP;

        // healthBar.update(cellHealth, cellATP);
        this.cellHealth = cellHealth;
        this.cellATP = cellATP;
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
