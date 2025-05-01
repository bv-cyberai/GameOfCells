package cellcorp.gameofcells;

/**
 * Test Test Class
 * <p>
 * A class to make sure Junit test work.
 * Feel free to rename/refactor for your own test.
 *
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTestClass {

    @Test
    public void testSimple() throws Exception {
        int x = 0;
        System.out.println("TESTDEBUGPRINT");
        assertEquals(0, x);
    }
}
