package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.ShopScreen;

/**
 * Ribosome upgrade.
 */
public class NucleusUpgrade extends Upgrade {
    public NucleusUpgrade() {
        super("Nucleus", 90, "Control center of the cell\nUnlocks advanced abilities", 90, 200);
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