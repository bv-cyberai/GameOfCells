package cellcorp.gameofcells.objects;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * GlucoseManager
 *
 * Used to manage all glucose in the game
 *
 * @author Brendon Vinyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 *
 * @date 03/05/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class GlucoseManager {
    // Consider making these a constant.
    // WORLD_WIDTH = 1200
    // WORLD_HEIGHT = 800

    private final AssetManager assetManager;
    private final int MAX_GLUCOSE;
    // The radius of the glucose circle, and also the height/width of a glucose
    // texture.
    private final float RADIUS;

    private ArrayList<Glucose> glucoseArray;
    private Random rand;

    public GlucoseManager(AssetManager assetManager) {
        this.assetManager = assetManager;
        MAX_GLUCOSE = 10; // likely the wrong max
        // may also need to track current glucose.
        RADIUS = 30; // likely the wrong size
        rand = new Random();
        glucoseArray = new ArrayList<>();
        fillGlucoseArray();
    }

    /**
     * Random Coordinates
     *
     * Creates random coordinates for use in glucose creation.
     *
     * @return float[xCoordinate,yCoordinate]
     */
    private float[] getRandomCoordinate() {
        // Duplicate coordinates may occur ~ 1 in 1,000,000 generations
        // Overlap still occurs, These might both just be best handled
        // pushing them apart via collision detection
        // Another solution is to track coordinates of the cells in
        // a separate array.

        float[] coordinateArray = new float[2];
        int radiusInt = (int) RADIUS;

        // subtract by radius int to avoid off screen
        // hardcoded values may not work well when camera moves.
        int genX = rand.nextInt(1200 - radiusInt);
        int genY = rand.nextInt(800 - radiusInt);

        coordinateArray[0] = genX;
        coordinateArray[1] = genY;

        return coordinateArray;
    }

    /**
     * Glucose Populator
     *
     * Populates the glucoseArray with the games current glucose objects.
     *
     * @post glucose array is populated with glucose objects up to MAX_GLUCOSE
     */
    private void fillGlucoseArray() {
        for (int i = 0; i < MAX_GLUCOSE; i++) {
            float[] generatedCoordinates = getRandomCoordinate();
            glucoseArray.add(new Glucose(assetManager, generatedCoordinates[0], generatedCoordinates[1], RADIUS));
        }
    }

    /**
     * Draw
     *
     * Draws each glucose in the glucose array.
     *
     * @param batch The GamePlayScreen spritebatch
     *
     * @post glucose is drawn.
     */
    public void draw(SpriteBatch batch) {
        for (Glucose g : glucoseArray) {
            g.draw(batch);
        }
    }

    /**
     * Dispose
     *
     * Disposes of each glucose object in the glucose array.
     *
     * @post Glucose assets are disposed.
     */
    public void dispose() {
        for (Glucose g : glucoseArray) {
            g.dispose();
        }
    }

    /**
     * Glucose getter
     *
     * @return MAX_GLUCOSE
     */
    public int getMAX_GLUCOSE() {
        return MAX_GLUCOSE;
    }

    /**
     * RADIUS getter
     *
     * @return radius constant
     */
    public float getRADIUS() {
        return RADIUS;
    }

    /**
     * Array Getter
     *
     * @return the Glucose array
     */
    public ArrayList<Glucose> getGlucoseArray() {
        return glucoseArray;
    }

}
