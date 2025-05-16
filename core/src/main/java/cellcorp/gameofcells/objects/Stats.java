package cellcorp.gameofcells.objects;

/**
 * Stats Class
 * <p>
 * Tracks the in game stats for the game over screen display
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 03/05/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class Stats {
    public float gameTimer = 0;
    public int glucoseCollected = 0;
    public int atpGenerated = 0;
    public float distanceMoved = 0;
    /**
     * Size in range (0, ..=
     */
    public int maxSize = 0;
    public int organellesPurchased = 0;

    /**
     * Generate a string describing the current max size.
     */
    public String sizeDescription() {
        switch (maxSize) {
            case 0:
                return "Tiny";
            case 1:
                return "Small";
            case 2:
                return "Medium";
            case 3:
                return "Large";
            default:
                return "Massive";
        }
    }

    /**
     * Set the size of the cell.
     *
     * @param size the new size
     */
    public void setSize(int size) {
        if (size > maxSize) {
            maxSize = size;
        }
    }

    /**
     * Get the size of the cell.
     *
     * @return the cell size
     */
    public int getSize() {
        return maxSize;
    }

}
