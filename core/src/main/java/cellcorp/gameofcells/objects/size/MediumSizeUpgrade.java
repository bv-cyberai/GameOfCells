package cellcorp.gameofcells.objects.size;

import cellcorp.gameofcells.objects.Cell;

public class MediumSizeUpgrade extends SizeUpgrade {
    public MediumSizeUpgrade() {
        super(1, 65, 1, "Medium");
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
        cell.increasecellSize(sizeIncrease * 100);
        cell.setMediumSizeUpgrade(true);
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
