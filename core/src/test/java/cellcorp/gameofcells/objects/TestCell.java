package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.TestMain;
import cellcorp.gameofcells.runner.GameRunner;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
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
    public void damagingCellToBelowZeroHealthEndsGame() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var gamePlayScreen = Mockito.mock(GamePlayScreen.class);
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        Mockito.when(fakeConfigProvider.getIntValue("cellHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("cellATP")).thenReturn(30);
        Mockito.when(fakeConfigProvider.getIntValue("maxHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("maxATP")).thenReturn(100);
        var cell = new Cell(gamePlayScreen, fakeAssetManager, fakeConfigProvider);
        cell.applyDamage(Cell.MAX_HEALTH + 1);
        assertTrue(cell.getCellHealth() > 0);
        Mockito.verify(gamePlayScreen, Mockito.atLeastOnce()).endGame();
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

        cell.setSmallSizeUpgrade(true);
        cell.setSmallSizeUpgrade(false);
        cell.setHasMitochondria(true);
        cell.setHasMitochondria(false);
        assertEquals(1, cell.getSizeUpgradeLevel());
        assertEquals(1, cell.getOrganelleUpgradeLevel());

        cell.setMediumSizeUpgrade(true);
        cell.setMediumSizeUpgrade(false);
        cell.setHasRibosomes(true);
        cell.setHasRibosomes(false);
        assertEquals(2, cell.getSizeUpgradeLevel());
        assertEquals(2, cell.getOrganelleUpgradeLevel());

        cell.setLargeSizeUpgrade(true);
        cell.setLargeSizeUpgrade(false);
        cell.setHasFlagella(true);
        cell.setHasFlagella(false);
        assertEquals(3, cell.getSizeUpgradeLevel());
        assertEquals(3, cell.getOrganelleUpgradeLevel());

        cell.setMassiveSizeUpgrade(true);
        cell.setMassiveSizeUpgrade(false);
        cell.setHasNucleus(true);
        cell.setHasNucleus(false);
        assertEquals(4, cell.getSizeUpgradeLevel());
        assertEquals(4, cell.getOrganelleUpgradeLevel());
    }

    @Test
    public void testFlagellaSpeedChange() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var gamePlayScreen = Mockito.mock(GamePlayScreen.class);
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        var cell = new Cell(gamePlayScreen, fakeAssetManager, fakeConfigProvider);
        float startspeed = cell.getCellSpeed();
        cell.setSmallSizeUpgrade(true);
        cell.setHasMitochondria(true);

        cell.setMediumSizeUpgrade(true);
        cell.setHasRibosomes(true);

        cell.setLargeSizeUpgrade(true);
        cell.setHasFlagella(true);

        float speedPostUpgrade = cell.getCellSpeed();

        //This is somewhat ambiguous but I left it as such in case we decide
        //to change the speed upgrade amount.
        //This is also tested in a different manner in testMain.
        assertTrue(speedPostUpgrade > startspeed);
    }
}
