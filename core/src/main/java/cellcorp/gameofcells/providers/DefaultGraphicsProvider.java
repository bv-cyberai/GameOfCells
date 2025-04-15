package cellcorp.gameofcells.providers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;

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

    @Override
    public Texture createTexture(int width, int height, Pixmap.Format format) {
        return new Texture(width, height, format);
    }

    @Override
    public Texture createWhitePixelTexture() {
        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        Texture whiteTexture = new Texture(whitePixmap);
        whitePixmap.dispose();
        return whiteTexture;
    }

    @Override
    public Texture createRoundedRectangleTexture(int width, int height, Color color, float cornerRadius) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);

        // Draw rounded rectangle
        pixmap.fillRectangle(0, (int)cornerRadius, width, height - (int)cornerRadius*2);
        pixmap.fillRectangle((int)cornerRadius, 0, width - (int)cornerRadius*2, height);
        pixmap.fillCircle((int)cornerRadius, (int)cornerRadius, (int)cornerRadius);
        pixmap.fillCircle((int)cornerRadius, height - (int)cornerRadius, (int)cornerRadius);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public BitmapFont createWhiteFont() {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
        return font;
    }

    @Override
    public Pixmap createPixmap(int width, int height, Pixmap.Format format) {
        return new Pixmap(width, height, format);
    }

    @Override
    public BitmapFont createBitmapFont() {
        return new BitmapFont(Gdx.files.internal("rubik.fnt"));
    }

    @Override
    public Matrix4 getScreenProjectionMatrix() {
        return new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public Label createLabel(CharSequence text, Label.LabelStyle labelStyle) {
        return new Label(text, labelStyle);
    }

    @Override
    public Image createImage(Texture texture) {
        return new Image(texture);
    }
}
