package cellcorp.gameofcells.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.assets.AssetManager;

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
        new Glucose(fakeAssetManager, 10, 10, 20);
    }

    @Test
    public void constructingGlucoseGeneratorDoesNotCrash() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var cell = new Cell(fakeAssetManager);
        // Pass arbitrary values for cellX and cellY
        new GlucoseManager(fakeAssetManager, cell);
    }

    @Test
    public void glucoseManagerFillsArray() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var cell = new Cell(fakeAssetManager);
        // Again pass arbitrary values for cellX and cellY
        var testManager = new GlucoseManager(fakeAssetManager, cell);

        int testMaxGlucose = testManager.getMAX_GLUCOSE();

        assertEquals(testMaxGlucose, testManager.getGlucoseArray().size());

    }

    @Test
    public void glucoseStoresCoordinatesAndRadiusCorrectly() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);

        Glucose glucose = new Glucose(fakeAssetManager, 100, 200, 30);

        assertEquals(100, glucose.getX());
        assertEquals(200, glucose.getY());
        assertEquals(30, glucose.getRadius());
    }
}
