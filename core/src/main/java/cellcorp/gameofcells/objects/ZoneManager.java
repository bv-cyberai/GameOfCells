package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Manages acid and basic zones.
 * ZoneManager spawns at random locations in chunks, with a random spawn chance,
 * but it uses seeded Random calls, so a given ZoneManager will always
 * spawn zones at the same positions in the world, even between despawns.
 */
public class ZoneManager {

    private static final double ACID_ZONE_SPAWN_CHANCE = 0.33;

    private final AssetManager assetManager;
    private final RandomFromHash random;

    /**
     * Stored as a set of chunks, for easy despawning.
     */
    private final Map<Chunk, AcidZone> acidZones = new HashMap<>();

    public ZoneManager(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.random = new RandomFromHash(SpawnManager.RANDOM_SEED);
    }

    public void draw(SpriteBatch spriteBatch) {
        drawAcidZones(spriteBatch);
    }

    private void drawAcidZones(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        for (var zone : acidZones.values()) {
            zone.draw(spriteBatch);
        }
        spriteBatch.end();
    }

    /**
     * Determines whether to spawn an acid zone in the given chunk,
     * and spawns it.
     * Zones will not spawn too close to the center of other zones.
     */
    public void spawnAcidZone(Chunk chunk) {
        if (acidZones.containsKey(chunk)) {
            return;
        }

        var randomValue = random.floatFrom(chunk.hashCode());
        if (true) {
//        if (randomValue < ACID_ZONE_SPAWN_CHANCE) {
            var rect = chunk.toRectangle();

            float randX = random.floatFrom(chunk.hashCode() * 2);
            float x = rect.x + randX * Chunk.CHUNK_LENGTH;
            float randY = random.floatFrom(chunk.hashCode() * 3);
            float y = rect.y + randY * Chunk.CHUNK_LENGTH;
            acidZones.put(chunk, new AcidZone(assetManager, x, y));
        }
    }

    public void despawnAcidZone(Chunk chunk) {
        acidZones.clear();
    }
}
