package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.ShopScreen;

/**
 * Ribosome upgrade.
 */
public class NucleusUpgrade extends Upgrade {
    public NucleusUpgrade() {
        super("Nucleus", 150, "Unlocks advanced abilities", 100, 200);
    }

    @Override
    public void applyUpgrade(Cell cell) {
        // Unlock advanced abilities (e.g., allow the cell to split)
        cell.setCanSplit(true);
    }

    @Override
    public boolean isPreviousUpgradePurchased(ShopScreen shopScreen) {
        // Nucleus requires Flagella to be purchased.
        return shopScreen.hasFlagella();
    }
}