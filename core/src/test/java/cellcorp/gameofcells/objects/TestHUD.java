package cellcorp.gameofcells.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.assets.AssetManager;

public class TestHUD {

    /**
     * Update Tester
     * 
     * Tests that hud updates properly.
     */
    @Test
    public void testUpdate() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        
        // var fakeCell = Mockito.mock(Cell.class);
        var spyCell = Mockito.spy(new Cell(fakeAssetManager));
        
        HUD hud = new HUD(fakeAssetManager, spyCell.getMaxHealth(), spyCell.getMaxATP());
        hud.update(1f,spyCell.getCellHealth(),spyCell.getCellATP()); // simulate 1 second has passed

        assertEquals("Timer: 1", hud.getTimerString());
        assertEquals("HEALTH: 100/100", hud.getCellHealthString());
        assertEquals("ATP: 0", hud.getAtpString());
    }

}
