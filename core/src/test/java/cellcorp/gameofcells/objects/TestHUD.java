package cellcorp.gameofcells.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cellcorp.gameofcells.providers.FakeGraphicsProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.assets.AssetManager;

/**
 * Hud tester
 * 
 * Tests all elements of the HUD including Energy bars even though these are
 * technically separate * from the HUDitself.
 */

public class TestHUD {

    /**
     * Update Tester
     * 
     * Tests that hud updates properly.
     */
    @Test
    public void testHUDUpdate() {
        var fakeGraphicsProvider = new FakeGraphicsProvider();
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var spyCell = Mockito.spy(new Cell(fakeAssetManager));

        HUD hud = new HUD(fakeGraphicsProvider, fakeAssetManager, spyCell.getMaxHealth(), spyCell.getMaxATP());
        hud.update(1f, spyCell.getCellHealth(), spyCell.getCellATP()); // simulate 1 second has passed

        assertEquals("Timer: 1", hud.getTimerString());
        assertEquals("HEALTH: 100/100", hud.getCellHealthString());
        assertEquals("ATP: 30", hud.getAtpString());
    }

    @Test
    public void testEnergyBarUpdate() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);

        var spyCell = Mockito.spy(new Cell(fakeAssetManager));
        EnergyBars energyBars = new EnergyBars(fakeAssetManager, spyCell.getMaxHealth(), spyCell.getMaxATP());

        // default constructor values - correct values are set during update.
        assertEquals(0, energyBars.getCellHealth());
        assertEquals(100, energyBars.getCellATP());

        energyBars.update(50, 50);

        assertEquals(50, energyBars.getCellHealth());
        assertEquals(50, energyBars.getCellATP());

    }

    @Test
    public void testEnergyBarPercentage() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var spyCell = Mockito.spy(new Cell(fakeAssetManager));

        EnergyBars energyBars = new EnergyBars(fakeAssetManager, spyCell.getMaxHealth(), spyCell.getMaxATP());

        energyBars.update(50, 50);
        assertEquals(.50 * energyBars.getBarSize(), energyBars.getHealthPercentage());
        assertEquals(.50 * energyBars.getBarSize(), energyBars.getATPPercentage());

        energyBars.update(25, 75);
        assertEquals(.25 * energyBars.getBarSize(), energyBars.getHealthPercentage());
        assertEquals(.75 * energyBars.getBarSize(), energyBars.getATPPercentage());

        energyBars.update(100, 100);
        assertEquals(1 * energyBars.getBarSize(), energyBars.getHealthPercentage());
        assertEquals(1 * energyBars.getBarSize(), energyBars.getATPPercentage());

        energyBars.update(0, 0);
        assertEquals(0 * energyBars.getBarSize(), energyBars.getHealthPercentage());
        assertEquals(0 * energyBars.getBarSize(), energyBars.getATPPercentage());

    }

}
