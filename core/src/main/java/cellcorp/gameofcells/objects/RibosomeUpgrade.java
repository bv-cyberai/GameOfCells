package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.OrganelleUpgradeScreen;

/**
 * Ribosome upgrade.
 */
public class RibosomeUpgrade extends Upgrade {
    public RibosomeUpgrade() {
        super("Ribosome", 50, "Protein synthesis factory\nIncreases protein synthesis", 50, 2); 
    }

    /**
     * Apply the upgrade's perks to the cell.
     */
    @Override
    public void applyUpgrade(Cell cell) {
        // Increase protein synthesis (e.g., increase ATP gain from glucose)
        cell.setProteinSynthesisMultiplier(1.5f);
    }

    /**
     * Check if the previous upgrade is purchased.
     */
    @Override
    public boolean isPreviousUpgradePurchased(OrganelleUpgradeScreen organelleUpgradeScreen) {
        // Ribosome requires Mitochondria to be purchased.
        return organelleUpgradeScreen.hasMitochondria();
    }
}