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

    @Override
    public void applyUpgrade(Cell cell) {
        // Increase movement speed
        cell.setMovementSpeedMultiplier(1.5f);
    }

    @Override
    public boolean isPreviousUpgradePurchased(OrganelleUpgradeScreen organelleUpgradeScreen) {
        // Flagella requires Ribosome to be purchased.
        return organelleUpgradeScreen.hasRibosome();
    }
}