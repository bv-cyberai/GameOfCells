package cellcorp.gameofcells.providers;

import cellcorp.gameofcells.Main;
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
        when(mockPrefs.getBoolean("smallSize")).thenReturn(true);
        when(mockPrefs.getBoolean("mito")).thenReturn(true);
        when(mockPrefs.getInteger("cellHealth")).thenReturn(50);
        when(mockPrefs.getInteger("cellATP")).thenReturn(60);
        when(mockPrefs.getFloat("cellSize")).thenReturn(200f);
        gameLoaderSaver.setUpMockPrefrences(mockPrefs);

        var cell = currentGamePlayScreen.getCell();

        //Set up a cell to save
        cell.setHasSmallSizeUpgrade(true);
        cell.setHasMitochondria(true);

        cell.setCellHealth(50);
        cell.setCellATP(60);

        //Save
//        gameLoaderSaver.saveState();

        //Reset Values
        cell.setHasSmallSizeUpgrade(false);
        cell.setHasMitochondria(false);

        cell.setCellHealth(100);
        cell.setCellATP(100);

        //Load
        gameLoaderSaver.loadState();

        assertTrue(cell.hasSmallSizeUpgrade());
        assertTrue(cell.hasMitochondria());
        assertEquals(50, cell.getCellHealth());
        assertEquals(60, cell.getCellATP());
        assertEquals(200f,cell.getCellSize());



    }
}
