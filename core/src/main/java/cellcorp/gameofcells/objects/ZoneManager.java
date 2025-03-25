package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.AssetFileNames;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.*;

/**
 * Manages acid and basic zones.
 * ZoneManager spawns at random locations in chunks, with a random spawn chance,
 * but it uses seeded Random calls, so a given ZoneManager will always
 * spawn zones at the same positions in the world, even between despawns.
 */
public class ZoneManager {

    private static final double ACID_ZONE_SPAWN_CHANCE = 0.33;
    private static final double BASIC_ZONE_SPAWN_CHANCE = 0.33;

    private final AssetManager assetManager;
    private final Cell cell;
    private final RandomFromHash random;


    /**
     * Stored as a set of chunks, for easy despawning.
     */
    private final Map<Chunk, AcidZone> acidZones = new HashMap<>();
    private final Map<Chunk, AcidZone> basicZones = new HashMap<>();

    private float timer = 0f;
    private float damageCounter = 0f;

    public ZoneManager(AssetManager assetManager, Cell cell) {
        this.assetManager = assetManager;
        this.cell = cell;
        this.random = new RandomFromHash(SpawnManager.RANDOM_SEED);
    }

    /**
     * Damage the cell if it's in an acid zone.
     * Damage amount and frequency is based on the cell's distance to the center of the acid zone.
     */
    public void update(float deltaTimeSeconds) {
        // Smoothstep rather than linear scaling, because I wanted to.
        var distance = distanceToNearestAcidZone(cell.getX(), cell.getY());
        if (distance.isEmpty()) {
            return;
        }
        // Get the % distance from zone center in range [0, 1]
        var distanceRatio = 1 - smoothStep(0f, AcidZone.ACID_ZONE_RADIUS, (float) distance.get().doubleValue());
        var damage = distanceRatio * AcidZone.ACID_ZONE_MAX_DAMAGE_PER_SECOND * deltaTimeSeconds;
        if (timer > AcidZone.ACID_ZONE_DAMAGE_INCREMENT_SECONDS && damageCounter > 1) {
            cell.reduceHealth((int)damageCounter);
            timer = deltaTimeSeconds;
            damageCounter = damage;
        } else if (damage == 0) {
            // Reset damage counter if we move outside an acid zone
            timer = 0f;
            damageCounter = 0f;
        } else {
            timer += deltaTimeSeconds;
            damageCounter += damage;
        }
    }

    /**
     * Distance from the give location to the nearest zone, if any exist.
     */
    private Optional<Double> distanceToNearestAcidZone(float x, float y) {
        var nearestZone = acidZones.values().stream().min(Comparator.comparingDouble(z -> z.distanceFrom(x, y)));
        return nearestZone.map(z -> z.distanceFrom(x, y));
    }

    /**
     * Adapted from Wikipedia:
     * <a href="https://en.wikipedia.org/wiki/Smoothstep">smoothstep</a>
     */
    private float smoothStep(float min, float max, float x) {
        // Scale, and clamp x to 0..1 range
        x = Math.min(Math.max((x - min) / (max - min), 0f), 1f);

        return x * x * (3.0f - 2.0f * x);
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        drawBasicZones(spriteBatch, shapeRenderer);
        drawAcidZones(spriteBatch, shapeRenderer);
    }

    private void drawAcidZones(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        for (var zone : acidZones.values()) {
            zone.draw(spriteBatch, shapeRenderer);
        }
    }

    private void drawBasicZones(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        for (var zone : basicZones.values()) {
            zone.draw(spriteBatch, shapeRenderer);
        }
    }

    /**
     * Spawn zones outside the range of chunks `Chunk(row0, col0) .. Chunk(row1, col1)`
     */
    public void spawnInRange(int row0, int col0, int row1, int col1) {

        for (int row = row0; row < row1; row++) {
            for (int col = col0; col < col1; col++) {
                var chunk = new Chunk(row, col);
                spawnZone(acidZones, ACID_ZONE_SPAWN_CHANCE, AssetFileNames.ACID_ZONE, 2, chunk);
                spawnZone(basicZones, BASIC_ZONE_SPAWN_CHANCE, AssetFileNames.BASIC_ZONE, 3, chunk);
            }
        }
    }

    /**
     * Determines whether to spawn an acid zone in the given chunk,
     * and spawns it.
     * Zones will not spawn too close to the center of other zones.
     */
    public void spawnZone(Map<Chunk, AcidZone> zoneMap, double spawnChance, String texturePath, int seed, Chunk chunk) {
        if (zoneMap.containsKey(chunk)) {
            return;
        }

        var randomValue = random.floatFrom(chunk.hashCode());
        if (randomValue < spawnChance) {
            var rect = chunk.toRectangle();

            float randX = random.floatFrom(chunk.hashCode() * seed * 2);
            float x = rect.x + randX * Chunk.CHUNK_LENGTH;
            float randY = random.floatFrom(chunk.hashCode() * seed * 3);
            float y = rect.y + randY * Chunk.CHUNK_LENGTH;
            zoneMap.put(chunk, new AcidZone(assetManager, texturePath, x, y));
        }
    }

    /**
     * Despawn zones outside the range of chunks `Chunk(row0, col0) .. Chunk(row1, col1)`
     */
    public void despawnOutsideRange(int row0, int col0, int row1, int col1) {
        var keepZones = acidZones.
                keySet().
                stream().
                filter(chunk ->
                               row0 <= chunk.row() && chunk.row() < row1
                                       && col0 <= chunk.col() && chunk.col() < col1
                ).toList();
        acidZones.keySet().retainAll(keepZones);
    }
}
