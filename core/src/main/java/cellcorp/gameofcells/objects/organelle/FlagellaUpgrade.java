package cellcorp.gameofcells.objects.organelle;

import cellcorp.gameofcells.objects.Cell;

/**
 * Flagella upgrade.
 */
public class FlagellaUpgrade extends OrganelleUpgrade {
    public FlagellaUpgrade() {
        super("Flagellum", 70, "Increases movement speed", "Increases movement speed", 70, 3);
    }

    /**
     * Check if the flagella upgrade is already purchased.
     *
     * @param cell
     */
    @Override
    protected boolean isAlreadyPurchased(Cell cell) {
        return cell.hasFlagella();
    }

    /**
     * Apply the upgrade's perks to the cell.
     * Increase movement speed.
     *
     * @param cell
     */
    @Override
    public void applyUpgrade(Cell cell) {
        cell.setHasFlagella(true);
        cell.removeCellATP(cost);
        // Increase movement speed
    }

    /**
     * Check if the previous upgrade is purchased.
     * Flagella requires Ribosome to be purchased.
     *
     * @param cell the cell.
     */
    @Override
    public boolean isPreviousUpgradePurchased(Cell cell) {
        // Flagella requires Ribosome to be purchased.
        return cell.hasRibosomes();
    }
}
