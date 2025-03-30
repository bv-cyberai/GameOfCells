package cellcorp.gameofcells.providers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cellcorp.gameofcells.Main;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Texture;
import org.mockito.Mockito;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Matrix4;

public class FakeGraphicsProvider implements GraphicsProvider {
    @Override
    public int getWidth() {
        return Main.DEFAULT_SCREEN_WIDTH;
    }

    @Override
    public int getHeight() {
        return Main.DEFAULT_SCREEN_HEIGHT;
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
    public Viewport createViewport(float viewRectWidth, float viewRectHeight) {
        return Mockito.mock(FitViewport.class);
    }

    @Override
    public ShapeRenderer createShapeRenderer() {
        return Mockito.mock(ShapeRenderer.class);
    }

    @Override
    public SpriteBatch createSpriteBatch() {
        // Non-graphics code should never reference the sprite batch,
        // so this might even be safe.
        return Mockito.mock(SpriteBatch.class);
    }

    @Override
    public Texture createTexture(int width, int height, Pixmap.Format format) {
        return Mockito.mock(Texture.class);
    }

    @Override
    public Texture createTexture() {
        return Mockito.mock(Texture.class);
    }

    @Override
    public Pixmap createPixmap(int width, int height, Pixmap.Format format) {
        return Mockito.mock(Pixmap.class);
    }

    @Override
    public BitmapFont createBitmapFont() {
        return new BitmapFont();
    }

    @Override
    public Matrix4 getScreenProjectionMatrix() {
        return Mockito.mock(Matrix4.class);
    }
}
