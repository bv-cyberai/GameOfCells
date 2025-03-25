package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.AssetFileNames;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Objects;

public final class AcidZone {
    public static final float ACID_ZONE_RADIUS = 600;

    private final AssetManager assetManager;

    private final float x;
    private final float y;

    public AcidZone(AssetManager assetManager, float x, float y) {
        this.assetManager = assetManager;
        this.x = x;
        this.y = y;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    /**
     * Expects `spriteBatch` to be already-begun.
     */
    public void draw(SpriteBatch spriteBatch) {
        var texture = assetManager.get(AssetFileNames.ACID_ZONE, Texture.class);
        float bottomLeftX = x - ACID_ZONE_RADIUS / 2f;
        float bottomLeftY = y - ACID_ZONE_RADIUS / 2f;
        float diameter = ACID_ZONE_RADIUS * 2;

        spriteBatch.draw(texture, bottomLeftX, bottomLeftY, diameter, diameter);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AcidZone) obj;
        return Float.floatToIntBits(this.x) == Float.floatToIntBits(that.x) &&
                Float.floatToIntBits(this.y) == Float.floatToIntBits(that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
