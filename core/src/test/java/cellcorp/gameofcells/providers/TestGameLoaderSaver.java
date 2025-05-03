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

import static java.lang.Integer.getInteger;
import static org.junit.jupiter.api.Assertions.*;

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

        // Make sure we're on the gameplay screen
        var screen = gameRunner.game.getScreen();
        assertInstanceOf(GamePlayScreen.class, screen);
        var currentGamePlayScreen = (GamePlayScreen) screen;

        var gameLoaderSaver = currentGamePlayScreen.getGameLoaderSaver();
        gameLoaderSaver.injectFakePreferences(new FakePreferences());
        Stats stats = currentGamePlayScreen.stats;
        var cell = currentGamePlayScreen.getCell();

        //Setup cell state

        cell.setCellHealth(50);
        cell.setCellATP(60);
        cell.setCellSize(100);

        cell.setHasSmallSizeUpgrade(true);
        cell.setHasMediumSizeUpgrade(true);
        cell.setHasLargeSizeUpgrade(false);
        cell.setHasMassiveSizeUpgrade(false);

        cell.setHasMitochondria(true);
        cell.setHasRibosomes(true);
        cell.setHasFlagella(false);
        cell.setHasNucleus(false);

        stats.atpGenerated = 40;
        stats.gameTimer = 40f;
        stats.distanceMoved = 35f;
        stats.glucoseCollected = 20;

        currentGamePlayScreen.getBasicZonePopup().setWasShown(false);
        currentGamePlayScreen.getAcidZonePopup().setWasShown(false);
        currentGamePlayScreen.getHealAvailablePopup().setWasShown(true);
        currentGamePlayScreen.getCellMembranePopup().setWasShown(true);
        currentGamePlayScreen.getSplitCellPopup().setWasShown(false);

        //Save
        gameLoaderSaver.saveState();

        //Change State
        cell.setCellHealth(70);
        cell.setCellATP(70);
        cell.setCellSize(90);

        cell.setHasSmallSizeUpgrade(false);
        cell.setHasMediumSizeUpgrade(false);
        cell.setHasLargeSizeUpgrade(false);
        cell.setHasMassiveSizeUpgrade(false);

        cell.setHasMitochondria(false);
        cell.setHasRibosomes(false);
        cell.setHasFlagella(false);
        cell.setHasNucleus(false);

        stats.atpGenerated = 50;
        stats.gameTimer = 50f;
        stats.distanceMoved = 45f;
        stats.glucoseCollected = 30;

        currentGamePlayScreen.getBasicZonePopup().setWasShown(false);
        currentGamePlayScreen.getAcidZonePopup().setWasShown(false);
        currentGamePlayScreen.getHealAvailablePopup().setWasShown(false);
        currentGamePlayScreen.getCellMembranePopup().setWasShown(false);
        currentGamePlayScreen.getSplitCellPopup().setWasShown(false);

        //Load State
        gameLoaderSaver.loadState();

        //Verify Load

        assertEquals(50, cell.getCellHealth());
        assertEquals(60, cell.getCellATP());
        assertEquals(100f, cell.getCellSize());

        assertTrue(cell.hasSmallSizeUpgrade());
        assertTrue(cell.hasMediumSizeUpgrade());
        assertFalse(cell.hasLargeSizeUpgrade());
        assertFalse(cell.hasMassiveSizeUpgrade());

        assertTrue(cell.hasMitochondria());
        assertTrue(cell.hasRibosomes());
        assertFalse(cell.hasFlagella());
        assertFalse(cell.hasNucleus());

        assertEquals(40, stats.atpGenerated);
        assertEquals(40, stats.gameTimer);
        assertEquals(35, stats.distanceMoved);
        assertEquals(20, stats.glucoseCollected);

        assertTrue(currentGamePlayScreen.getHealAvailablePopup().wasShown());
        assertTrue(currentGamePlayScreen.getCellMembranePopup().wasShown());
        assertFalse(currentGamePlayScreen.getAcidZonePopup().wasShown());
        assertFalse(currentGamePlayScreen.getBasicZonePopup().wasShown());
        assertFalse(currentGamePlayScreen.getSplitCellPopup().wasShown());

    }

    @Test
    //Ensures that an empty save file doesn't load bad data.
    public void loadWithNoSaveFile() {
        var gameRunner = GameRunner.create();

        // Hold down space and step forward a frame.
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        // Make sure we're on the gameplay screen
        var screen = gameRunner.game.getScreen();
        assertInstanceOf(GamePlayScreen.class, screen);
        var currentGamePlayScreen = (GamePlayScreen) screen;

        var gameLoaderSaver = currentGamePlayScreen.getGameLoaderSaver();
        gameLoaderSaver.injectFakePreferences(new FakePreferences());
        GameLoaderSaver.clearSaveFile();
        Stats stats = currentGamePlayScreen.stats;
        var cell = currentGamePlayScreen.getCell();

        gameLoaderSaver.loadState();
        assertEquals(100, cell.getCellHealth());
        assertFalse(cell.hasSmallSizeUpgrade());
        assertFalse(cell.hasMitochondria());
        assertEquals(0, stats.distanceMoved);

    }

    @Test
    public void autoSaveAfterNukeAndSplit() {
        var gameRunner = GameRunner.create();

        // Hold down space and step forward a frame.
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        // Make sure we're on the gameplay screen
        var screen = gameRunner.game.getScreen();
        assertInstanceOf(GamePlayScreen.class, screen);
        var currentGamePlayScreen = (GamePlayScreen) screen;

        var gameLoaderSaver = currentGamePlayScreen.getGameLoaderSaver();
        gameLoaderSaver.injectFakePreferences(new FakePreferences());
        GameLoaderSaver.clearSaveFile();
        Stats stats = currentGamePlayScreen.stats;
        var cell = currentGamePlayScreen.getCell();

        //Setup cell state

        cell.setCellHealth(100);
        cell.setCellATP(100);
        cell.setCellSize(100);

        cell.setHasSmallSizeUpgrade(true);
        cell.setHasMediumSizeUpgrade(true);
        cell.setHasLargeSizeUpgrade(true);
        cell.setHasMassiveSizeUpgrade(true);

        cell.setHasMitochondria(true);
        cell.setHasRibosomes(true);
        cell.setHasFlagella(true);
        cell.setHasNucleus(false);

        stats.atpGenerated = 40;
//        stats.gameTimer = 40f; // not tracking these floats for this test and tested elsewhere
//        stats.distanceMoved = 35f; // not tracking these floats for this test and tested elsewhere
        stats.glucoseCollected = 20;

        currentGamePlayScreen.getBasicZonePopup().setWasShown(true);
        currentGamePlayScreen.getAcidZonePopup().setWasShown(true);
        currentGamePlayScreen.getHealAvailablePopup().setWasShown(true);
        currentGamePlayScreen.getCellMembranePopup().setWasShown(true);
        currentGamePlayScreen.getSplitCellPopup().setWasShown(false);

        //Acquire Nuke
        gameRunner.step();
        cell.setHasNucleus(true);
        gameRunner.step();

        gameLoaderSaver.loadState(); // Load Game

        currentGamePlayScreen.getSplitCellPopup().setWasShown(false);


        //Verify Load

        assertEquals(100, cell.getCellHealth());
        assertEquals(100, cell.getCellATP());
        assertEquals(100f, cell.getCellSize());

        assertTrue(cell.hasSmallSizeUpgrade());
        assertTrue(cell.hasMediumSizeUpgrade());
        assertTrue(cell.hasLargeSizeUpgrade());
        assertTrue(cell.hasMassiveSizeUpgrade());

        assertTrue(cell.hasMitochondria());
        assertTrue(cell.hasRibosomes());
        assertTrue(cell.hasFlagella());
        assertTrue(cell.hasNucleus());

        assertEquals(40, stats.atpGenerated);
        assertEquals(20, stats.glucoseCollected);

        assertTrue(currentGamePlayScreen.getHealAvailablePopup().wasShown());
        assertTrue(currentGamePlayScreen.getCellMembranePopup().wasShown());
        assertTrue(currentGamePlayScreen.getAcidZonePopup().wasShown());
        assertTrue(currentGamePlayScreen.getBasicZonePopup().wasShown());
        assertFalse(currentGamePlayScreen.getSplitCellPopup().wasShown());

        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.U));
        gameRunner.runForSeconds(20);

        screen = gameRunner.game.getScreen();
        assertInstanceOf(GamePlayScreen.class, screen);

        gameLoaderSaver.loadState();
        gameRunner.step();
        assertTrue(cell.hasSplit());
    }

}
