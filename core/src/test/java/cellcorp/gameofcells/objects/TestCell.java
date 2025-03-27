package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.assets.AssetManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;

public class TestCell {
    @Test
    public void constructingCellDoesNotCrash() {
        // Make sure we can construct a Cell without crashing.
        // We have to mock the AssetManager, so that it doesn't try to load a file and crash.
        // Unlike the InputProvider, we don't need any additional behavior,
        // so we can just use `Mockito.mock`
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var fakeGamePlayScreen = Mockito.mock(GamePlayScreen.class);
        new Cell(fakeGamePlayScreen, fakeAssetManager);
    }

    @Test
    public void damagingCellToBelowZeroHealthEndsGame() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var gamePlayScreen= Mockito.mock(GamePlayScreen.class);
        var cell = new Cell(gamePlayScreen, fakeAssetManager);
        cell.applyDamage(Cell.MAX_HEALTH + 1);
        assertTrue(cell.getCellHealth() > 0);
        Mockito.verify(gamePlayScreen, Mockito.atLeastOnce()).endGame();
    }
}
