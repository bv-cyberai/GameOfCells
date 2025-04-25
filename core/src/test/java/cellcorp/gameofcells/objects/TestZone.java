package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestZone {

    private AssetManager mockAssetManager;
    private Texture mockTexture;
    private Zone zone;

    @BeforeEach
    public void setup() {
        mockAssetManager = mock(AssetManager.class);
        mockTexture = mock(Texture.class);
        when(mockAssetManager.get("zone.png", Texture.class)).thenReturn(mockTexture);

        zone = new Zone(mockAssetManager, "zone.png", 100f, 200f);
    }

    @Test
    public void testXAndYReturnCorrectValues() {
        assertEquals(100f, zone.x());
        assertEquals(200f, zone.y());
    }

    @Test
    public void testDrawWithSpriteBatch() {
        SpriteBatch mockBatch = mock(SpriteBatch.class);

        assertDoesNotThrow(() -> zone.draw(mockBatch));
        verify(mockAssetManager).get("zone.png", Texture.class);
        verify(mockBatch).draw(eq(mockTexture), anyFloat(), anyFloat(), anyFloat(), anyFloat());
    }

    @Test
    public void testDistanceFromPoint() {
        double d = zone.distanceFrom(100f, 250f);
        assertEquals(50.0, d, 0.001);
    }

    @Test
    public void testEqualsAndHashCode() {
        Zone same = new Zone(mockAssetManager, "zone.png", 100f, 200f);
        Zone different = new Zone(mockAssetManager, "zone.png", 100f, 201f);

        assertEquals(zone, same);
        assertNotEquals(zone, different);
        assertEquals(zone.hashCode(), same.hashCode());
    }
}
