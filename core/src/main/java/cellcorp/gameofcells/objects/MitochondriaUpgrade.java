package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.ShopScreen;

/**
 * Mitochondria upgrade.
 */
public class MitochondriaUpgrade extends Upgrade {
    public MitochondriaUpgrade() {
        super("Mitochondria", 30, "Powerhouse of the cell\nIncreases ATP production", 30, 0);
    }

    @Override
    public void applyUpgrade(Cell cell) {
        cell.setHasMitochondria(true);
    }

    @Override
    public boolean isPreviousUpgradePurchased(ShopScreen shopScreen) {
        // Mitochondria is the first upgrade, so no previous upgrade it required.
        return true;
    }
}