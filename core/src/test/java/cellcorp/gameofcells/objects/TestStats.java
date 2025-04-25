package cellcorp.gameofcells.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestStats {

    private Stats stats;

    @BeforeEach
    public void setup() {
        stats = new Stats();
    }

    @Test
    public void testInitialSizeIsZero() {
        assertEquals(0, stats.getSize());
        assertEquals("Tiny", stats.sizeDescription());
    }

    @Test
    public void testSetSizeIncreasesMaxSize() {
        stats.setSize(2);
        assertEquals(2, stats.getSize());

        stats.setSize(3);
        assertEquals(3, stats.getSize());
    }

    @Test
    public void testSetSizeDoesNotDecreaseMaxSize() {
        stats.setSize(3);
        stats.setSize(1); // shouldn't override

        assertEquals(3, stats.getSize());
    }

    @Test
    public void testSizeDescriptionMappings() {
        stats.setSize(0);
        assertEquals("Tiny", stats.sizeDescription());

        stats.setSize(1);
        assertEquals("Small", stats.sizeDescription());

        stats.setSize(2);
        assertEquals("Medium", stats.sizeDescription());

        stats.setSize(3);
        assertEquals("Large", stats.sizeDescription());

        stats.setSize(99);
        assertEquals("Massive", stats.sizeDescription());
    }

    @Test
    public void testFieldAccessAndDefaults() {
        assertEquals(0f, stats.gameTimer);
        assertEquals(0, stats.glucoseCollected);
        assertEquals(0, stats.atpGenerated);
        assertEquals(0f, stats.distanceMoved);
        assertEquals(0, stats.organellesPurchased);

        stats.gameTimer = 5.5f;
        stats.glucoseCollected = 10;
        stats.atpGenerated = 20;
        stats.distanceMoved = 42.42f;
        stats.organellesPurchased = 3;

        assertEquals(5.5f, stats.gameTimer);
        assertEquals(10, stats.glucoseCollected);
        assertEquals(20, stats.atpGenerated);
        assertEquals(42.42f, stats.distanceMoved);
        assertEquals(3, stats.organellesPurchased);
    }
}
