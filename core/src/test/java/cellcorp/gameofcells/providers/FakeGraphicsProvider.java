package cellcorp.gameofcells.providers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import cellcorp.gameofcells.Main;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.mockito.Mockito;

public class FakeGraphicsProvider implements GraphicsProvider {
    @Override
    public int getWidth() {
        return Main.VIEW_RECT_WIDTH;
    }

    @Override
    public int getHeight() {
        return Main.VIEW_RECT_HEIGHT;
    }

    @Override
    public OrthographicCamera createCamera() {
        return Mockito.mock(OrthographicCamera.class);
    }

    @Override
    public FitViewport createFitViewport(float viewRectWidth, float viewRectHeight) {
        return Mockito.mock(FitViewport.class);
    }

    @Override
    public FitViewport createFitViewport(float viewRectWidth, float viewRectHeight, Camera camera) {
        return Mockito.mock(FitViewport.class);
    }

    @Override
    public ShapeRenderer createShapeRenderer() {
        // Non-graphics code should never reference the shape renderer
        return null;
    }

    @Override
    public SpriteBatch createSpriteBatch() {
        // Non-graphics code should never reference the sprite batch,
        // so this might even be safe.
        return null;
    }

}
