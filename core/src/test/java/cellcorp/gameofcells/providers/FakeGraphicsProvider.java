package cellcorp.gameofcells.providers;

import cellcorp.gameofcells.Main;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
}
