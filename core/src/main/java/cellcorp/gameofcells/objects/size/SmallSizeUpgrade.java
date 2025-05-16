package cellcorp.gameofcells.objects.size;

import cellcorp.gameofcells.objects.Cell;
/**
 * SmallSizeUpgrade Class
 * <p>
 * Manages the gameplay and visual
 * results of purchasing the small size upgrade
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class SmallSizeUpgrade extends SizeUpgrade {
    public static final int ATP_COST = 50;
    private static final float SIZE_INCREASE = 75;

    public SmallSizeUpgrade() {
        super(1, ATP_COST, 0, "Small");
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
        cell.increaseCellSize(SIZE_INCREASE);
        cell.setHasSmallSizeUpgrade(true);
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
