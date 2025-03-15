package cellcorp.gameofcells.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import cellcorp.gameofcells.objects.HUDBar.Type;

public class HUDBar {

    public enum Type {
        HEALTH, ATP
    }

    private ShapeRenderer shape;
    private static int cellHealth;
    private static int cellATP;
    private static int maxHealth;
    private static int maxATP;
    private Type type;

    public HUDBar(HUDBar.Type type) {
        cellHealth = 0; // should be set by update
        cellATP = 100; // should be set by update 
        maxHealth = 100;
        maxATP = 99; // This hurts should just be 100, but alpha is 99.
        // shape = graphics
        this.type = type;

    }

    public void draw() {
        if (shape == null) { 
            // System.out.println("NULLSHAPERENDER");
            shape = new ShapeRenderer();
        }
        if(type == Type.HEALTH) {
            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setColor(Color.RED);
            shape.rect(400, 770, 400, 25);
            shape.end();

            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(Color.RED);
            shape.rect(400, 770, 400/2, 25);
            shape.end();
        } else {
            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setColor(Color.YELLOW);
            shape.rect(400, 740, 400, 25);
            shape.end();
    
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(Color.YELLOW);
            shape.rect(400, 740, 400/2, 25);
            shape.end();
        }
        
    }

    public static void update(int updatedCellHealth, int updatedCellATP) {
        cellHealth = updatedCellHealth;
        cellATP = updatedCellATP;
    }

}

