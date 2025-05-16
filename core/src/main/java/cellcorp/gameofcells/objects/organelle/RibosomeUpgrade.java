package cellcorp.gameofcells.objects.organelle;

import cellcorp.gameofcells.objects.Cell;

/**
 * RibosomeUpgrade Class
 * <p>
 * Class For Ribosome Upgrade
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class RibosomeUpgrade extends OrganelleUpgrade {
    public RibosomeUpgrade() {
        super("Ribosome", 50, "Protein synthesis factory", "Increases protein synthesis", 50, 2);
    }

    /**
     * Check if the ribosome upgrade is already purchased.
     *
     * @param cell
     */
    @Override
    protected boolean isAlreadyPurchased(Cell cell) {
        return cell.hasRibosomes();
    }

    /**
     * Apply the upgrade's perks to the cell.
     * Increase protein synthesis.
     *
     * @param cell
     */
    @Override
    public void applyUpgrade(Cell cell) {
        cell.setHasRibosomes(true);
        cell.removeCellATP(cost);
        // Increase protein synthesis (e.g., increase ATP gain from glucose)
    }

    /**
     * Check if the previous upgrade is purchased.
     * Ribosome requires Mitochondria to be purchased.
     *
     * @param organelleUpgradeScreen
     */
    @Override
    protected boolean isPreviousUpgradePurchased(Cell cell) {
        return cell.hasMitochondria();
    }
}
