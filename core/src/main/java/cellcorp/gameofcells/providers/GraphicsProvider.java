package cellcorp.gameofcells.providers;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Provides information from `Gdx.graphics`, which is needed by game code.
 * If you need methods from `com.badlogic.gdx.Graphics` in game code,
 * add them here and call the method in {@link DefaultGraphicsProvider}.
 */
public interface GraphicsProvider {
    /**
     * @return the width of the client area in logical pixels.
     */
    int getWidth();

    /**
     * @return the height of the client area in logical pixels
     */
    int getHeight();

    /**
     * Create an orthographic camera.
     * Equivalent to calling `new OrthographicCamera(viewRectWidth, viewRectHeight)`,
     * but won't crash test code.
     *
     * @return A newly-constructed {@link Camera}
     */
    OrthographicCamera createCamera();

    /**
     * Create a viewport. Equivalent to calling `new FitViewport()`,
     * but won't crash test code.
     *
     * @param viewRectWidth  The width of the camera view rectangle
     *                       (the rectangle of the world which the camera will display).
     *                       Equivalent to the questionably-named {@link Viewport} `worldWidth`
     * @param viewRectHeight The width of the camera view rectangle
     *                       Equivalent to the questionably-named {@link Camera} `worldHeight`
     * @return A newly-constructed {@link FitViewport}
     */
    FitViewport createFitViewport(float viewRectWidth, float viewRectHeight);

    /**
     * Create a viewport with the given camera. Equivalent to calling `new FitViewport(camera)`,
     * but won't crash test code.
     *
     * @param viewRectWidth  The width of the camera view rectangle
     *                       (the rectangle of the world which the camera will display).
     *                       Equivalent to the questionably-named {@link Viewport} `worldWidth`
     * @param viewRectHeight The width of the camera view rectangle
     *                       Equivalent to the questionably-named {@link Camera} `worldHeight`
     * @return A newly-constructed {@link Viewport}
     */
    FitViewport createFitViewport(float viewRectWidth, float viewRectHeight, Camera camera);

    /**
     * Create a shape renderer. Equivalent to calling `new ShapeRenderer()`,
     * but won't crash test code.
     *
     * @return A newly-constructed {@link ShapeRenderer}
     */
    ShapeRenderer createShapeRenderer();

    /**
     * Create a sprite batch. Equivalent to calling `new SpriteBatch()`,
     * but won't crash test code.
     *
     * @return A newly-constructed {@link SpriteBatch}
     */
    SpriteBatch createSpriteBatch();

    /**
     * Create a texture with the given width, height, and format.
     *
     * @param width
     * @param height
     * @param format
     * @return
     */
    Texture createTexture(int width, int height, Pixmap.Format format);

    /**
     * Create a texture from the given pixmap.
     */
    Texture createWhitePixelTexture();

    /**
     * Create a rounded rectangle texture with the given parameters.
     * This is a convenience method for creating a texture with rounded corners.
     *
     * @param width
     * @param height
     * @param color
     * @param cornerRadius
     * @return
     */
    Texture createRoundedRectangleTexture(int width, int height, Color color, float cornerRadius);

    /**
     * Create a bitmap font with the given parameters.
     *
     * @return
     */
    BitmapFont createWhiteFont();

    /**
     * Create a pixmap with the given width, height, and format.
     *
     * @param width
     * @param height
     * @param format
     * @return
     */
    Pixmap createPixmap(int width, int height, Pixmap.Format format);

    /**
     * Create a bitmap font.
     *
     * @return
     */
    BitmapFont createBitmapFont();

    /**
     * Get the screen projection matrix.
     *
     * @return
     */
    Matrix4 getScreenProjectionMatrix();

    /**
     * Create a Label
     */
    Label createLabel(CharSequence text, Label.LabelStyle labelStyle);

    /**
     * Create an Image
     */
    Image createImage(Texture texture);

    /**
     * Create a drawable texture region
     */
    TextureRegionDrawable createTextureRegionDrawable(Texture texture);

    /**
     * Create a glyph layout
     */
    GlyphLayout createGlyphLayout(BitmapFont font, String text);

    /**
     * Create a Scene2d stage
     */
    default Stage createStage(int viewRectWidth, int viewRectHeight) {
        return new Stage(
            this.createFitViewport(viewRectWidth, viewRectHeight),
            this.createSpriteBatch()
        );
    }
}
