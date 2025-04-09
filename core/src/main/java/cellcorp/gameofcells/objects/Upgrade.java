package cellcorp.gameofcells.objects;

/**
 * Upgrade Interface
 * <p>
 * Defines the common methods for all upgrades in the game
 */
public interface Upgrade {
    String getName();
    String getDescription();
    String getPerks();
    int getRequiredATP();
    int getRequiredSize();

    // Generic version that works for both screens
    boolean canPurchase(Cell cell);
    void applyUpgrade(Cell cell);
}