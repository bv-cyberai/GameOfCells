package cellcorp.gameofcells.objects.size;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Upgrade;

/**
 * MediumSizeUpgrade Class
 * <p>
 * Represents a size upgrade that can be purchased in the shop
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public abstract class SizeUpgrade implements Upgrade {
    protected final int sizeLevel;
    protected final int atpCost;
    protected final int requiredSize;
    protected final String tierName;

    /**
     * Creates a new size upgrade
     *
     * @param sizeIncrease How much to increase the cell size (in units)
     * @param atpCost      ATP cost for this upgrade
     * @param requiredSize Minimum size required to purchase (in units)
     * @param tierName     Name of the upgrade tier
     */
    public SizeUpgrade(int sizeLevel, int atpCost, int requiredSize, String tierName) {
        this.sizeLevel = sizeLevel;
        this.atpCost = atpCost;
        this.requiredSize = requiredSize;
        this.tierName = tierName;
    }

    /**
     * Gets the name of the upgrade
     */
    @Override
    public String getName() {
        return tierName + " Size Upgrade";
    }

    /**
     * Gets the description of the upgrade
     */
    @Override
    public String getDescription() {
        return "Increases cell size by " + 1 + " units";
    }

    /**
     * Gets the perks of the upgrade
     */
    @Override
    public String getPerks() {
        return "• Larger collision radius\n• Increased visibility";
    }

    /**
     * Gets the ATP cost of the upgrade
     */
    @Override
    public int getRequiredATP() {
        return atpCost;
    }

    /**
     * Gets the required size for the upgrade
     */
    @Override
    public int getRequiredSize() {
        return requiredSize;
    }

    /**
     * Checks if the upgrade can be purchased
     *
     * @param cell   The cell to check
     * @param screen The screen to check
     */
    @Override
    public boolean canPurchase(Cell cell) {
        return cell.getCellATP() >= atpCost &&
            isPreviousUpgradePurchased(cell) &&
            !isAlreadyPurchased(cell);
    }

    /**
     * Applies the upgrade to the cell
     *
     * @param cell The cell to apply the upgrade to
     */
    @Override
    public abstract void applyUpgrade(Cell cell);

    // Unique visual effect description for each tier
    public abstract String getVisualEffect();

    /**
     * Check if the previous upgrade was purchased
     *
     * @param screen
     * @return true if the previous upgrade was purchased, false otherwise
     */
    protected abstract boolean isPreviousUpgradePurchased(Cell cell);

    /**
     * Check if the upgrade is already purchased
     *
     * @param cell
     * @return true if the upgrade is already purchased, false otherwise
     */
    protected abstract boolean isAlreadyPurchased(Cell cell);
}
