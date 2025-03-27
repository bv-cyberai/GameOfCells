package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.assets.AssetManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestZoneManager {
    @Test
    public void canSpawnZonesIn100x100Chunks() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var fakeGamePlayScreen = Mockito.mock(GamePlayScreen.class);
        var cell = new Cell(fakeGamePlayScreen, fakeAssetManager);
        var zoneManager = new ZoneManager(fakeAssetManager, cell);
        zoneManager.spawnInRange(-50, -50, 50, 50);
        // Non-deterministic, but the probability of < 1000 spawns at 33% chance is super low
        assertTrue(zoneManager.getAcidZones().size() > 1000);
        assertTrue(zoneManager.getBasicZones().size() > 1000);
    }

    @Test
    public void repeatedSpawningProducesSameZones() {
        // In a single zone manager, spawn, despawn, then spawn again
        // The results of the two spans should be the same
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var fakeGamePlayScreen = Mockito.mock(GamePlayScreen.class);
        var cell = new Cell(fakeGamePlayScreen, fakeAssetManager);
        var zoneManager = new ZoneManager(fakeAssetManager, cell);

        zoneManager.spawnInRange(0, 0, 25, 25);
        var acidZones1 = new HashMap<>(zoneManager.getAcidZones());
        var basicZones1 = new HashMap<>(zoneManager.getBasicZones());

        // The range is exclusive. Should despawn everything
        zoneManager.despawnOutsideRange(0, 0, 0, 0);
        assertTrue(zoneManager.getAcidZones().isEmpty());
        assertTrue(zoneManager.getBasicZones().isEmpty());

        zoneManager.spawnInRange(0, 0, 25, 25);
        var acidZones2 = new HashMap<>(zoneManager.getAcidZones());
        var basicZones2 = new HashMap<>(zoneManager.getBasicZones());

        assertEquals(acidZones1, acidZones2);
        assertEquals(basicZones1, basicZones2);
    }
}
