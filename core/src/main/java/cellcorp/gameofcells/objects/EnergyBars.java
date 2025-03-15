package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Energy Bar Class
 *
 * Provides a way to update and display health and ATP
 * bars.
 * 
 * NOTE: This is a separate from HUD due to the need for
 * shaperender to be called outside of a sprite batch.
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

    private ShapeRenderer shape;

    // private SpriteBatch batch;
    private BitmapFont barFont;

    private int cellHealth;
    private int cellATP;
    private final int maxHealth;
    private final int maxATP;
    private final int barSize;

    private float healthPercentage;
    private float ATPPercentage;



    public EnergyBars(AssetManager assetManager, int maxHealth, int maxATP) {
        this.assetManager = assetManager;
        cellHealth = 0; // will be set by update
        cellATP = 100; // will be set by update
        this.maxHealth = maxHealth;
        this.maxATP = maxATP;
        barSize = 400;

        if (assetManager != null) {
            assetManager.load("rubik.fnt", BitmapFont.class);
            assetManager.load("rubik1.png", Texture.class);
            assetManager.load("rubik2.png", Texture.class);
            assetManager.finishLoading();
        }

    }

    public void draw() {
        if (shape == null) {
            shape = new ShapeRenderer();
        }
        // Draw Health Bar
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.RED);
        shape.rect(400, 770, barSize, 25);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.RED);
        shape.rect(400, 770, healthPercentage, 25);
        shape.end();
        // Draw ATP Bar
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.YELLOW);
        shape.rect(400, 740, barSize, 25);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.YELLOW);
        shape.rect(400, 740, ATPPercentage, 25);
        shape.end();

        // drawText();
    }

    public void drawText(SpriteBatch batch){
        if (barFont == null) {
            barFont = assetManager.get("rubik.fnt", BitmapFont.class);
            barFont.getData().setScale(5f); // set the scale of the barFont.
        }
        // batch.begin();
        barFont.draw(batch, "HEALTH", 500, 770);
        barFont.draw(batch, "ATP", 500, 740);
        // batch.end();
    }

    /**
     * Update
     * 
     * Updates current cell health and ATP values.
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
     * Cell Health Getter
     * 
     * @return Cell Health
     */
    public int getCellHealth() {
        return cellHealth;
    }

    /**
     * ATP Getter
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

    /**
     * Dispose
     */
    public void dispose() {
        shape.dispose();
        barFont.dispose();
        // batch.dispose();
    }

}
