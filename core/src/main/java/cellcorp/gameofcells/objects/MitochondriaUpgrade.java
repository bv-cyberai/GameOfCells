package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.OrganelleUpgradeScreen;

/**
 * Mitochondria upgrade.
 */
public class MitochondriaUpgrade extends Upgrade {
    public MitochondriaUpgrade() {
        super("Mitochondria", 30, "Powerhouse of the cell\nIncreases ATP production", 30, 1);
    }

    @Override
    public void applyUpgrade(Cell cell) {
        cell.setHasMitochondria(true);
    }

    @Override
    public boolean isPreviousUpgradePurchased(OrganelleUpgradeScreen organelleUpgradeScreen) {
        // Mitochondria is the first upgrade, so no previous upgrade it required.
        return true;
    }
}