package cellcorp.gameofcells.objects.Organelle;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.screens.OrganelleUpgradeScreen;

/**
 * Mitochondria upgrade.
 */
public class MitochondriaUpgrade extends OrganelleUpgrade {
    public MitochondriaUpgrade() {
        super("Mitochondria", 30, "Powerhouse of the cell", "Increases ATP production", 30, 1);
    }

    @Override
    public void applyUpgrade(Cell cell) {
        cell.setHasMitochondria(true);
    }

    @Override
    protected boolean isPreviousUpgradePurchased(OrganelleUpgradeScreen organelleUpgradeScreen) {
        // Mitochondria is the first upgrade, so no previous upgrade it required.
        return true;
    }
}