package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

/**
 * Glucose Object
 *
 * Represents a glucose molecule which a cell converts
 * to ATP.
 * 
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 *
 * @date 03/04/2025
 * @course CIS 405
 * @assignment GameOfCells
 */

public class Glucose {
    public static final int RADIUS = 15;
    public static final int ATP_PER_GLUCOSE = 2;
    public static final int ATP_PER_GLUCOSE_WITH_MITOCHONDRIA = 10;

    private final AssetManager assetManager;
    private final Circle boundCircle;

    /**
     * Constructor
     *
     * @param assetManager The Game assetManager
     * @param x            - The x position
     * @param y            - The y position
     */
    public Glucose(AssetManager assetManager, float x, float y) {
        this.assetManager = assetManager;
        boundCircle = new Circle(x, y, RADIUS);
    }

    /**
     * Draw
     * 
     * Draws a cell.
     * 
     * @param batch The gamePlayScreen Spritebatch
     */
    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // other options for glucose exist within assests.
        var glucoseTexture = assetManager.get("glucose_orange.png", Texture.class);

        var bottomLeftX = boundCircle.x - boundCircle.radius;
        var bottomLeftY = boundCircle.y - boundCircle.radius;
        var diameter = boundCircle.radius * 2;

        batch.begin();
        batch.draw(glucoseTexture, bottomLeftX, bottomLeftY, diameter, diameter);
        batch.end();

        if (GamePlayScreen.DEBUG_DRAW_ENABLED) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.circle(boundCircle.x, boundCircle.y, boundCircle.radius);
            shapeRenderer.end();
        }
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
