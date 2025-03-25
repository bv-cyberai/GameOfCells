package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.providers.ConfigProvider;
import com.badlogic.gdx.assets.AssetManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class TestCell {
    @Test
    public void constructingCellDoesNotCrash() {
        // Make sure we can construct a Cell without crashing.
        // We have to mock the AssetManager, so that it doesn't try to load a file and crash.
        // Unlike the InputProvider, we don't need any additional behavior,
        // so we can just use `Mockito.mock`
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        new Cell(fakeAssetManager, fakeConfigProvider);
    }
}
