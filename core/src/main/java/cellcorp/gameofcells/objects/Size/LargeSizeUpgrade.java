package cellcorp.gameofcells.objects.Size;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.screens.SizeUpgradeScreen;

public class LargeSizeUpgrade extends SizeUpgrade {
    public LargeSizeUpgrade() {
        super(1, 85, 2, "Large");
    }

    @Override
    public String getPerks() {
        return "• +1 Size Unit\n• Significant mass\n• " + getVisualEffect();
    }

    @Override
    public String getVisualEffect() {
        return "Swirling cytoplasm";
    }

    @Override
    public void applyUpgrade(Cell cell) {
        cell.increasecellSize(sizeIncrease * 100);
        cell.setLargeSizeUpgrade(true);
        cell.removeCellATP(atpCost);
    }

    @Override
    public boolean isAlreadyPurchased(Cell cell) {
        return cell.hasLargeSizeUpgrade();
    }

    @Override
    protected boolean isPreviousUpgradePurchased(SizeUpgradeScreen sizeUpgradeScreen) {
        return sizeUpgradeScreen.hasMediumSizeUpgrade();
    }
}
