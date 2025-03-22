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
}