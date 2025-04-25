package cellcorp.gameofcells.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestSpawnManager {

    private Cell mockCell;
    private ZoneManager mockZoneManager;
    private GlucoseManager mockGlucoseManager;
    private SpawnManager spawnManager;

    @BeforeEach
    public void setup() {
        mockCell = mock(Cell.class);
        mockZoneManager = mock(ZoneManager.class);
        mockGlucoseManager = mock(GlucoseManager.class);

        // Simulated world position for the cell
        when(mockCell.getX()).thenReturn(256f);  // e.g. world units
        when(mockCell.getY()).thenReturn(384f);

        spawnManager = new SpawnManager(mockCell, mockZoneManager, mockGlucoseManager);
    }

    @Test
    public void testUpdateCallsSpawnAndDespawnCorrectly() {
        spawnManager.update();

        Chunk centerChunk = Chunk.fromWorldCoords(256f, 384f);

        int spawnRow0 = centerChunk.row() - SpawnManager.SPAWN_CHUNK_RANGE;
        int spawnCol0 = centerChunk.col() - SpawnManager.SPAWN_CHUNK_RANGE;
        int spawnRow1 = centerChunk.row() + SpawnManager.SPAWN_CHUNK_RANGE;
        int spawnCol1 = centerChunk.col() + SpawnManager.SPAWN_CHUNK_RANGE;

        int despawnRow0 = centerChunk.row() - SpawnManager.DESPAWN_CHUNK_RANGE;
        int despawnCol0 = centerChunk.col() - SpawnManager.DESPAWN_CHUNK_RANGE;
        int despawnRow1 = centerChunk.row() + SpawnManager.DESPAWN_CHUNK_RANGE;
        int despawnCol1 = centerChunk.col() + SpawnManager.DESPAWN_CHUNK_RANGE;

        // ✅ Verify both managers are told to spawn/despawn in correct regions
        verify(mockZoneManager).spawnInRange(spawnRow0, spawnCol0, spawnRow1, spawnCol1);
        verify(mockGlucoseManager).spawnInRange(spawnRow0, spawnCol0, spawnRow1, spawnCol1);

        verify(mockZoneManager).despawnOutsideRange(despawnRow0, despawnCol0, despawnRow1, despawnCol1);
        verify(mockGlucoseManager).despawnOutsideRange(despawnRow0, despawnCol0, despawnRow1, despawnCol1);
    }

    @Test
    public void testGetZoneManagerReturnsInjectedManager() {
        assertSame(mockZoneManager, spawnManager.getZoneManager(), "getZoneManager() should return the one provided in constructor");
    }

    @Test
    public void testUpdateDoesNotThrowWithNegativeCoordinates() {
        when(mockCell.getX()).thenReturn(-512f);
        when(mockCell.getY()).thenReturn(-1024f);

        assertDoesNotThrow(() -> spawnManager.update());
    }

    @Test
    public void testUpdateDoesNotThrowWithZeroCoordinates() {
        when(mockCell.getX()).thenReturn(0f);
        when(mockCell.getY()).thenReturn(0f);

        assertDoesNotThrow(() -> spawnManager.update());
    }
}
