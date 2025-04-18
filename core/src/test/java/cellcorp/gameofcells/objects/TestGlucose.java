package cellcorp.gameofcells.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TestGlucose {

    private AssetManager mockAssetManager;
    private Glucose glucose;

    @BeforeEach
    public void setup() {
        mockAssetManager = mock(AssetManager.class);

        // Position: (100, 200)
        glucose = new Glucose(mockAssetManager, 100f, 200f);
    }

    @Test
    public void testGetXReturnsCorrectValue() {
        assertEquals(100f, glucose.getX(), 0.001);
    }

    @Test
    public void testGetYReturnsCorrectValue() {
        assertEquals(200f, glucose.getY(), 0.001);
    }

    @Test
    public void testGetRadiusReturnsCorrectValue() {
        assertEquals(Glucose.RADIUS, glucose.getRadius());
    }

    @Test
    public void testGetCircleReturnsCorrectCircle() {
        Circle circle = glucose.getCircle();
        assertNotNull(circle);
        assertEquals(100f, circle.x, 0.001);
        assertEquals(200f, circle.y, 0.001);
        assertEquals(Glucose.RADIUS, circle.radius);
    }

    @Test
    public void testDrawWithSpriteBatch() {
        Texture mockTexture = mock(Texture.class);
        when(mockAssetManager.get("glucose_orange.png", Texture.class)).thenReturn(mockTexture);

        SpriteBatch mockBatch = mock(SpriteBatch.class);
        assertDoesNotThrow(() -> glucose.draw(mockBatch));

        // Optionally verify draw called
        verify(mockBatch).draw(eq(mockTexture), anyFloat(), anyFloat(), anyFloat(), anyFloat());
    }

    @Test
    public void testDrawWithShapeRendererWhenDebugDisabled() {
        // Turn off debug draw
        ShapeRenderer mockRenderer = mock(ShapeRenderer.class);

        // Simulate behavior when DEBUG_DRAW_ENABLED is false
        if (!cellcorp.gameofcells.screens.GamePlayScreen.DEBUG_DRAW_ENABLED) {
            assertDoesNotThrow(() -> glucose.draw(mockRenderer));

            // Make sure draw was NOT called
            verify(mockRenderer, never()).circle(anyFloat(), anyFloat(), anyFloat());
        }
    }

    @Test
    public void testSetAtpPerGlucoseToZero() {
        Glucose.setAtpPerGlucoseDoNotUseForTestingOnly(true);
        assertEquals(0, Glucose.ATP_PER_GLUCOSE);

        Glucose.setAtpPerGlucoseDoNotUseForTestingOnly(false);
        assertEquals(20, Glucose.ATP_PER_GLUCOSE);
    }
}
