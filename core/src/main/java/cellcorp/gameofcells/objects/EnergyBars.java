package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Energy Bar Class
 * <p>
 * Provides a way to update and display health and ATP
 * bars.
 * <p>
 * NOTE: This is a separate from HUD due to the need for
 * shaperenderer to be called outside of a sprite batch.
 *
 * @author Brendon Vinyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 03/15/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class EnergyBars {

    private static final Color DARK_RED = new Color(0.7f, 0, 0, 1);

    // Since HUD uses its own viewport with an unchanging position,
    // we can use constant world coordinates to describe our draw locations.

    private AssetManager assetManager;

    private int cellHealth;
    private int cellATP;
    private final int maxHealth;
    private final int maxATP;
    public static final int BAR_SIZE = 400;

    private static final float HEALTH_BAR_X = ((float) HUD.VIEW_RECT_WIDTH / 2) - ((float) BAR_SIZE / 2);
    private static final float HEALTH_BAR_Y = (float) HUD.VIEW_RECT_HEIGHT - 30;
    private static final float ATP_BAR_X = HEALTH_BAR_X;
    private static final float ATP_BAR_Y = (float) HUD.VIEW_RECT_HEIGHT - 60;

    private float healthPercentage;
    private float ATPPercentage;

    public EnergyBars(AssetManager assetManager, int maxHealth, int maxATP) {
        this.assetManager = assetManager;
        this.maxHealth = maxHealth;
        this.maxATP = maxATP;

        cellHealth = 0; // will be set by update
        cellATP = 100; // will be set by update

        if (assetManager != null) {
            assetManager.load("rubik.fnt", BitmapFont.class);
            assetManager.load("rubik1.png", Texture.class);
            assetManager.load("rubik2.png", Texture.class);
            assetManager.finishLoading();
        }

    }

    /**
     * Draw
     * <p>
     * Draws the health bars to the screen.
     * Note: This does not include the text, this handled by the HUD
     * Class.
     */
    public void draw(ShapeRenderer shape) {
        // Draw Health Bar
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(DARK_RED);
        shape.rect(HEALTH_BAR_X, HEALTH_BAR_Y, BAR_SIZE, 25);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(DARK_RED);
        shape.rect(HEALTH_BAR_X, HEALTH_BAR_Y, healthPercentage, 25);
        shape.end();
        // Draw ATP Bar
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.YELLOW);
        shape.rect(ATP_BAR_X, ATP_BAR_Y, BAR_SIZE, 25);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.YELLOW);
        shape.rect(ATP_BAR_X, ATP_BAR_Y, ATPPercentage, 25);
        shape.end();
    }

    /**
     * Update
     * <p>
     * Updates current cell health and ATP values for displaying via
     * the Energy Bars.
     *
     * @param updatedCellHealth - The new cell health.
     * @param updatedCellATP    - The new ATP amount.
     */
    public void update(int updatedCellHealth, int updatedCellATP) {
        cellHealth = updatedCellHealth;
        cellATP = updatedCellATP;
        healthPercentage = ((float) cellHealth / maxHealth) * BAR_SIZE;
        ATPPercentage = ((float) cellATP / maxATP) * BAR_SIZE;
    }

    /**
     * Cell Health Getter (TEST METHOD)
     *
     * @return Cell Health
     */
    public float getCellHealth() {
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
     * AssetManager Getter (TEST METHOD)
     *
     * @return AssetManager
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }
}
