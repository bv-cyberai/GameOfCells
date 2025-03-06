package cellcorp.gameofcells.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class TestHUD {

    @Test
    public void testUpdate() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
    
        // Mock the behavior for getting a font from the AssetManager
        BitmapFont mockFont = Mockito.mock(BitmapFont.class);
        Mockito.when(fakeAssetManager.isLoaded("rubik.fnt", BitmapFont.class)).thenReturn(true);
        Mockito.when(fakeAssetManager.get("rubik.fnt", BitmapFont.class)).thenReturn(mockFont);

        // Mock textures for rubik1.png and rubik2.png (even if not directly used in the test)
        Texture mockTexture1 = Mockito.mock(Texture.class);
        Texture mockTexture2 = Mockito.mock(Texture.class);
        Mockito.when(fakeAssetManager.get("rubik1.png", Texture.class)).thenReturn(mockTexture1);
        Mockito.when(fakeAssetManager.get("rubik2.png", Texture.class)).thenReturn(mockTexture2);

        HUD hud = new HUD(fakeAssetManager);
        hud.update(1f); // simulate 1 second has passed

        assertEquals("Timer: 1", hud.getTimerString());
        assertEquals("HEALTH: 100/100", hud.getCellHealthString());
        assertEquals("ATP: 0", hud.getAtpString());
    }

}
