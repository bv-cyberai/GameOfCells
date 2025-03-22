package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.screens.ShopScreen;

/**
 * Based class for all upgrades
 */
public class Upgrade {
    private String name;
    private int cost;
    private String description;
    private int requiredATP;
    private int requiredSize;

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