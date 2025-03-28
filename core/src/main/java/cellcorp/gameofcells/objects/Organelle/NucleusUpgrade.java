package cellcorp.gameofcells.objects.Organelle;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.screens.OrganelleUpgradeScreen;

/**
 * Ribosome upgrade.
 */
public class NucleusUpgrade extends OrganelleUpgrade {
    public NucleusUpgrade() {
        super("Nucleus", 90, "Control center of the cell", "Unlocks advanced abilities", 90, 4);
    }

    /**
     * Check if the nucleus upgrade is already purchased.
     * @param cell
     */
    @Override
    protected boolean isAlreadyPurchased(Cell cell) {
        return cell.hasNucleus();
    }

    /**
     * Apply the upgrade's perks to the cell.
     * Unlock advanced abilities.
     * @param cell
     */
    @Override
    public void applyUpgrade(Cell cell) {
        cell.setHasNucleus(true);
        cell.removeCellATP(cost);
        // Unlock advanced abilities (e.g., allow the cell to split)
    }

    /**
     * Check if the previous upgrade is purchased.
     * Nucleus requires Flagella to be purchased.
     * @param organelleUpgradeScreen
     */
    @Override
    public boolean isPreviousUpgradePurchased(OrganelleUpgradeScreen organelleUpgradeScreen) {
        return organelleUpgradeScreen.hasFlagella();
    }
}