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

    /**
     * Check if the mitochondria upgrade is already purchased.
     * @param cell
     */
    @Override
    protected boolean isAlreadyPurchased(Cell cell) {
        return cell.hasMitochondria();
    }

    /**
     * Apply the upgrade's perks to the cell.
     * @param cell
     */
    @Override
    public void applyUpgrade(Cell cell) {
        cell.setHasMitochondria(true);
        cell.removeCellATP(cost);
        // Increase ATP production
    }

    /**
     * Check if the previous upgrade is purchased.
     * Mitochondria is the first upgrade, so no previous upgrade it required.
     * @param organelleUpgradeScreen
     */
    @Override
    protected boolean isPreviousUpgradePurchased(OrganelleUpgradeScreen organelleUpgradeScreen) {
        return true;
    }
}