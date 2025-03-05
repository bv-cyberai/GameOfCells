package cellcorp.gameofcells.objects;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.assets.AssetManager;

public class TestGlucose {
        @Test
    public void constructingGlucoseDoesNotCrash() {
        // Make sure we can construct a Glucose object without crashing.
        // We have to mock the AssetManager, so that it doesn't try to load a file and crash.
        // Unlike the InputProvider, we don't need any additional behavior,
        // so we can just use `Mockito.mock`
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        new Glucose(fakeAssetManager);
    }
}
