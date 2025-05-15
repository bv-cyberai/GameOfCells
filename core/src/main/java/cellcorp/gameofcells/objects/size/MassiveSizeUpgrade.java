package cellcorp.gameofcells.objects.size;

import cellcorp.gameofcells.objects.Cell;

/**
 * Massive size upgrade.
 */
public class MassiveSizeUpgrade extends SizeUpgrade {
    private static final float SIZE_INCREASE = 50;

    public MassiveSizeUpgrade() {
        super(4, 100, 3, "Massive");
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
        cell.increaseCellSize(SIZE_INCREASE);
        cell.setHasMassiveSizeUpgrade(true);
        cell.setSizeUpgradeLevel(sizeLevel);
        cell.removeCellATP(atpCost);
    }

    @Override
    public boolean isAlreadyPurchased(Cell cell) {
        return cell.hasMassiveSizeUpgrade();
    }

    @Override
    protected boolean isPreviousUpgradePurchased(Cell cell) {
        return cell.hasLargeSizeUpgrade();
    }
}
