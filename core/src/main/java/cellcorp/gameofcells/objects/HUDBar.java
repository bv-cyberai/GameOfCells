package cellcorp.gameofcells.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class HUDBar {
    private ShapeRenderer shape;
    private int cellHealth;
    private int cellATP;
    private int maxHealth;
    private int maxATP;

    public HUDBar() {
        cellHealth = 0; // should be set by update
        cellATP = 100; // should be set by update 
        maxHealth = 100;
        maxATP = 99; // This hurts should just be 100, but alpha is 99.
        // shape = graphics

    }

    public void draw(SpriteBatch batch) {
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

    public void update(int cellHealth, int cellATP) {
        this.cellHealth = cellHealth;
        this.cellATP = cellATP;
    }

}

