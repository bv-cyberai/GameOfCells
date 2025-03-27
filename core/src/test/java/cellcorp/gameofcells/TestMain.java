package cellcorp.gameofcells;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Glucose;
import cellcorp.gameofcells.objects.GlucoseManager;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.runner.GameRunner;
import cellcorp.gameofcells.screens.GameOverScreen;
import cellcorp.gameofcells.screens.GamePlayScreen;
import cellcorp.gameofcells.screens.MainMenuScreen;
import cellcorp.gameofcells.screens.ShopScreen;

public class TestMain {

    @Test
    public void movingToGamePlayScreenSpawnsGlucose() {
        // Create a new `GameRunner`.
        // We use a static method instead of the constructor,
        // for reasons described in the method documentation.
        var gameRunner = GameRunner.create();

        // Hold down space and step forward a frame.
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        // Make sure we're on the gameplay screen, and that some glucose have spawned
        var screen = gameRunner.game.getScreen();
        assertInstanceOf(GamePlayScreen.class, screen);
        var gamePlayScreen = (GamePlayScreen) screen;
        var glucoseArray = gamePlayScreen.getGlucoseManager().getGlucoseArray();
        assertNotEquals(0, glucoseArray.size());

        // Let the game run for 2 seconds, with nothing held down.
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.runForSeconds(2);
        // Test that the game ran the expected number of ticks.
        assertEquals(1 + GameRunner.TICKS_PER_SECOND * 2, gameRunner.getTicksElapsed());
        // Test that there are still glucose on the screen.
        // `Main.screen`, `GamePlayScreen.glucoseManager`, and
        // `GlucoseManager.glucoseArray`
        // are _not_ final in their respective classes, so we should set them again,
        // to make sure we're not using an old reference that's been replaced in the
        // actual game.
        screen = gameRunner.game.getScreen();
        assertInstanceOf(GamePlayScreen.class, screen);
        gamePlayScreen = (GamePlayScreen) screen;
        glucoseArray = gamePlayScreen.getGlucoseManager().getGlucoseArray();
        assertNotEquals(0, glucoseArray.size());
    }

    @Test
    public void gameCreatedWithCorrectScreenDimensions() {
        // Games should be created with screen dimensions matching the default screen
        // dimensions
        var gameRunner = GameRunner.create();

        assertEquals(Main.DEFAULT_SCREEN_WIDTH, gameRunner.game.getGraphicsProvider().getWidth());
        assertEquals(Main.DEFAULT_SCREEN_HEIGHT, gameRunner.game.getGraphicsProvider().getHeight());
    }

    @Test
    public void canMoveToShopScreen() {
//        Gdx.files = Mockito.mock(Files.class);
//        Mockito.when(Gdx.files.internal(Mockito.anyString()))
//            .thenReturn(new FileHandle("config.txt"));
        var gameRunner = GameRunner.create();

        // Press enter to move to game screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        // Press 's' to move to shop screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.S));
        gameRunner.step();

        assertInstanceOf(ShopScreen.class, gameRunner.game.getScreen());
    }

    @Test
    // This will need to be changed eventually to account for g -> game over screen
    // likely being removed.
    public void canMoveToGameOverScreen() {
        var gameRunner = GameRunner.create();
//        Gdx.files = Mockito.mock(Files.class);
//        Mockito.when(Gdx.files.internal(Mockito.anyString()))
//            .thenReturn(new FileHandle("config.txt"));
        // Press space to move to game screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        // Press 'G' to move to shop screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.G));
        gameRunner.step();

        assertInstanceOf(GameOverScreen.class, gameRunner.game.getScreen());
    }

    @Test

    public void canMoveToGamePlayScreenFromGameOver() {
        var gameRunner = GameRunner.create();

        // Press space to move to game screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        // Press 'G' to move to shop screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.G));
        gameRunner.step();

        // Press 'R' to move to start new game may need to be changed if client prefers
        // it switch to main menu screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.R));
        gameRunner.step();

        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());
    }

    @Test
    public void gameStartsOnMainMenuScreen() {
        var gameRunner = GameRunner.create();
        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());
    }

    @Test
    public void timerCorrectAfterScreenSwitch() {
        var gameRunner = GameRunner.create();

        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.runForSeconds(2);

        gameRunner.setHeldDownKeys(Set.of(Input.Keys.S));
        gameRunner.step();
        gameRunner.runForSeconds(2);

        gameRunner.setHeldDownKeys(Set.of(Input.Keys.E));
        gameRunner.step();
        var screen = (GamePlayScreen) gameRunner.game.getScreen();
        String time = screen.getHud().getTimerString();
        assertEquals("Timer: 2", time);
    }

    @Test
    public void cellCollidesWithMultipleGlucoseRemovesAll() {
        // Create a game. Move to gameplay screen.
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        var screen = gameRunner.game.getScreen();
        assert screen instanceof GamePlayScreen;
        var gamePlayScreen = (GamePlayScreen) screen;
        gameRunner.step();

        // Spawn multiple glucose on top of the cell
        var cell = gamePlayScreen.getCell();

        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        Mockito.when(fakeConfigProvider.getIntValue("cellHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("cellATP")).thenReturn(30);
        Mockito.when(fakeConfigProvider.getIntValue("maxHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("maxATP")).thenReturn(100);

        var testCell = new Cell(gamePlayScreen,fakeAssetManager,fakeConfigProvider);
        System.out.println("TESTCELLSTART" + testCell.getCellATP());

        var startATP = cell.getCellATP();
        System.out.println("START ATP:" + startATP);
        var gameGlucose = gamePlayScreen.getGlucoseManager().getGlucoseArray();

        var addedGlucose = new ArrayList<Glucose>();
        for (int i = 0; i < 10; i++) {
            addedGlucose.add(new Glucose(
                    Mockito.mock(AssetManager.class),
                    cell.getX() + 100,
                    cell.getY() + 80,
                    GlucoseManager.RADIUS));
        }
        gameGlucose.addAll(addedGlucose);

        gameRunner.step();
        // Assert that all the glucose have been removed, and that the cell ATP has
        // increased 10 times.
        for (var glucose : addedGlucose) {
            assert !gameGlucose.contains(glucose);
        }
        System.out.println(cell.getCellATP());
        assertEquals(100, cell.getCellATP());
    }
}
