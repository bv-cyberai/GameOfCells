package cellcorp.gameofcells.objects.organelle;

import cellcorp.gameofcells.objects.Cell;

/**
 * Flagellum Class
 * <p>
 * Manages the implementation of the Flagellum Upgrade
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class FlagellumUpgrade extends OrganelleUpgrade {
    /**
     * Create this upgrade.
     */
    public FlagellumUpgrade() {
        super("Flagellum", 70, "Increases movement speed", "Increases movement speed", 70, 3);
    }

    /**
     * Check if the flagellum upgrade is already purchased.
     *
     * @param cell
     */
    @Override
    protected boolean isAlreadyPurchased(Cell cell) {
        return cell.hasFlagellum();
    }

    /**
     * Apply the upgrade's perks to the cell.
     * Increase movement speed.
     *
     * @param cell
     */
    @Override
    public void applyUpgrade(Cell cell) {
        cell.setHasFlagellum(true);
        cell.removeCellATP(cost);
        // Increase movement speed
    }

    /**
     * Check if the previous upgrade is purchased.
     * Flagellum requires Ribosome to be purchased.
     *
     * @param cell the cell.
     */
    @Override
    public boolean isPreviousUpgradePurchased(Cell cell) {
        // Flagellum requires Ribosome to be purchased.
        return cell.hasRibosomes();
    }
}
