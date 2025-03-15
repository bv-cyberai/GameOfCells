package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import cellcorp.gameofcells.Main;

/**
 * Energy Bar Class
 *
 * Provides a way to update and display health and ATP
 * bars.
 * 
 * NOTE: This is a separate from HUD due to the need for
 * shaperenderer to be called outside of a sprite batch.
 * 
 * @author Brendon Vinyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 *
 * @date 03/15/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class EnergyBars {

    private AssetManager assetManager;
    private FitViewport viewport;

    private ShapeRenderer shape;
    private BitmapFont barFont;

    private int cellHealth;
    private int cellATP;
    private final int maxHealth;
    private final int maxATP;
    private final int barSize;

    private float healthBarX;
    private float healthBarY;
    private float ATPBarX;
    private float ATPBarY;

    private float healthPercentage;
    private float ATPPercentage;

    public EnergyBars(AssetManager assetManager, int maxHealth, int maxATP) {
        this.assetManager = assetManager;
        this.maxHealth = maxHealth;
        this.maxATP = maxATP;

        cellHealth = 0; // will be set by update
        cellATP = 100; // will be set by update
        barSize = 400;
        
        healthBarX = (Main.viewport.getWorldWidth() / 2) - (barSize /2);
        healthBarY = Main.viewport.getWorldHeight() - 30;

        ATPBarX = (Main.viewport.getWorldWidth() / 2) - (barSize /2);
        ATPBarY = Main.viewport.getWorldHeight() - 60;

        System.out.println("CONSTRUCTOR_EB:" + " HX: " + healthBarX + " HY: " + healthBarY
        + " AX: " + ATPBarX + " AY: " + ATPBarY);

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
     * Draws the health bars to the screen.
     * Note: This does not include the text, this handled by the HUD
     * Class.
     */
    public void draw() {
        if (shape == null) {
            shape = new ShapeRenderer();
        }
        // Draw Health Bar
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.RED);
        shape.rect(healthBarX, healthBarY, barSize, 25);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.RED);
        shape.rect(healthBarX, healthBarY, healthPercentage, 25);
        shape.end();
        // Draw ATP Bar
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.YELLOW);
        shape.rect(ATPBarX, ATPBarY, barSize, 25);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.YELLOW);
        shape.rect(ATPBarX, ATPBarY, ATPPercentage, 25);
        shape.end();
    }

    /**
     * Update
     * 
     * Updates current cell health and ATP values for displaying via
     * the Energy Bars.
     * 
     * @param updatedCellHealth - The new cell health.
     * @param updatedCellATP    - The new ATP amount.
     */
    public void update(int updatedCellHealth, int updatedCellATP) {
        cellHealth = updatedCellHealth;
        cellATP = updatedCellATP;
        healthPercentage = ((float) cellHealth / maxHealth) * barSize;
        ATPPercentage = ((float) cellATP / maxATP) * barSize;
    }

    /**
     * Cell Health Getter (TEST METHOD)
     * 
     * @return Cell Health
     */
    public int getCellHealth() {
        return cellHealth;
    }

    /**
     * ATP Getter (TEST METHOD)
     * 
     * @return ATP amount
     */
    public int getCellATP() {
        return cellATP;
    }

    /**
     * HealthPercentage Getter (TEST METHOD)
     * 
     * @return health percentage as a pixel value
     */
    public float getHealthPercentage() {
        return healthPercentage;
    }

    /**
     * ATP Percentage Getter (TEST METHOD)
     * 
     * @return ATP percentage as a pixel value.
     */
    public float getATPPercentage() {
        return ATPPercentage;
    }

    /**
     * Bar Size Getter(TEST METHOD)
     * 
     * @return The barsize.
     */
    public int getBarSize() {
        return barSize;
    }

    public void resize() {
        healthBarX = (Main.viewport.getWorldWidth() / 2) - (barSize /2);
        healthBarY = Main.viewport.getWorldHeight() - 30;

        ATPBarX = (Main.viewport.getWorldWidth() / 2) - (barSize /2);
        ATPBarY = Main.viewport.getWorldHeight() - 60;

        System.out.println("RESIZE_EB:" + " HX: " + healthBarX + " HY: " + healthBarY
        + " AX: " + ATPBarX + " AY: " + ATPBarY);
    }

    /**
     * Dispose
     */
    public void dispose() {
        shape.dispose();
        barFont.dispose();
    }

}
