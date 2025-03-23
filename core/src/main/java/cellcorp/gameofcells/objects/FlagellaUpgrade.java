package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.ShopScreen;

/**
 * Flagella upgrade.
 */
public class FlagellaUpgrade extends Upgrade {
    public FlagellaUpgrade() {
        super("Flagella", 70, "Cell movement and propulsion\nIncreases movement speed", 70, 150);
    }

    @Override
    public void applyUpgrade(Cell cell) {
        // Increase movement speed
        cell.setMovementSpeedMultiplier(1.5f);
    }

    @Override
    public boolean isPreviousUpgradePurchased(ShopScreen shopScreen) {
        // Flagella requires Ribosome to be purchased.
        return shopScreen.hasRibosome();
    }
}