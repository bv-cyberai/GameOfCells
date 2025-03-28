package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

/**
 * Glucose Object
 *
 * Represents a glucose molecule which a cell converts
 * to ATP.
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

public class Glucose {
    public static final int ATP_PER_GLUCOSE = 20;

    private final AssetManager assetManager;
    private Circle boundCircle;

    /**
     * Constructor
     * 
     * @param assetManager The Game assetManager
     * @param x            - The x position
     * @param y            - The y position
     * @param radius       - The radius of the circle hitbox.
     */
    public Glucose(AssetManager assetManager, float x, float y, float radius) {
        this.assetManager = assetManager;
        boundCircle = new Circle(x, y, radius);
    }

    /**
     * Draw
     * 
     * Draws a cell.
     * 
     * @param batch The gamePlayScreen Spritebatch
     */
    public void draw(SpriteBatch batch) {
        // other options for glucose exist within assests.
        var glucoseTexture = assetManager.get("glucose_orange.png", Texture.class);
        assert (glucoseTexture != null);
        batch.draw(glucoseTexture, boundCircle.x, boundCircle.y, boundCircle.radius, boundCircle.radius);
    }

    /**
     * Dispose
     * 
     * Unloads a glucose asset.
     */
    public void dispose() {
        assetManager.unload("glucose_orange.png");
    }

    /**
     * xGetter
     * 
     * @return x Coordinate of the hitbox.
     */
    public float getX() {
        return boundCircle.x;
    }

    /**
     * yGetter
     * 
     * @return y Coordinate of the hitbox.
     */
    public float getY() {
        return boundCircle.y;
    }

    public Circle getCircle() {
        return boundCircle;
    }

    /**
     * Radius Getter
     * 
     * @return The radius of the hitbox.
     */
    public float getRadius() {
        return boundCircle.radius;
    }

    // opensource assets
    // https://en.wikipedia.org/wiki/Glucose#/media/File:Beta-D-glucose-from-xtal-3D-balls.png
    // https://openclipart.org/detail/75313/glossy-balls
}
