package cellcorp.gameofcells.providers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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
    public OrthographicCamera createCamera() {
        return new OrthographicCamera();
    }

    @Override
    public FitViewport createFitViewport(float viewRectWidth, float viewRectHeight) {
        return new FitViewport(viewRectWidth, viewRectHeight);
    }

    @Override
    public FitViewport createFitViewport(float viewRectWidth, float viewRectHeight, Camera camera) {
        return new FitViewport(viewRectWidth, viewRectHeight, camera);
    }

    @Override
    public Viewport createViewport(float viewRectWidth, float viewRectHeight) {
        return new FitViewport(viewRectWidth, viewRectHeight);
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
