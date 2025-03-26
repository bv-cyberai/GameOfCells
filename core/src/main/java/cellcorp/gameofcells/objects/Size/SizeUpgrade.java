package cellcorp.gameofcells.objects.Size;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Upgrade;
import cellcorp.gameofcells.screens.SizeUpgradeScreen;

/**
 * Size Upgrade
 * <p>
 * Represents a size upgrade that can be purchased in the shop
 */
public abstract class SizeUpgrade implements Upgrade<SizeUpgradeScreen> {
    protected final int sizeIncrease;
    protected final int atpCost;
    protected final int requiredSize;
    protected final String tierName;

    /**
     * Creates a new size upgrade
     * @param sizeIncrease How much to increase the cell size (in units)
     * @param atpCost ATP cost for this upgrade
     * @param requiredSize Minimum size required to purchase (in units)
     * @param tierName Name of the upgrade tier
     */
    public SizeUpgrade(int sizeIncrease, int atpCost, int requiredSize, String tierName) {
        this.sizeIncrease = sizeIncrease;
        this.atpCost = atpCost;
        this.requiredSize = requiredSize;
        this.tierName = tierName;
    }

    @Override
    public String getName() {
        return tierName + " Size Upgrade";
    }

    @Override
    public String getDescription() {
        return "Increases cell size by " + sizeIncrease + " units";
    }

    @Override
    public String getPerks() {
        return "• Larger collision radius\n• Increased visibility";
    }

    @Override
    public int getRequiredATP() {
        return atpCost;
    }

    @Override
    public int getRequiredSize() {
        return requiredSize;
    }

    @Override
    public boolean canPurchase(Cell cell, SizeUpgradeScreen screen) {
        // Convert shop size units to actual cell size (100 = base size)
        int currentSizeUnits = (cell.getcellSize() - 100) / 100;
        return cell.getCellATP() >= atpCost && 
            currentSizeUnits >= requiredSize;
    }

    @Override
    public void applyUpgrade(Cell cell) {
        // Convert size units to actual pixels (100 = base size)
        cell.increasecellSize(sizeIncrease * 100);
        cell.removeCellATP(atpCost);
    }

    /**
     * Gets the size increase amount (in units)
     */
    public int getSizeIncrease() {
        return sizeIncrease;
    }

    // Unique visual effect description for each tier
    public abstract String getVisualEffect();
}