package cellcorp.gameofcells.providers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
     * Create a sprite batch. Equivalent to calling `new SpriteBatch()`,
     * but won't crash test code.
     * 
     * @return A newly-constructed {@link SpriteBatch}
     */
    SpriteBatch createSpriteBatch();

}
