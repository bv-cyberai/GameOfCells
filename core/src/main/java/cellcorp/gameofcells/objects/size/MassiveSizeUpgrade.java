package cellcorp.gameofcells.objects.size;

import cellcorp.gameofcells.objects.Cell;
/**
 * MassiveSizeUpgrade Class
 * <p>
 * Manages the gameplay and visual
 * results of purchasing the Large Size Upgrade
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
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
