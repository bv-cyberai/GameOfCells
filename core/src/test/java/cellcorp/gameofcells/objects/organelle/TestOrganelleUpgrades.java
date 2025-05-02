package cellcorp.gameofcells.objects.organelle;

import cellcorp.gameofcells.objects.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestOrganelleUpgrades {

    private Cell mockCell;

    @BeforeEach
    public void setup() {
        mockCell = mock(Cell.class);
    }

    @Test
    public void testMitochondriaUpgrade() {
        OrganelleUpgrade upgrade = new MitochondriaUpgrade();
        when(mockCell.getCellATP()).thenReturn(100);
        when(mockCell.getSizeUpgradeLevel()).thenReturn(1); // size unit = 1
        when(mockCell.hasMitochondria()).thenReturn(false);

        assertTrue(upgrade.canPurchase(mockCell));
        upgrade.applyUpgrade(mockCell);

        verify(mockCell).setHasMitochondria(true);
        verify(mockCell).removeCellATP(30);
    }

    @Test
    public void testRibosomeUpgradeFailsWithoutMitochondria() {
        OrganelleUpgrade upgrade = new RibosomeUpgrade();
        when(mockCell.getCellATP()).thenReturn(100);
        when(mockCell.getSizeUpgradeLevel()).thenReturn(2); // size unit = 2
        when(mockCell.hasMitochondria()).thenReturn(false);

        assertFalse(upgrade.canPurchase(mockCell));
    }

    @Test
    public void testRibosomeUpgradeSuccess() {
        OrganelleUpgrade upgrade = new RibosomeUpgrade();
        when(mockCell.getCellATP()).thenReturn(100);
        when(mockCell.getSizeUpgradeLevel()).thenReturn(2); // size unit = 2
        when(mockCell.hasMitochondria()).thenReturn(true);
        when(mockCell.hasRibosomes()).thenReturn(false);

        assertTrue(upgrade.canPurchase(mockCell));
        upgrade.applyUpgrade(mockCell);

        verify(mockCell).setHasRibosomes(true);
        verify(mockCell).removeCellATP(50);
    }

    @Test
    public void testFlagellaUpgradeFailsWithoutRibosomes() {
        OrganelleUpgrade upgrade = new FlagellaUpgrade();
        when(mockCell.getCellATP()).thenReturn(100);
        when(mockCell.getSizeUpgradeLevel()).thenReturn(4); // size unit = 3
        when(mockCell.hasRibosomes()).thenReturn(false);

        assertFalse(upgrade.canPurchase(mockCell));
    }

    @Test
    public void testFlagellaUpgradeSuccess() {
        OrganelleUpgrade upgrade = new FlagellaUpgrade();
        when(mockCell.getCellATP()).thenReturn(100);
        when(mockCell.getSizeUpgradeLevel()).thenReturn(3); // size unit = 3
        when(mockCell.hasRibosomes()).thenReturn(true);
        when(mockCell.hasFlagella()).thenReturn(false);

        assertTrue(upgrade.canPurchase(mockCell));
        upgrade.applyUpgrade(mockCell);

        verify(mockCell).setHasFlagella(true);
        verify(mockCell).removeCellATP(70);
    }

    @Test
    public void testNucleusUpgradeFailsWithoutFlagella() {
        OrganelleUpgrade upgrade = new NucleusUpgrade();
        when(mockCell.getCellATP()).thenReturn(100);
        when(mockCell.getSizeUpgradeLevel()).thenReturn(4); // size unit = 4
        when(mockCell.hasFlagella()).thenReturn(false);

        assertFalse(upgrade.canPurchase(mockCell));
    }

    @Test
    public void testNucleusUpgradeSuccess() {
        OrganelleUpgrade upgrade = new NucleusUpgrade();
        when(mockCell.getCellATP()).thenReturn(150);
        when(mockCell.getSizeUpgradeLevel()).thenReturn(4); // size unit = 4
        when(mockCell.hasFlagella()).thenReturn(true);
        when(mockCell.hasNucleus()).thenReturn(false);

        assertTrue(upgrade.canPurchase(mockCell));
        upgrade.applyUpgrade(mockCell);

        verify(mockCell).setHasNucleus(true);
        verify(mockCell).removeCellATP(90);
    }

    @Test
    public void testDescriptionsAndPerks() {
        OrganelleUpgrade mito = new MitochondriaUpgrade();
        OrganelleUpgrade ribo = new RibosomeUpgrade();
        OrganelleUpgrade flag = new FlagellaUpgrade();
        OrganelleUpgrade nuc = new NucleusUpgrade();

        assertEquals("Mitochondria", mito.getName());
        assertEquals("Ribosome", ribo.getName());
        assertEquals("Flagellum", flag.getName());
        assertEquals("Nucleus", nuc.getName());

        assertEquals("Increases ATP production", mito.getPerks());
        assertEquals("Increases protein synthesis", ribo.getPerks());
        assertEquals("Increases movement speed", flag.getPerks());
        assertEquals("Unlocks advanced abilities", nuc.getPerks());
    }
}
