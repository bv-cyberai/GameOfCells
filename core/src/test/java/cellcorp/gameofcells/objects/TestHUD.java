package cellcorp.gameofcells.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.assets.AssetManager;

/**
 * Hud tester
 * 
 * Tests all elements of the HUD including Energy bars even though these are technically separate * from the HUDitself.  
 */

public class TestHUD {

    /**
     * Update Tester
     * 
     * Tests that hud updates properly.
     */
    @Test
    public void testHUDUpdate() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        
        // var fakeCell = Mockito.mock(Cell.class);
        var spyCell = Mockito.spy(new Cell(fakeAssetManager));
        
        HUD hud = new HUD(fakeAssetManager, spyCell.getMaxHealth(), spyCell.getMaxATP());
        hud.update(1f,spyCell.getCellHealth(),spyCell.getCellATP()); // simulate 1 second has passed

        assertEquals("Timer: 1", hud.getTimerString());
        assertEquals("HEALTH: 100/100", hud.getCellHealthString());
        assertEquals("ATP: 30", hud.getAtpString());
    }

    @Test
    public void testEnergyBarUpdate() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        
        var spyCell = Mockito.spy(new Cell(fakeAssetManager));
        EnergyBars energyBars = new EnergyBars(fakeAssetManager,spyCell.getMaxHealth(), spyCell.getMaxATP());

        //default constructor values - correct values are set during update.
        assertEquals(0, energyBars.getCellHealth()); 
        assertEquals(100, energyBars.getCellATP());

        energyBars.update(50,50);

        assertEquals(50, energyBars.getCellHealth());
        assertEquals(50, energyBars.getCellATP());

    }

    @Test
    public void testEnergyBarPercentage() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        
        var spyCell = Mockito.spy(new Cell(fakeAssetManager));
        // var energyBars = Mockito.spy(new EnergyBars(spyCell.getMaxHealth(), spyCell.getMaxATP()));

        EnergyBars energyBars = new EnergyBars(fakeAssetManager, spyCell.getMaxHealth(), spyCell.getMaxATP());


        System.out.println(spyCell.getCellHealth());
        System.out.println(spyCell.getMaxHealth());
        System.out.println(spyCell.getCellATP());
        System.out.println(spyCell.getMaxATP());

        
        // //default constructor values - correct values are set during update.
        // assertEquals(0, energyBars.getHealthPercentage());
        // assertEquals(100, energyBars.getATPPercentage());

        energyBars.update(50,50);
        System.out.println("=======================");
        System.out.println(energyBars.getCellHealth());
        System.out.println(energyBars.getCellATP());
        System.out.println(energyBars.getHealthPercentage());
        System.out.println((energyBars.getCellHealth()/100)*400);
        System.out.println(energyBars.getATPPercentage());
        System.out.println("=======================");

        assertEquals(.50 * energyBars.getBarSize(), energyBars.getHealthPercentage());
        assertEquals(.50 * energyBars.getBarSize(), energyBars.getATPPercentage());

        energyBars.update(25,75);

        assertEquals(.25 * energyBars.getBarSize(), energyBars.getHealthPercentage());
        assertEquals(.75 * energyBars.getBarSize(), energyBars.getATPPercentage());

        energyBars.update(100,100);

        assertEquals(1 * energyBars.getBarSize(), energyBars.getHealthPercentage());
        assertEquals(1 * energyBars.getBarSize(), energyBars.getATPPercentage());

        energyBars.update(0,0);

        assertEquals(0 * energyBars.getBarSize(), energyBars.getHealthPercentage());
        assertEquals(0 * energyBars.getBarSize(), energyBars.getATPPercentage());

    }

}
