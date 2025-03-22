package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.ShopScreen;

/**
 * Based class for all upgrades
 */
public abstract class Upgrade {
    protected String name;
    protected int cost;
    protected String description;
    protected int requiredATP;
    protected int requiredSize;

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
    public boolean canPurchase(Cell cell, ShopScreen shopScreen) {
        if (cell.getCellATP() < cost || cell.getCellDiameter() < requiredSize) {
            return false;
        }

        // Check if previous upgrade is purchased
        return isPreviousUpgradePurchased(shopScreen);
    }

    /**
     * Apply the upgrade's perks to the cell.
     */
    public abstract void applyUpgrade(Cell cell);

    /**
     * Check if the previous upgrade is purchased.
     */
    protected abstract boolean isPreviousUpgradePurchased(ShopScreen shopScreen);

    // Getters
    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    public int getRequiredATP() {
        return requiredATP;
    }

    public int getRequiredSize() {
        return requiredSize;
    }
}