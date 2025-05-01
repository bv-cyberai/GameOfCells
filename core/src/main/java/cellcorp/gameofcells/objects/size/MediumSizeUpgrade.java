package cellcorp.gameofcells.objects.size;

import cellcorp.gameofcells.objects.Cell;

public class MediumSizeUpgrade extends SizeUpgrade {
    private static final float SIZE_INCREASE = 50;

    public MediumSizeUpgrade() {
        super(2, 65, 1, "Medium");
    }

    @Override
    public String getPerks() {
        return "• +1 Size Unit\n• Visible growth\n• " + getVisualEffect();
    }

    @Override
    public String getVisualEffect() {
        return "Pulsing membrane";
    }

    @Override
    public void applyUpgrade(Cell cell) {
        cell.increaseCellSize(SIZE_INCREASE);
        cell.setHasMediumSizeUpgrade(true);
        cell.removeCellATP(atpCost);
    }

    @Override
    public boolean isAlreadyPurchased(Cell cell) {
        return cell.hasMediumSizeUpgrade();
    }

    @Override
    protected boolean isPreviousUpgradePurchased(Cell cell) {
        return cell.hasSmallSizeUpgrade();
    }
}
