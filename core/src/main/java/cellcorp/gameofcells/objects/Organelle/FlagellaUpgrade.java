package cellcorp.gameofcells.objects.Organelle;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.screens.OrganelleUpgradeScreen;

/**
 * Flagella upgrade.
 */
public class FlagellaUpgrade extends OrganelleUpgrade {
    public FlagellaUpgrade() {
        super("Flagella", 70, "Cell movement", "Increases movement speed", 70, 3);
    }

    /**
     * Check if the flagella upgrade is already purchased.
     * @param cell
     */
    @Override
    protected boolean isAlreadyPurchased(Cell cell) {
        return cell.hasFlagella();
    }
    /**
     * Apply the upgrade's perks to the cell.
     * Increase movement speed.
     * @param cell
     */
    @Override
    public void applyUpgrade(Cell cell) {
        // Increase movement speed
        cell.setMovementSpeedMultiplier(1.5f);
    }

    /**
     * Check if the previous upgrade is purchased.
     * Flagella requires Ribosome to be purchased.
     * @param organelleUpgradeScreen
     */
    @Override
    public boolean isPreviousUpgradePurchased(OrganelleUpgradeScreen organelleUpgradeScreen) {
        // Flagella requires Ribosome to be purchased.
        return organelleUpgradeScreen.hasRibosome();
    }
}