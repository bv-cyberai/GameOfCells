package cellcorp.gameofcells.objects.size;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.screens.SizeUpgradeScreen;

public class MassiveSizeUpgrade extends SizeUpgrade {
    public MassiveSizeUpgrade() {
        super(1, 100, 3, "Massive");
    }

    @Override
    public String getPerks() {
        return "• +1 Size Unit\n• Maximum growth\n• " + getVisualEffect();
    }

    @Override
    public String getVisualEffect() {
        return "Glowing nucleus";
    }

    @Override
    public void applyUpgrade(Cell cell) {
        cell.increasecellSize(sizeIncrease * 100);
        cell.setMassiveSizeUpgrade(true);
        cell.removeCellATP(atpCost);
    }

    @Override
    public boolean isAlreadyPurchased(Cell cell) {
        return cell.hasMassiveSizeUpgrade();
    }

    @Override
    protected boolean isPreviousUpgradePurchased(SizeUpgradeScreen sizeUpgradeScreen) {
        return sizeUpgradeScreen.hasLargeSizeUpgrade();
    }
}
