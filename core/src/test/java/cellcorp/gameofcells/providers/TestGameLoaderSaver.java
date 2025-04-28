package cellcorp.gameofcells.providers;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Stats;
import cellcorp.gameofcells.runner.GameRunner;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestGameLoaderSaver {
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
    public void testSaveThenLoad() {
        var gameRunner = GameRunner.create();

        // Hold down space and step forward a frame.
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        // Make sure we're on the gameplay screen, and that some glucose have spawned
        var screen = gameRunner.game.getScreen();
        assertInstanceOf(GamePlayScreen.class, screen);
        var currentGamePlayScreen = (GamePlayScreen) screen;

        var gameLoaderSaver = currentGamePlayScreen.getGameLoaderSaver();
        Preferences mockPrefs = mock(Preferences.class);
        when(mockPrefs.getInteger("cellHealth")).thenReturn(50);
        when(mockPrefs.getInteger("cellATP")).thenReturn(60);
        when(mockPrefs.getFloat("cellSize")).thenReturn(200f);

        when(mockPrefs.getBoolean("smallSize")).thenReturn(true);
        when(mockPrefs.getBoolean("mediumSize")).thenReturn(false);
        when(mockPrefs.getBoolean("largeSize")).thenReturn(false);
        when(mockPrefs.getBoolean("massiveSize")).thenReturn(false);

        when(mockPrefs.getBoolean("mito")).thenReturn(true);
        when(mockPrefs.getBoolean("ribo")).thenReturn(false);
        when(mockPrefs.getBoolean("flag")).thenReturn(false);
        when(mockPrefs.getBoolean("nuke")).thenReturn(false);

        when(mockPrefs.getInteger("atpGen")).thenReturn(10);
        when(mockPrefs.getInteger("glukeCollected")).thenReturn(9);
        when(mockPrefs.getFloat("distanceMoved")).thenReturn(8f);
        when(mockPrefs.getFloat("time")).thenReturn(7f);

        when(mockPrefs.getBoolean("glukePopup")).thenReturn(false);
        when(mockPrefs.getBoolean("acidPopup")).thenReturn(false);
        when(mockPrefs.getBoolean("healPopup")).thenReturn(true);
        when(mockPrefs.getBoolean("membranePopup")).thenReturn(true);

        gameLoaderSaver.setUpMockPrefrences(mockPrefs);

        Stats stats = currentGamePlayScreen.stats;
        var cell = currentGamePlayScreen.getCell();

        //Set up a game state different from the saved state
        cell.setCellHealth(50);
        cell.setCellATP(60);
        stats.atpGenerated = 40;
        stats.gameTimer = 40f;
        stats.distanceMoved = 35f;
        stats.glucoseCollected = 20;

        //Load
        gameLoaderSaver.loadState();

        assertEquals(50, cell.getCellHealth());
        assertEquals(60, cell.getCellATP());
        assertEquals(200f,cell.getCellSize());

        assertTrue(cell.hasSmallSizeUpgrade());
        assertFalse(cell.hasMediumSizeUpgrade());
        assertFalse(cell.hasLargeSizeUpgrade());
        assertFalse(cell.hasMassiveSizeUpgrade());

        assertTrue(cell.hasMitochondria());
        assertFalse(cell.hasRibosomes());
        assertFalse(cell.hasFlagella());
        assertFalse(cell.hasNucleus());

        assertEquals(10, stats.atpGenerated);
        assertEquals(9,stats.glucoseCollected);
        assertEquals(8,stats.distanceMoved);
        assertEquals(7,stats.gameTimer);

        assertTrue(currentGamePlayScreen.getHealAvailablePopup().wasShown());
        assertTrue(currentGamePlayScreen.getCellMembranePopup().wasShown());
        assertFalse(currentGamePlayScreen.getAcidZonePopup().wasShown());
        assertFalse(currentGamePlayScreen.getBasicZonePopup().wasShown());







    }
}
