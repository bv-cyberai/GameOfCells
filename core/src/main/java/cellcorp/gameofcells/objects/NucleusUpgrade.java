package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.OrganelleUpgradeScreen;

/**
 * Ribosome upgrade.
 */
public class NucleusUpgrade extends Upgrade {
    public NucleusUpgrade() {
        super("Nucleus", 90, "Control center of the cell", "Unlocks advanced abilities", 90, 4);
    }

    @Override
    public void applyUpgrade(Cell cell) {
        // Unlock advanced abilities (e.g., allow the cell to split)
        cell.setCanSplit(true);
    }

    @Override
    public boolean isPreviousUpgradePurchased(OrganelleUpgradeScreen organelleUpgradeScreen) {
        // Nucleus requires Flagella to be purchased.
        return organelleUpgradeScreen.hasFlagella();
    }
}