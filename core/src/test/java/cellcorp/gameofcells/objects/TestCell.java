package cellcorp.gameofcells.objects;

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
        new Cell(fakeAssetManager);
    }
}
