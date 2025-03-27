package cellcorp.gameofcells.objects;

import java.util.*;
import java.util.stream.Collectors;

import cellcorp.gameofcells.Util;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Spawns, despawns, and draws glucose.
 * Unlike zone manager, doesn't care about reproducibility after despawning.
 *
 * @author Brendon Vinyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 03/05/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class GlucoseManager {

    /**
     * Base chance for glucose to spawn in a sub-chunk, outside any basic zones
     */
    private static final float BASE_SPAWN_CHANCE = 0.001f;
    /**
     * Max additional spawn chance for a sub-chunk inside a basic zone
     */
    private static final float MAX_ADDITIONAL_SPAWN_CHANCE = 0.90f;

    /**
     * Adds a little extra space for spawning around actual zone radius
     */
    private static final float ZONE_ADDITIONAL_SPAWN_RADIUS = 100f;

    public final class SubChunk {
        /**
         * Number of rows/cols of sub-chunks to split the chunk into when spawning.
         * Gives 2500 sub-chunks per chunk.
         */
        private static final int SUB_CHUNK_ROWS = 50;
        private static final float SUB_CHUNK_LENGTH = Chunk.CHUNK_LENGTH / (float) SUB_CHUNK_ROWS;

        private final Chunk chunk;
        private final int row;
        private final int col;

        public SubChunk(Chunk chunk, int row, int col) {
            this.chunk = chunk;
            this.row = row;
            this.col = col;
        }

        public int row() {
            return row;
        }

        public int col() {
            return col;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (SubChunk) obj;
            return this.row == that.row &&
                    this.col == that.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        /**
         * Get the rectangle this sub-chunk covers in the world.
         */
        public Rectangle rect() {
            var chunkRect = chunk.toRectangle();
            return new Rectangle(
                    chunkRect.getX() + col * SUB_CHUNK_LENGTH,
                    chunkRect.getY() + row * SUB_CHUNK_LENGTH,
                    SUB_CHUNK_LENGTH,
                    SUB_CHUNK_LENGTH
            );
        }

        public Vector2 center() {
            var chunkRect = chunk.toRectangle();
            return new Vector2(
                    chunkRect.getX() + col * SUB_CHUNK_LENGTH + SUB_CHUNK_LENGTH / 2,
                    chunkRect.getY() + row * SUB_CHUNK_LENGTH + SUB_CHUNK_LENGTH / 2
            );
        }
    }

    private final AssetManager assetManager;
    private final GamePlayScreen gamePlayScreen;
    private final ZoneManager zoneManager;
    private final Cell cell; // Store the cell's position

    /**
     * Stored as a hashmap of chunks, for easy despawning.
     * If the player eats all the glucose in a chunk, it remains an empty set in the hashmap.
     * When a chunk is despawned, the set of glucose is removed entirely from the hashmap.
     */
    private final Map<Chunk, List<Glucose>> glucoses;
    private final Random rand;

    public GlucoseManager(AssetManager assetManager, GamePlayScreen gamePlayScreen, ZoneManager zoneManager, Cell cell) {
        this.assetManager = assetManager;
        this.gamePlayScreen = gamePlayScreen;
        this.zoneManager = zoneManager;
        this.cell = cell;

        this.glucoses = new HashMap<>();
        this.rand = new Random();
    }

//    /**
//     * Random Coordinates
//     *
//     * Creates random coordinates for use in glucose creation.
//     *
//     * @return float[xCoordinate,yCoordinate]
//     */
//    private float[] getRandomCoordinate(float cellX, float cellY) {
//        // Duplicate coordinates may occur ~ 1 in 1,000,000 generations
//        // Overlap still occurs, These might both just be best handled
//        // pushing them apart via collision detection
//        // Another solution is to track coordinates of the cells in
//        // a separate array.
//
//        float[] coordinateArray = new float[2];
//        int radiusInt = (int) RADIUS;
//
//        // subtract by radius int to avoid off screen
//        // hardcoded values may not work well when camera moves.
//        int genX, genY;
//        float distance;
//
//        do {
//            genX = rand.nextInt(1200 - radiusInt );
//            genY = rand.nextInt(800 - radiusInt);
//
//            // Calculate distance between generated coordinates and the cell's initial position
//
//            distance = (float) Math.sqrt(Math.pow(genX - cellX, 2) + Math.pow(genY - cellY, 2));
//        } while (distance < MIN_SPAWN_DISTANCE); // Regenerate if too close to the cell
//
//        coordinateArray[0] = genX;
//        coordinateArray[1] = genY;
//
//        return coordinateArray;
//    }

    public void spawnInRange(int row0, int col0, int row1, int col1) {
        for (int row = row0; row < row1; row++) {
            for (int col = col0; col < col1; col++) {
                var chunk = new Chunk(row, col);
                spawnInChunk(chunk);
            }
        }
    }

    /**
     * Spawn
     */
    private void spawnInChunk(Chunk chunk) {
        // Even if the set of glucose in the chunk is empty,
        // don't respawn it unless it's been despawned.
        // That way, the player can't just go back and forth between 3 chunks or so.
        if (glucoses.containsKey(chunk)) {
            return;
        }

        var glucoseList = new ArrayList<Glucose>();
        for (int subRow = 0; subRow < SubChunk.SUB_CHUNK_ROWS; subRow++) {
            for (int subCol = 0; subCol < SubChunk.SUB_CHUNK_ROWS; subCol++) {
                var subChunk = new SubChunk(chunk, subRow, subCol);
                var center = subChunk.center();
                float spawnChance = spawnChance(center.x, center.y);

                if (rand.nextFloat() <= spawnChance) {
                    var glucose = spawnInSubChunk(subChunk);
                    glucoseList.add(glucose);
                }
            }
        }
        glucoses.put(chunk, glucoseList);
    }

    private float spawnChance(float x, float y) {
        var distance = zoneManager.distanceToNearestBasicZone(x, y);
        float additionalSpawnChance;
        if (distance.isEmpty()) {
            additionalSpawnChance = 0;
        } else {
            // Get the % distance from zone center in range [0, 1]
            float spawnRadius = Zone.ZONE_RADIUS + ZONE_ADDITIONAL_SPAWN_RADIUS;
            float distanceRatio = 1 - Util.smoothStep(0f, spawnRadius, (float) distance.get().doubleValue());
            additionalSpawnChance = distanceRatio * MAX_ADDITIONAL_SPAWN_CHANCE;
        }
        return BASE_SPAWN_CHANCE + additionalSpawnChance;
    }

    private Glucose spawnInSubChunk(SubChunk subChunk) {
        // This the spawn location be a few sub-chunks outside the sub-chunk,
        // to cut down on the grid-like look
        // This can place a glucose outside its assigned chunk.
        // I _think_ this will never be a problem for collision/drawing,
        // because even if it goes outside the current chunk, we're always drawing/checking collision
        // a few chunks out.
        var rect = subChunk.rect();
        float x0 = rect.x - 2 * SubChunk.SUB_CHUNK_LENGTH;
        float y0 = rect.y - 2 * SubChunk.SUB_CHUNK_LENGTH;
        float x1 = x0 + 5 * SubChunk.SUB_CHUNK_LENGTH;
        float y1 = y0 + 5 * SubChunk.SUB_CHUNK_LENGTH;
        float x = rand.nextFloat(x0, x1);
        float y = rand.nextFloat(y0, y1);
        return new Glucose(assetManager, x, y);
    }

    public void despawnOutsideRange(int row0, int col0, int row1, int col1) {
        var keep = glucoses.
                keySet().
                stream().
                filter(chunk ->
                               row0 <= chunk.row() && chunk.row() < row1
                                       && col0 <= chunk.col() && chunk.col() < col1
                ).collect(Collectors.toList());
        glucoses.keySet().retainAll(keep);
    }

    /**
     * Checks for cell <-> glucose collisions
     */
    public void update() {
        handleCollisions();
    }

    /**
     * Checks for collisions between the cell and each glucose in adjacent chunks.
     */
    private void handleCollisions() {
        var currentChunk = Chunk.fromWorldCoords(cell.getX(), cell.getY());
        var adjacentChunks = currentChunk.adjacentChunks();

        for (var chunk : adjacentChunks) {
            handleCollisionsInChunk(chunk);
        }
    }

    private void handleCollisionsInChunk(Chunk chunk) {
        var glucoseList = glucoses.get(chunk);
        if (glucoseList == null) {
            return;
        }

        var collisions = glucoseList
            .stream()
            .filter(glucose ->
                cell.getCircle().overlaps(glucose.getCircle())
            ).collect(Collectors.toList());

        if (!collisions.isEmpty()) {
            gamePlayScreen.reportGlucoseCollision();
        }
        for (var ignored : collisions) {
            cell.addCellATP(Glucose.ATP_PER_GLUCOSE);
        }
        glucoseList.removeAll(collisions);
    }

    /**
     * Draw glucose in current chunk
     */
    public void draw(SpriteBatch spriteBatch) {
        // Draw all glucose in each adjacent chunk.
        // Will unnecessarily draw some glucose, but should be fine.
        var currentChunk = Chunk.fromWorldCoords(cell.getX(), cell.getY());
        var adjacentChunks = currentChunk.adjacentChunks();
        for (var chunk : adjacentChunks) {
            drawInChunk(spriteBatch, chunk);
        }
    }

    private void drawInChunk(SpriteBatch spriteBatch, Chunk chunk) {
        var glucoseList = glucoses.get(chunk);
        if (glucoseList == null) {
            return;
        }

        spriteBatch.begin();
        for (var glucose : glucoseList) {
            glucose.draw(spriteBatch);
        }
        spriteBatch.end();
    }

    /**
     * For use in tests only.
     * Get an array of all glucose.
     */
    public List<Glucose> getGlucoseArray() {
        return glucoses
            .values()
            .stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }
}
