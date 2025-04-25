package cellcorp.gameofcells.objects.size;

import cellcorp.gameofcells.objects.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestSizeUpgrades {

    private Cell mockCell;

    @BeforeEach
    public void setup() {
        mockCell = mock(Cell.class);
    }

    @Test
    public void testSmallSizeUpgradePurchaseFlow() {
        SizeUpgrade upgrade = new SmallSizeUpgrade();

        when(mockCell.getCellSize()).thenReturn(100); // base size
        when(mockCell.getCellATP()).thenReturn(100);
        when(mockCell.hasSmallSizeUpgrade()).thenReturn(false);

        assertTrue(upgrade.canPurchase(mockCell));
        upgrade.applyUpgrade(mockCell);

        verify(mockCell).increasecellSize(100);
        verify(mockCell).setHasSmallSizeUpgrade(true);
        verify(mockCell).removeCellATP(50);
    }

    @Test
    public void testMediumSizeUpgradeBlockedIfPreviousNotPurchased() {
        SizeUpgrade upgrade = new MediumSizeUpgrade();

        when(mockCell.getCellSize()).thenReturn(200); // size = 1 unit
        when(mockCell.getCellATP()).thenReturn(100);
        when(mockCell.hasSmallSizeUpgrade()).thenReturn(false); // 🔥 should block
        when(mockCell.hasMediumSizeUpgrade()).thenReturn(false);

        assertFalse(upgrade.canPurchase(mockCell));
    }

    @Test
    public void testMediumSizeUpgradeAllowedIfPreviousPurchased() {
        SizeUpgrade upgrade = new MediumSizeUpgrade();

        when(mockCell.getCellSize()).thenReturn(200); // size = 1 unit
        when(mockCell.getCellATP()).thenReturn(100);
        when(mockCell.hasSmallSizeUpgrade()).thenReturn(true);
        when(mockCell.hasMediumSizeUpgrade()).thenReturn(false);

        assertTrue(upgrade.canPurchase(mockCell));
        upgrade.applyUpgrade(mockCell);

        verify(mockCell).increasecellSize(100);
        verify(mockCell).setHasMediumSizeUpgrade(true);
        verify(mockCell).removeCellATP(65);
    }

    @Test
    public void testLargeSizeUpgrade() {
        SizeUpgrade upgrade = new LargeSizeUpgrade();

        when(mockCell.getCellSize()).thenReturn(300); // size = 2 units
        when(mockCell.getCellATP()).thenReturn(200);
        when(mockCell.hasMediumSizeUpgrade()).thenReturn(true);
        when(mockCell.hasLargeSizeUpgrade()).thenReturn(false);

        assertTrue(upgrade.canPurchase(mockCell));
        upgrade.applyUpgrade(mockCell);

        verify(mockCell).increasecellSize(100);
        verify(mockCell).setHasLargeSizeUpgrade(true);
        verify(mockCell).removeCellATP(85);
    }

    @Test
    public void testMassiveSizeUpgrade() {
        SizeUpgrade upgrade = new MassiveSizeUpgrade();

        when(mockCell.getCellSize()).thenReturn(400); // size = 3 units
        when(mockCell.getCellATP()).thenReturn(200);
        when(mockCell.hasLargeSizeUpgrade()).thenReturn(true);
        when(mockCell.hasMassiveSizeUpgrade()).thenReturn(false);

        assertTrue(upgrade.canPurchase(mockCell));
        upgrade.applyUpgrade(mockCell);

        verify(mockCell).increasecellSize(100);
        verify(mockCell).setHasMassiveSizeUpgrade(true);
        verify(mockCell).removeCellATP(100);
    }

    @Test
    public void testUpgradeDescriptionsAndPerks() {
        SizeUpgrade small = new SmallSizeUpgrade();
        SizeUpgrade medium = new MediumSizeUpgrade();
        SizeUpgrade large = new LargeSizeUpgrade();
        SizeUpgrade massive = new MassiveSizeUpgrade();

        assertTrue(small.getPerks().contains(small.getVisualEffect()));
        assertTrue(medium.getPerks().contains(medium.getVisualEffect()));
        assertTrue(large.getPerks().contains(large.getVisualEffect()));
        assertTrue(massive.getPerks().contains(massive.getVisualEffect()));

        assertEquals("Small Size Upgrade", small.getName());
        assertEquals("Increases cell size by 1 units", small.getDescription());
    }

    @Test
    public void testCannotRepurchaseUpgrade() {
        SizeUpgrade medium = new MediumSizeUpgrade();

        when(mockCell.getCellSize()).thenReturn(300); // size = 2 units
        when(mockCell.getCellATP()).thenReturn(100);
        when(mockCell.hasSmallSizeUpgrade()).thenReturn(true);
        when(mockCell.hasMediumSizeUpgrade()).thenReturn(true); // already bought

        assertFalse(medium.canPurchase(mockCell));
    }
}
