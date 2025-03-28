package cellcorp.gameofcells.objects.Organelle;

import cellcorp.gameofcells.objects.Cell;
import cellcorp.gameofcells.objects.Upgrade;
import cellcorp.gameofcells.screens.OrganelleUpgradeScreen;

/**
 * Base class for all organelle upgrades
 */
public abstract class OrganelleUpgrade implements Upgrade<OrganelleUpgradeScreen> {
    protected String name;
    protected int cost;
    protected String description;
    protected String perks;
    protected int requiredATP;
    protected int requiredSize;

    public OrganelleUpgrade(String name,
        int cost,
        String description,
        String perks,
        int requiredATP,
        int requiredSize) {
            this.name = name;
            this.cost = cost;
            this.description = description;
            this.perks = perks;
            this.requiredATP = requiredATP;
            this.requiredSize = requiredSize;
    }

    /**
     * Get the name of the upgrade.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the cost of the upgrade.
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Get the perks of the upgrade.
     */
    @Override
    public String getPerks() {
        return perks;
    }

    /**
     * Get the required ATP to purchase the upgrade.
     */
    @Override
    public int getRequiredATP() {
        return requiredATP;
    }

    /**
     * Get the required size to purchase the upgrade.
     */
    @Override
    public int getRequiredSize() {
        return requiredSize;
    }

    /**
     * Check if the upgrade can be purchased.
     * @param cell
     * @param screen
     * @return true if the upgrade can be purchased, false otherwise
     */
    @Override
    public boolean canPurchase(Cell cell, OrganelleUpgradeScreen screen) {
        int requiredSizeInCellUnits = 100 + requiredSize * 100;
        return cell.getCellATP() >= cost && 
            cell.getcellSize() >= requiredSizeInCellUnits &&
            !isAlreadyPurchased(cell) && isPreviousUpgradePurchased(screen);
    }

    /**
     * Apply the upgrade's perks to the cell.
     * @param cell
     */
    @Override
    public abstract void applyUpgrade(Cell cell);

    /**
     * Check if the upgrade is already purchased.
     * @param screen
     * @return
     */
    protected abstract boolean isAlreadyPurchased(Cell cell);
    
    /**
     * Check if the previous upgrade is purchased.
     * @param screen
     * @return
     */
    protected abstract boolean isPreviousUpgradePurchased(OrganelleUpgradeScreen screen);
}