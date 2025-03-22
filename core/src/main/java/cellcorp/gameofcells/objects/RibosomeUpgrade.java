package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.ShopScreen;

/**
 * Ribosome upgrade.
 */
public class RibosomeUpgrade extends Upgrade {
    public RibsomeUpgrade() {
        super("Ribosome", 75, "Increases protein synthesis", 50, 100);
    }

    @Override
    public void applyUpgrade(Cell cell) {
        // Increase protein synthesis (e.g., increase ATP gain from glucose)
        cell.setProteinSynthesisMultiplier(1.5f);
    }

    @Override
    public boolean isPreviousUpgradePurchased(ShopScreen shopScreen) {
        // Ribosome requires Mitochondria to be purchased.
        return shopScreen.hasMitochondria();
    }
}