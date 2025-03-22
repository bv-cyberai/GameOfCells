package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.ShopScreen;

/**
 * Mitochondria upgrade.
 */
public class MitochondriaUpgrade extends Upgrade {
    public MitochondriaUpgrade() {
        super("Mitochondria", 30, "Increases ATP production", 0, 0);
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