package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.runner.GameRunner;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestZoneManager {
    @BeforeAll
    public static void setUpLibGDX() {
        System.setProperty("com.badlogic.gdx.backends.headless.disableNativesLoading", "true");
        // Initialize headless LibGDX
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(
                new ApplicationListener() {
                    @Override
                    public void create() {
                    }

                    @Override
                    public void resize(int width, int height) {
                    }

                    @Override
                    public void render() {
                    }

                    @Override
                    public void pause() {
                    }

                    @Override
                    public void resume() {
                    }

                    @Override
                    public void dispose() {
                    }
                }, config
        );

        // Mock the graphics provider
        Gdx.graphics = Mockito.mock(Graphics.class);
        Mockito.when(Gdx.graphics.getWidth()).thenReturn(Main.DEFAULT_SCREEN_WIDTH);
        Mockito.when(Gdx.graphics.getHeight()).thenReturn(Main.DEFAULT_SCREEN_HEIGHT);
        Mockito.when(Gdx.graphics.getDeltaTime()).thenReturn(1f / 60f);

        GL20 gl20 = Mockito.mock(GL20.class);
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;

        Gdx.files = Mockito.mock(Files.class);
        FileHandle fileHandle = Mockito.mock(FileHandle.class);

        // Return fake config string that has valid values
        String mockConfig = """
                    cellHealth:100
                    cellATP:30
                    maxHealth:100
                    maxATP:100
                    [descriptions]/
                """;

        Mockito.when(fileHandle.readString()).thenReturn(mockConfig);
        Mockito.when(Gdx.files.internal(Mockito.anyString())).thenReturn(fileHandle);
    }

    @Test
    public void canSpawnZonesIn100x100Chunks() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var fakeGamePlayScreen = Mockito.mock(GamePlayScreen.class);

        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        var cell = new Cell(fakeGamePlayScreen, fakeAssetManager, fakeConfigProvider);
        // Acid zones only spawn after small size upgrade.
        cell.setHasSmallSizeUpgrade(true);
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
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        var cell = new Cell(fakeGamePlayScreen, fakeAssetManager, fakeConfigProvider);
        // Acid zones only spawn after small size upgrade.
        cell.setHasSmallSizeUpgrade(true);
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

    @Test
    public void acidZonesDontSpawnUntilSizeUpgrade1() {
        var runner = GameRunner.create();

        // Move to gameplay screen
        runner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        runner.step();
        var screen = (GamePlayScreen) runner.game.getScreen();

        runner.runForSeconds(1);
        var zoneManager = screen.getZoneManager();
        assertTrue(zoneManager.getAcidZones().isEmpty());

        var cell = screen.getCell();
        cell.setHasSmallSizeUpgrade(true);
        runner.step();
        assertFalse(zoneManager.getAcidZones().isEmpty());
    }
}
