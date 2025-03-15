package cellcorp.gameofcells.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
/**
 * Energy Bar Class
 *
 * Provides a way to update and display health and ATP
 * bars.
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
public class EnergyBars {

    private ShapeRenderer shape;
    private  int cellHealth;
    private  int cellATP;
    private  int maxHealth;
    private  int maxATP;
    private  int barSize;

    public EnergyBars(int maxHealth, int maxATP) {
        cellHealth = 0; // will be set by update
        cellATP = 100; // will be set by update 
        this.maxHealth = maxHealth;
        this.maxATP = maxHealth;
        barSize = 400;

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
            shape.rect(400, 770, (cellHealth/maxHealth)*barSize, 25);
            shape.end();
        // Draw ATP Bar
            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setColor(Color.YELLOW);
            shape.rect(400, 740, barSize, 25);
            shape.end();
    
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(Color.YELLOW);
            shape.rect(400, 740, (cellATP/maxATP)*barSize, 25);
            shape.end();
        
    }
    /**
     * Update
     * 
     * Updates current cell health and ATP values.
     * @param updatedCellHealth - The new cell health.
     * @param updatedCellATP - The new ATP amount.
     */
    public void update(int updatedCellHealth, int updatedCellATP) {
        cellHealth = updatedCellHealth;
        cellATP = updatedCellATP;
    }

}

