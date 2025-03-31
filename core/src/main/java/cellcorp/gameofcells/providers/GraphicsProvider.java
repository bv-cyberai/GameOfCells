package cellcorp.gameofcells.providers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;

/**
 * Provides information from `Gdx.graphics`, which is needed by game code.
 * If you need methods from `com.badlogic.gdx.Graphics` in game code,
 * add them here and call the method in {@link DefaultGraphicsProvider}.
 */
public interface GraphicsProvider {
    /** @return the width of the client area in logical pixels. */
    int getWidth();

    /** @return the height of the client area in logical pixels */
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
     * @param viewRectWidth The width of the camera view rectangle
     *                      (the rectangle of the world which the camera will display).
     *                      Equivalent to the questionably-named {@link Viewport} `worldWidth`
     * @param viewRectHeight The width of the camera view rectangle
     *                      Equivalent to the questionably-named {@link Camera} `worldHeight`
     * @return A newly-constructed {@link FitViewport}
     */
    FitViewport createFitViewport(float viewRectWidth, float viewRectHeight);

    /**
     * Create a viewport with the given camera. Equivalent to calling `new FitViewport(camera)`,
     * but won't crash test code.
     *
     * @param viewRectWidth The width of the camera view rectangle
     *                      (the rectangle of the world which the camera will display).
     *                      Equivalent to the questionably-named {@link Viewport} `worldWidth`
     * @param viewRectHeight The width of the camera view rectangle
     *                      Equivalent to the questionably-named {@link Camera} `worldHeight`
     * @return A newly-constructed {@link Viewport}
     */
    FitViewport createFitViewport(float viewRectWidth, float viewRectHeight, Camera camera);

    /**
     * Create a viewport. Equivalent to calling `new FitViewport()`,
     * but won't crash test code.
     *
     * @param viewRectWidth The width of the camera view rectangle
     *                      (the rectangle of the world which the camera will display).
     *                      Equivalent to the questionably-named {@link Viewport} `worldWidth`
     * @param viewRectHeight The width of the camera view rectangle
     *                      Equivalent to the questionably-named {@link Camera} `worldHeight`
     * @return A newly-constructed {@link Viewport}
     */
    Viewport createViewport(float viewRectWidth, float viewRectHeight);
    
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
     * Create a pixmap with the given width, height, and format.
     * @param width
     * @param height
     * @param format
     * @return
     */
    Pixmap createPixmap(int width, int height, Pixmap.Format format);

    /**
     * Create a bitmap font.
     * @return
     */
    BitmapFont createBitmapFont();

    /**
     * Get the screen projection matrix.
     * @return
     */
    Matrix4 getScreenProjectionMatrix();
}
