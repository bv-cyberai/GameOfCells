package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.OrganelleUpgradeScreen;

/**
 * Based class for all upgrades
 */
public abstract class Upgrade {
    protected String name; // Name of the upgrade
    protected int cost; // Cost of the upgrade
    protected String description; // Description of the upgrade
    protected int requiredATP; // Required ATP to purchase the upgrade
    protected int requiredSize; // Required size to purchase the upgrade

    public Upgrade(String name,
            int cost,
            String description,
            int requiredATP,
            int requiredSize) {
                this.name = name;
                this.cost = cost;
                this.description = description;
                this.requiredATP = requiredATP;
                this.requiredSize = requiredSize;
    }

    /**
     * Check if the player can purchase this upgrade
     */
    public boolean canPurchase(Cell cell, OrganelleUpgradeScreen organelleUpgradeScreen) {
        if (cell.getCellATP() < cost || cell.getcellSize() < requiredSize) {
            return false;
        }

        // Check if previous upgrade is purchased
        return isPreviousUpgradePurchased(organelleUpgradeScreen);
    }

    /**
     * Apply the upgrade's perks to the cell.
     */
    public abstract void applyUpgrade(Cell cell);

    /**
     * Check if the previous upgrade is purchased.
     */
    protected abstract boolean isPreviousUpgradePurchased(OrganelleUpgradeScreen organelleUpgradeScreen);

    /**
     * Get the name of the upgrade
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get the cost of the upgrade
     * @return
     */
    public int getCost() {
        return cost;
    }

    /**
     * Get the description of the upgrade
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the required ATP of the upgrade
     * @return
     */
    public int getRequiredATP() {
        return requiredATP;
    }

    /**
     * Get the required size of the upgrade
     * @return
     */
    public int getRequiredSize() {
        return requiredSize;
    }
}