package cellcorp.gameofcells.objects;

/**
 * Determines whether to spawn and despawn based on current cell position.
 */
public class SpawnManager {

    public static final long RANDOM_SEED = System.nanoTime();

    /**
     * SpawnManager spawns in a `SPAWN_CHUNK_RANGE` x `SPAWN_CHUNK_RANGE` grid around the cell position.
     */
    public static final int SPAWN_CHUNK_RANGE = 3;
    /**
     * SpawnManager despawns in a `DESPAWN_CHUNK_RANGE` x `DESPAWN_CHUNK_RANGE` grid around the cell position.
     */
    public static final int DESPAWN_CHUNK_RANGE = 10;

    private final Cell cell;
    private final ZoneManager zoneManager;
    private final GlucoseManager glucoseManager;

    /**
     * Create a new spawn manager.
     */
    public SpawnManager(Cell cell, ZoneManager zoneManager, GlucoseManager glucoseManager) {
        this.cell = cell;
        this.zoneManager = zoneManager;
        this.glucoseManager = glucoseManager;
    }

    /**
     * Spawn and despawn chunks, if needed, based on the current cell position.
     */
    public void update() {
        spawn();
        despawn();
    }

    /**
     * Determines which chunks, if any, to spawn in, based on the current cell position,
     * then calls the spawn methods of various managers.
     */
    private void spawn() {
        var centerChunk = Chunk.fromWorldCoords(cell.getX(), cell.getY());
        var row0 = centerChunk.row() - SPAWN_CHUNK_RANGE;
        var col0 = centerChunk.col() - SPAWN_CHUNK_RANGE;
        var row1 = centerChunk.row() + SPAWN_CHUNK_RANGE;
        var col1 = centerChunk.col() + SPAWN_CHUNK_RANGE;

        zoneManager.spawnInRange(row0, col0, row1, col1);
        glucoseManager.spawnInRange(row0, col0, row1, col1);
    }

    /**
     * Determines which chunks, if any, to despawn in, based on the current cell position,
     * then calls the despawn methods of various managers.
     */
    private void despawn() {
        var centerChunk = Chunk.fromWorldCoords(cell.getX(), cell.getY());
        var row0 = centerChunk.row() - DESPAWN_CHUNK_RANGE;
        var col0 = centerChunk.col() - DESPAWN_CHUNK_RANGE;
        var row1 = centerChunk.row() + DESPAWN_CHUNK_RANGE;
        var col1 = centerChunk.col() + DESPAWN_CHUNK_RANGE;
        zoneManager.despawnOutsideRange(row0, col0, row1, col1);
        glucoseManager.despawnOutsideRange(row0, col0, row1, col1);
    }
}
