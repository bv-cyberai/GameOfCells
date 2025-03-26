package cellcorp.gameofcells.objects.Organelle;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.screens.OrganelleUpgradeScreen;

/**
 * Ribosome upgrade.
 */
public class RibosomeUpgrade extends OrganelleUpgrade {
    public RibosomeUpgrade() {
        super("Ribosome", 50, "Protein synthesis factory", "Increases protein synthesis", 50, 2); 
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
    protected boolean isPreviousUpgradePurchased(OrganelleUpgradeScreen organelleUpgradeScreen) {
        // Ribosome requires Mitochondria to be purchased.
        return organelleUpgradeScreen.hasMitochondria();
    }
}