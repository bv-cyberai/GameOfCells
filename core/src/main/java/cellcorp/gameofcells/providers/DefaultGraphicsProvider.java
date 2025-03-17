package cellcorp.gameofcells.providers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DefaultGraphicsProvider implements GraphicsProvider {
    @Override
    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    @Override
    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    @Override
    public ShapeRenderer createShapeRenderer() {
        return new ShapeRenderer();
    }

    @Override
    public SpriteBatch createSpriteBatch() {
        return new SpriteBatch();
    }
}
