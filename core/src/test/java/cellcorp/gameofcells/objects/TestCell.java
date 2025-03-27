package cellcorp.gameofcells.objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.assets.AssetManager;

import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;

public class TestCell {
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
        var gamePlayScreen= Mockito.mock(GamePlayScreen.class);
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        Mockito.when(fakeConfigProvider.getIntValue("cellHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("cellATP")).thenReturn(30);
        Mockito.when(fakeConfigProvider.getIntValue("maxHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("maxATP")).thenReturn(100);
        var cell = new Cell(gamePlayScreen, fakeAssetManager,fakeConfigProvider);
        cell.applyDamage(Cell.MAX_HEALTH + 1);
        assertTrue(cell.getCellHealth() > 0);
        Mockito.verify(gamePlayScreen, Mockito.atLeastOnce()).endGame();
    }
}
