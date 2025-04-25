package cellcorp.gameofcells.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestUpgrade {

    @Test
    public void testUpgradeInterfaceMethodsCanBeCalled() {
        Upgrade mockUpgrade = mock(Upgrade.class);

        when(mockUpgrade.getName()).thenReturn("ATP Boost");
        when(mockUpgrade.getDescription()).thenReturn("Increases ATP regen rate");
        when(mockUpgrade.getPerks()).thenReturn("+5 ATP/sec");
        when(mockUpgrade.getRequiredATP()).thenReturn(100);
        when(mockUpgrade.getRequiredSize()).thenReturn(2);

        Cell mockCell = mock(Cell.class);
        when(mockUpgrade.canPurchase(mockCell)).thenReturn(true);

        assertEquals("ATP Boost", mockUpgrade.getName());
        assertEquals("Increases ATP regen rate", mockUpgrade.getDescription());
        assertEquals("+5 ATP/sec", mockUpgrade.getPerks());
        assertEquals(100, mockUpgrade.getRequiredATP());
        assertEquals(2, mockUpgrade.getRequiredSize());
        assertTrue(mockUpgrade.canPurchase(mockCell));

        // No exception on applyUpgrade
        assertDoesNotThrow(() -> mockUpgrade.applyUpgrade(mockCell));
    }
}
