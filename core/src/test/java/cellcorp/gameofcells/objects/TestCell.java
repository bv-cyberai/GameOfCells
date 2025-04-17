package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.TestMain;
import cellcorp.gameofcells.runner.GameRunner;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Files;

import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestCell {

    @BeforeAll
    public static void setup() {
        TestMain.setUpLibGDX();
        Gdx.files = Mockito.mock(Files.class);
        Mockito.when(Gdx.files.internal(Mockito.anyString()))
            .thenReturn(Mockito.mock(FileHandle.class));
        Mockito.when(Gdx.files.internal(Mockito.anyString()).readString())
            .thenReturn("cellHealth:100\ncellATP:30\nmaxHealth:100\nmaxATP:100\n[descriptions]/");
    }

    @Test
    public void constructingCellDoesNotCrash() {
        // Make sure we can construct a Cell without crashing.
        // We have to mock the AssetManager, so that it doesn't try to load a file and crash.
        // Unlike the InputProvider, we don't need any additional behavior,
        // so we can just use `Mockito.mock`
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        var fakeGamePlayScreen = Mockito.mock(GamePlayScreen.class);
        new Cell(fakeGamePlayScreen, fakeAssetManager, fakeConfigProvider);
    }

    @Test
    public void damagingCellToBelowZeroHealthEntersDeathState() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var gamePlayScreen = Mockito.mock(GamePlayScreen.class);
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        Mockito.when(fakeConfigProvider.getIntValue("cellHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("cellATP")).thenReturn(30);
        Mockito.when(fakeConfigProvider.getIntValue("maxHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("maxATP")).thenReturn(100);
        var cell = new Cell(gamePlayScreen, fakeAssetManager, fakeConfigProvider);
        cell.applyDamage(Cell.MAX_HEALTH + 1);
        assertEquals(0, cell.getCellHealth());
        Mockito.verify(gamePlayScreen, Mockito.never()).endGame();
    }

    @Test
    public void cellDeathAnimationDelaysGameOver() {
        var runner = GameRunner.create();
        runner.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();
        var gamePlay = (GamePlayScreen) runner.game.getScreen();
        var cell = gamePlay.getCell();

        cell.setCellHealth(1);
        cell.applyDamage(5); // Trigger death

        // During animation, game should not have switched yet
        assertSame(gamePlay, runner.game.getScreen());

        // Let animation finish
        runner.runForSeconds(2.6f);

        // After delay, GameOverScreen should be active
        assertNotSame(gamePlay, runner.game.getScreen());
    }

    @Test
    public void screenShakesOnCellDeath() throws Exception {
        var runner = GameRunner.create();
        runner.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();
        var gamePlay = (GamePlayScreen) runner.game.getScreen();
        var cell = gamePlay.getCell();

        cell.setCellHealth(1);
        cell.applyDamage(5); // Trigger death

        // Let it run 1 frame
        runner.step();

        // shakeTime should be greater than 0
        var shakeTimeField = GamePlayScreen.class.getDeclaredField("shakeTime");
        shakeTimeField.setAccessible(true);
        float shakeTime = (float) shakeTimeField.get(gamePlay);

        assertTrue(shakeTime > 0f);
    }

    @Test
    public void cameraZoomsDuringCellDeath() throws Exception {
        var runner = GameRunner.create();
        runner.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();
        var gamePlay = (GamePlayScreen) runner.game.getScreen();
        var cell = gamePlay.getCell();

        cell.setCellHealth(1);
        cell.applyDamage(5); // Trigger death

        runner.runForSeconds(0.5f); // Halfway into the animation

        // Pull viewport size to verify zoom applied
        var viewport = gamePlay.getViewport();
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Should be less than the default 1920x1080 due to zoom-in
        assertTrue(worldWidth < GamePlayScreen.VIEW_RECT_WIDTH);
        assertTrue(worldHeight < GamePlayScreen.VIEW_RECT_HEIGHT);
    }

    @Test
    public void gameRemainsOnGamePlayScreenDuringDeathAnimation() {
        var runner = GameRunner.create();
        runner.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();
        var gamePlay = (GamePlayScreen) runner.game.getScreen();
        var cell = gamePlay.getCell();

        cell.setCellHealth(1);
        cell.applyDamage(10);
        runner.runForSeconds(0.5f); // Not enough to finish animation

        assertSame(gamePlay, runner.game.getScreen());
    }

    @Test
    public void cellHealthDoesNotGoBelowZero() {
        var runner = GameRunner.create();
        runner.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();
        var gamePlay = (GamePlayScreen) runner.game.getScreen();
        var cell = gamePlay.getCell();

        cell.setCellHealth(5);
        cell.applyDamage(10); // Exceeds health

        assertEquals(0, cell.getCellHealth());
    }

    @Test
    public void applyingDamageDuringDeathDoesNothing() {
        var runner = GameRunner.create();
        runner.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();
        var gamePlay = (GamePlayScreen) runner.game.getScreen();
        var cell = gamePlay.getCell();

        cell.setCellHealth(5);
        cell.applyDamage(10); // Initiate death

        int afterDeathHealth = cell.getCellHealth();
        cell.applyDamage(50); // Should be ignored
        assertEquals(afterDeathHealth, cell.getCellHealth(), "Health should not change after death starts");
    }

    @Test
    public void cellTakesDamagePerSecondAtZeroATP() {
        var runner = GameRunner.create();
        runner.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();
        assertInstanceOf(GamePlayScreen.class, runner.game.getScreen());
        var gamePlay = (GamePlayScreen) runner.game.getScreen();

        var cell = gamePlay.getCell();
        var startHealth = cell.getCellHealth();
        gamePlay.getCell().setCellATP(0);
        runner.runForSeconds(1.1f);
        assertEquals(startHealth - Cell.ZERO_ATP_DAMAGE_PER_SECOND, cell.getCellHealth());
        runner.runForSeconds(1);
        assertEquals(startHealth - 2 * Cell.ZERO_ATP_DAMAGE_PER_SECOND, cell.getCellHealth());
    }

    @Test
    public void testSizeAndOrganelleUpgradeLimits() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var gamePlayScreen = Mockito.mock(GamePlayScreen.class);
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        var cell = new Cell(gamePlayScreen, fakeAssetManager, fakeConfigProvider);

        assertEquals(0, cell.getSizeUpgradeLevel());
        assertEquals(0, cell.getOrganelleUpgradeLevel());

        cell.setHasSmallSizeUpgrade(true);
        cell.setHasSmallSizeUpgrade(false);
        cell.setHasMitochondria(true);
        cell.setHasMitochondria(false);
        assertEquals(1, cell.getSizeUpgradeLevel());
        assertEquals(1, cell.getOrganelleUpgradeLevel());

        cell.setHasMediumSizeUpgrade(true);
        cell.setHasMediumSizeUpgrade(false);
        cell.setHasRibosomes(true);
        cell.setHasRibosomes(false);
        assertEquals(2, cell.getSizeUpgradeLevel());
        assertEquals(2, cell.getOrganelleUpgradeLevel());

        cell.setHasLargeSizeUpgrade(true);
        cell.setHasLargeSizeUpgrade(false);
        cell.setHasFlagella(true);
        cell.setHasFlagella(false);
        assertEquals(3, cell.getSizeUpgradeLevel());
        assertEquals(3, cell.getOrganelleUpgradeLevel());

        cell.setHasMassiveSizeUpgrade(true);
        cell.setHasMassiveSizeUpgrade(false);
        cell.setHasNucleus(true);
        cell.setHasNucleus(false);
        assertEquals(4, cell.getSizeUpgradeLevel());
        assertEquals(4, cell.getOrganelleUpgradeLevel());
    }
}
