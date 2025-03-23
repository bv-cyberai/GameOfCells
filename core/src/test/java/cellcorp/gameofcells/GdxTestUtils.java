package cellcorp.gameofcells;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import org.mockito.Mockito;

public class GdxTestUtils {
    public static void mockGdxGraphics() {
        // Mock the Gdx.graphics object
        Graphics mockGraphics = Mockito.mock(Graphics.class);
        Mockito.when(mockGraphics.getWidth()).thenReturn(1920); // Default screen width
        Mockito.when(mockGraphics.getHeight()).thenReturn(1080); // Default screen height
        Gdx.graphics = mockGraphics;
    }
}