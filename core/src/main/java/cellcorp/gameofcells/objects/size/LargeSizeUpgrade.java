package cellcorp.gameofcells.objects.size;

import cellcorp.gameofcells.objects.Cell;


public class LargeSizeUpgrade extends SizeUpgrade {
    private static final float SIZE_INCREASE = 75;

    public LargeSizeUpgrade() {
        super(3, 85, 2, "Large");
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
        cell.increaseCellSize(SIZE_INCREASE);
        cell.setHasLargeSizeUpgrade(true);
        cell.removeCellATP(atpCost);
    }

    @Override
    public boolean isAlreadyPurchased(Cell cell) {
        return cell.hasLargeSizeUpgrade();
    }

    @Override
    protected boolean isPreviousUpgradePurchased(Cell cell) {
        return cell.hasMediumSizeUpgrade();
    }
}
