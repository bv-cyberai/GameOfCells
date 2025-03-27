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
     * Check if the ribosome upgrade is already purchased.
     * @param cell
     */
    @Override
    protected boolean isAlreadyPurchased(Cell cell) {
        return cell.hasRibosomes();
    }

    /**
     * Apply the upgrade's perks to the cell.
     * Increase protein synthesis.
     * @param cell
     */
    @Override
    public void applyUpgrade(Cell cell) {
        // Increase protein synthesis (e.g., increase ATP gain from glucose)
        cell.setProteinSynthesisMultiplier(1.5f);
        cell.setHasRibosomes(true);
    }

    /**
     * Check if the previous upgrade is purchased.
     * Ribosome requires Mitochondria to be purchased.
     * @param organelleUpgradeScreen
     */
    @Override
    protected boolean isPreviousUpgradePurchased(OrganelleUpgradeScreen organelleUpgradeScreen) {
        return organelleUpgradeScreen.hasMitochondria();
    }
}