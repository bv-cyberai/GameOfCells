package cellcorp.gameofcells.providers;

import cellcorp.gameofcells.Main;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.mockito.Mockito;

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
    public Texture createWhitePixelTexture() {
        return Mockito.mock(Texture.class);
    }

    public Texture createRoundedRectangleTexture(int width, int height, Color color, float radius) {
        return Mockito.mock(Texture.class);
    }

    @Override
    public BitmapFont createWhiteFont() {
        return Mockito.mock(BitmapFont.class);
    }

    @Override
    public Pixmap createPixmap(int width, int height, Pixmap.Format format) {
        return Mockito.mock(Pixmap.class);
    }

    @Override
    public BitmapFont createBitmapFont() {
        return Mockito.mock(BitmapFont.class);
        // For some reason, this works for the bitmapfont cache being null error.
    }

    @Override
    public Matrix4 getScreenProjectionMatrix() {
        return Mockito.mock(Matrix4.class);
    }

    @Override
    public Label createLabel(CharSequence text, Label.LabelStyle labelStyle) {
        return Mockito.mock(Label.class);
    }

    @Override
    public Image createImage(Texture texture) {
        return Mockito.mock(Image.class);
    }

    @Override
    public TextureRegionDrawable createTextureRegionDrawable(Texture texture) {
        return Mockito.mock(TextureRegionDrawable.class);
    }

    @Override
    public GlyphLayout createGlyphLayout(BitmapFont font, String text) {
        return Mockito.mock(GlyphLayout.class);
    }
}
