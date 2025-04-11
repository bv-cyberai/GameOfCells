package cellcorp.gameofcells.objects.size;

import cellcorp.gameofcells.objects.Cell;

public class SmallSizeUpgrade extends SizeUpgrade {
    public SmallSizeUpgrade() {
        super(1, 50, 0, "Small");
    }

    @Override
    public String getPerks() {
        return "• +1 Size Unit\n• Basic expansion\n• " + getVisualEffect();
    }

    @Override
    public String getVisualEffect() {
        return "Slight glow effect";
    }

    @Override
    public void applyUpgrade(Cell cell) {
        cell.increasecellSize(sizeIncrease * 100);
        cell.setSmallSizeUpgrade(true);
        cell.removeCellATP(atpCost);
    }

    @Override
    public boolean isAlreadyPurchased(Cell cell) {
        return cell.hasSmallSizeUpgrade();
    }

    @Override
    protected boolean isPreviousUpgradePurchased(Cell cell) {
        return true;
    }
}
