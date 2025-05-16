package cellcorp.gameofcells.objects.organelle;

import cellcorp.gameofcells.objects.Cell;

/**
 * NucleusUpgrade Class
 * <p>
 * Manages the implementation of the Nucleus Upgrade
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class NucleusUpgrade extends OrganelleUpgrade {
    public NucleusUpgrade() {
        super("Nucleus", 90, "Control center of the cell", "Unlocks advanced abilities", 90, 4);
    }

    /**
     * Check if the nucleus upgrade is already purchased.
     *
     * @param cell
     */
    @Override
    protected boolean isAlreadyPurchased(Cell cell) {
        return cell.hasNucleus();
    }

    /**
     * Apply the upgrade's perks to the cell.
     * Unlock advanced abilities.
     *
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
     * Nucleus requires Flagellum to be purchased.
     */
    @Override
    public boolean isPreviousUpgradePurchased(Cell cell) {
        return cell.hasFlagellum();
    }
}
