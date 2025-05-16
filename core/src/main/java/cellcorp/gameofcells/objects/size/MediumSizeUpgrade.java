package cellcorp.gameofcells.objects.size;

import cellcorp.gameofcells.objects.Cell;
/**
 * MediumSizeUpgrade Class
 * <p>
 * Manages the gameplay and visual
 * results of purchasing the Medium Size Upgrade
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
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
