package cellcorp.gameofcells.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.providers.FakeGraphicsProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;

/**
 * Hud tester
 *
 * Tests all elements of the HUD including Energy bars even though these are
 * technically separate * from the HUDitself.
 */

public class TestHUD {

    @BeforeAll
    public static void setUpLibGDX() {
        System.setProperty("com.badlogic.gdx.backends.headless.disableNativesLoading", "true");
        // Initialize headless LibGDX
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationListener() {
            @Override public void create() {}
            @Override public void resize(int width, int height) {}
            @Override public void render() {}
            @Override public void pause() {}
            @Override public void resume() {}
            @Override public void dispose() {}
        }, config);

        // Mock the graphics provider
        Gdx.graphics = Mockito.mock(Graphics.class);
        Mockito.when(Gdx.graphics.getWidth()).thenReturn(Main.DEFAULT_SCREEN_WIDTH);
        Mockito.when(Gdx.graphics.getHeight()).thenReturn(Main.DEFAULT_SCREEN_HEIGHT);

        GL20 gl20 = Mockito.mock(GL20.class);
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
    }

    /**
     * Update Tester
     *
     * Tests that hud updates properly.
     */
    @Test
    public void testHUDUpdate() {
        var fakeGraphicsProvider = new FakeGraphicsProvider();
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        
        Mockito.when(fakeConfigProvider.getIntValue("cellHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("cellATP")).thenReturn(30);
        Mockito.when(fakeConfigProvider.getIntValue("maxHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("maxATP")).thenReturn(100);

        var fakeGamePlayScreen = Mockito.mock(GamePlayScreen.class);
        var cell = new Cell(fakeGamePlayScreen, fakeAssetManager,fakeConfigProvider);

        var spyCell = Mockito.spy(cell);

        HUD hud = new HUD(fakeGraphicsProvider, fakeAssetManager, spyCell.getMaxHealth(), spyCell.getMaxATP());
        hud.update(1f, spyCell.getCellHealth(), spyCell.getCellATP()); // simulate 1 second has passed

        assertEquals("Timer: 1", hud.getTimerString());
        assertEquals("HEALTH: 100/100", hud.getCellHealthString());
        assertEquals("ATP: 30/100", hud.getAtpString());
    }

    @Test
    public void testEnergyBarUpdate() {
        var fakeAssetManager = Mockito.mock(AssetManager.class);
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        Mockito.when(fakeConfigProvider.getIntValue("cellMaxHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("cellMaxATP")).thenReturn(100);
        var fakeGamePlayScreen = Mockito.mock(GamePlayScreen.class);
        var cell = new Cell(fakeGamePlayScreen, fakeAssetManager,fakeConfigProvider);

        var spyCell = Mockito.spy(cell);
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
        var fakeConfigProvider = Mockito.mock(ConfigProvider.class);
        Mockito.when(fakeConfigProvider.getIntValue("maxHealth")).thenReturn(100);
        Mockito.when(fakeConfigProvider.getIntValue("maxATP")).thenReturn(100);
        var fakeGamePlayScreen = Mockito.mock(GamePlayScreen.class);
        var cell = new Cell(fakeGamePlayScreen, fakeAssetManager,fakeConfigProvider);

        var spyCell = Mockito.spy(cell);
        EnergyBars energyBars = new EnergyBars(fakeAssetManager, spyCell.getMaxHealth(), spyCell.getMaxATP());

        energyBars.update(50, 50);
        assertEquals(.50 * EnergyBars.BAR_SIZE, energyBars.getHealthPercentage());
        assertEquals(.50 * EnergyBars.BAR_SIZE, energyBars.getATPPercentage());

        energyBars.update(25, 75);
        assertEquals(.25 * EnergyBars.BAR_SIZE, energyBars.getHealthPercentage());
        assertEquals(.75 * EnergyBars.BAR_SIZE, energyBars.getATPPercentage());

        energyBars.update(100, 100);
        assertEquals(EnergyBars.BAR_SIZE, energyBars.getHealthPercentage());
        assertEquals(EnergyBars.BAR_SIZE, energyBars.getATPPercentage());

        energyBars.update(0, 0);
        assertEquals(0, energyBars.getHealthPercentage());
        assertEquals(0, energyBars.getATPPercentage());

    }

}
