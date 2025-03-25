package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Shape;

import java.util.Objects;

public final class AcidZone {
    public static final float ACID_ZONE_RADIUS = 400;
    public static final float ACID_ZONE_MAX_DAMAGE_PER_SECOND = 10;
    public static final float ACID_ZONE_DAMAGE_INCREMENT_SECONDS = 0.5f;
    private static final float ACID_ZONE_TEXTURE_RADIUS = 500;

    private final AssetManager assetManager;
    private final String texturePath;

    private final float x;
    private final float y;

    public AcidZone(AssetManager assetManager, String texturePath, float x, float y) {
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

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        var texture = assetManager.get(texturePath, Texture.class);
        float bottomLeftX = x - ACID_ZONE_TEXTURE_RADIUS;
        float bottomLeftY = y - ACID_ZONE_TEXTURE_RADIUS;
        float diameter = ACID_ZONE_TEXTURE_RADIUS * 2;

        spriteBatch.begin();
        spriteBatch.draw(texture, bottomLeftX, bottomLeftY, diameter, diameter);
        spriteBatch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (GamePlayScreen.DEBUG_DRAW_ENABLED) {
            shapeRenderer.point(x, y, 0);
            shapeRenderer.circle(x, y, ACID_ZONE_RADIUS);
            shapeRenderer.end();
        }
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

    public double distanceFrom(float x, float y) {
        return Math.sqrt(
                Math.pow(this.x() - x, 2) + Math.pow(this.y() - y, 2)
        );
    }
}
