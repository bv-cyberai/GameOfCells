package cellcorp.gameofcells.objects.organelle;

import cellcorp.gameofcells.objects.Cell;
/**
 * Mitochondria upgrade.
 */
public class MitochondriaUpgrade extends OrganelleUpgrade {
    public MitochondriaUpgrade() {
        super("Mitochondria",
            30,
            "Powerhouse of the cell",
            "Increases ATP production",
            30,
            1);
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
     * @param cell the game cell
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
     * @param cell the game cell
     */
    @Override
    protected boolean isPreviousUpgradePurchased(Cell cell) {
        return true;
    }
}
