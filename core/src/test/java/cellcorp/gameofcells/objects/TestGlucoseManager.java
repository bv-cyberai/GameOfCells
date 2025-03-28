package cellcorp.gameofcells.objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.assets.AssetManager;

import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;

/**
 * Glucose Testing
 * 
 * Manages testing for glucose manager and glucose objects.
 */
public class TestGlucoseManager {

    @Test
    public void constructingGlucoseDoesNotCrash() {
        // Make sure we can construct a Glucose object without crashing.
        // We have to mock the AssetManager, so that it doesn't try to load a file and
        // crash.
        // Unlike the InputProvider, we don't need any additional behavior,
        // so we can just use `Mockito.mock`
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        new Glucose(fakeAssetManager, 10, 10);
    }

    @Test
    public void constructingGlucoseGeneratorDoesNotCrash() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var fakeGamePlayScreen = Mockito.mock(GamePlayScreen.class);
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        var cell = new Cell(fakeGamePlayScreen, fakeAssetManager,fakeConfigProvider);
        var zoneManager = new ZoneManager(fakeAssetManager, cell);
        // Pass arbitrary values for cellX and cellY
        new GlucoseManager(fakeAssetManager, fakeGamePlayScreen, zoneManager, cell);
    }

// DEPRECATED
// Max glucose no longer exists
//    @Test
//    public void glucoseManagerFillsArray() {
//        var fakeAssetManager = Mockito.mock(AssetManager.class);
//        var fakeGamePlayScreen = Mockito.mock(GamePlayScreen.class);
//        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        var cell = new Cell(fakeGamePlayScreen, fakeAssetManager,fakeConfigProvider);
//        // Again pass arbitrary values for cellX and cellY
//        var testManager = new GlucoseManager(fakeAssetManager, cell);
//
//        int testMaxGlucose = testManager.getMAX_GLUCOSE();
//
//        assertEquals(testMaxGlucose, testManager.getGlucoseArray().size());
//    }

    @Test
    public void glucoseStoresCoordinatesAndRadiusCorrectly() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);

        Glucose glucose = new Glucose(fakeAssetManager, 100, 200);

        assertEquals(100, glucose.getX());
        assertEquals(200, glucose.getY());
        assertEquals(30, glucose.getRadius());
    }
}
