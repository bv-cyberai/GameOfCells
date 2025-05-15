package cellcorp.gameofcells.providers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestAssetManager {
    @Test
    public void testLoadNonExistentAsset() {
        // Loading a non-existent asset should crash.
        var assetManager = new AssetManager();

        assertThrows(
            RuntimeException.class,
            () -> {
                assetManager.load("DOES_NOT_EXIST.png", Texture.class);
                assetManager.finishLoading();
            }
        );
    }
}
