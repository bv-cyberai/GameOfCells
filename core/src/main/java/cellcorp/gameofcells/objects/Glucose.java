package cellcorp.gameofcells.objects;

import java.util.Random;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

/**
* Glucose Object
*
* Represents a glucose which a cell converts
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
    private final AssetManager assetManager;
    private Circle boundCircle;
    private Texture glucoseTexture;
    private Random rand;

    // WORLD_WIDTH = 1200
    // WORLD_HEIGHT = 800

    //x and y currently not needed to be passed but gonna leave it. 
    public Glucose(AssetManager assetManager) {
        this.assetManager = assetManager;
        boundCircle = new Circle(0, 0, 30);
        glucoseTexture = new Texture("glucose2.png");
        rand = new Random();

        // do better
        generateRandomPosition();
    }

    public void draw(SpriteBatch batch) {
        batch.draw(glucoseTexture, boundCircle.x, boundCircle.y, boundCircle.radius, boundCircle.radius);
    }

    public void dispose() {
        glucoseTexture.dispose();
    }

    public void generateRandomPosition() {
        // probably should actually be tied to a constant.
        // check if bounds are correct
        int genX = rand.nextInt(1200);
        int genY = rand.nextInt(800);
        
        boundCircle.setX((float)genX);
        boundCircle.setY((float)genY);
    }

    //opensource assets
    //https://en.wikipedia.org/wiki/Glucose#/media/File:Beta-D-glucose-from-xtal-3D-balls.png
    //https://openclipart.org/detail/75313/glossy-balls
}
