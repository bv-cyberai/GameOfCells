package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Objects;

public final class Zone {
    public static final float ZONE_RADIUS = 800;
    private static final float ZONE_TEXTURE_RADIUS = 1000;
    /**
     * Minimum distance required between zone centers to be "non-overlapping"
     */
    public static final float ZONE_NON_OVERLAPPING_DISTANCE = 800;
    public static final float ACID_ZONE_MAX_DAMAGE_PER_SECOND = 10;
    public static final float ACID_ZONE_DAMAGE_INCREMENT_SECONDS = 0.5f;

    private final AssetManager assetManager;
    private final String texturePath;

    private final float x;
    private final float y;

    public Zone(AssetManager assetManager, String texturePath, float x, float y) {
        this.assetManager = assetManager;
        this.texturePath = texturePath;
        this.x = x;
        this.y = y;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public void draw(SpriteBatch spriteBatch) {
        var texture = assetManager.get(texturePath, Texture.class);
        float bottomLeftX = x - ZONE_TEXTURE_RADIUS;
        float bottomLeftY = y - ZONE_TEXTURE_RADIUS;
        float diameter = ZONE_TEXTURE_RADIUS * 2;

        spriteBatch.draw(texture, bottomLeftX, bottomLeftY, diameter, diameter);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        if (GamePlayScreen.DEBUG_DRAW_ENABLED) {
            shapeRenderer.point(x, y, 0);
            shapeRenderer.circle(x, y, ZONE_RADIUS);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Zone) obj;
        return Float.floatToIntBits(this.x) == Float.floatToIntBits(that.x) &&
                Float.floatToIntBits(this.y) == Float.floatToIntBits(that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public double distanceFrom(float x, float y) {
        return Math.sqrt(
                Math.pow(this.x() - x, 2) + Math.pow(this.y() - y, 2)
        );
    }

    /**
     * @return the radius of the zone
     */
    public float getZoneRadius() {
        return ZONE_RADIUS;
    }
}
