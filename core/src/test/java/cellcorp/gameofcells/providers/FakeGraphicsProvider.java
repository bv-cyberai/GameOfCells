package cellcorp.gameofcells.providers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import cellcorp.gameofcells.Main;

public class FakeGraphicsProvider implements GraphicsProvider {
    @Override
    public int getWidth() {
        return Main.WORLD_WIDTH;
    }

    @Override
    public int getHeight() {
        return Main.WORLD_HEIGHT;
    }

    @Override
    public SpriteBatch createSpriteBatch() {
        // Non-graphics code should never reference the sprite batch,
        // so this might even be safe.
        return null;
    }

    @Override
    public ShapeRenderer createShapeRenderer() {
        return null;
    }
}
