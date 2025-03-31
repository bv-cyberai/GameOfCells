package cellcorp.gameofcells.screens;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.runner.GameRunner;

public class TestShopScreen {

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

    @AfterAll
    public static void cleanUp() {
        if (Gdx.app != null) {
            Gdx.app.exit();
        }
    }

    /**
     * Test that the shop screen initializes with correct options.
     * This test checks that the shop screen shows both size and organelle upgrade options.
     */
    @Test
    public void shopScreenShowsCorrectOptions() {
        // Create game and move to shop screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step(); 

        var shopScreen = (ShopScreen) gameRunner.game.getScreen();
        
        // Verify both options are present
        assertEquals(2, shopScreen.getOptionCards().size());
        assertTrue(shopScreen.getOptionCards().get(0).toString().contains("Size"));
        assertTrue(shopScreen.getOptionCards().get(1).toString().contains("Organelle"));
    }
    
    /**
     * * Test that the shop screen allows navigation between options using arrow keys.
     * This test checks that pressing the right arrow key moves the selection to the next option,
     */
    @Test
    public void arrowKeysNavigateShopOptions() {
        // Create game and move to shop screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step(); 

        var shopScreen = (ShopScreen) gameRunner.game.getScreen();

        // Verify initial selection is on the first option
        assertEquals(0, shopScreen.getSelectedOptionIndex());

        // Press down arrow key to navigate to the second option
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.RIGHT));
        gameRunner.step();
        assertEquals(1, shopScreen.getSelectedOptionIndex());

        // Press up arrow key to navigate back to the first option
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.LEFT));
        gameRunner.step();
        assertEquals(0, shopScreen.getSelectedOptionIndex());
    }

    @Test
    public void escapeKeyReturnsToPreviousScreen() {
        // Create game and move to shop screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step(); 

        var shopScreen = (ShopScreen) gameRunner.game.getScreen();

        // Verify initial screen is the shop screen
        assertTrue(shopScreen == gameRunner.game.getScreen());

        // Press escape key to return to the previous screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ESCAPE));
        gameRunner.runForSeconds(2f);

        var previousScreen = gameRunner.game.getScreen();

        // Verify that the previous screen is the gameplay screen
        assertTrue(previousScreen instanceof GamePlayScreen);
        assertFalse(previousScreen instanceof ShopScreen);
        assertNotEquals(shopScreen, previousScreen);
    }

    // @Test
    // public void enterKeySelectsCurrentOption() {
    //     // Create game and move to shop screen
    //     var gameRunner = GameRunner.create();
    //     gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
    //     gameRunner.step();
    //     gameRunner.setHeldDownKeys(Set.of());
    //     gameRunner.step();
    //     gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q));
    //     gameRunner.step();
    //     gameRunner.setHeldDownKeys(Set.of());
    //     gameRunner.step(); 

    //     var shopScreen = (ShopScreen) gameRunner.game.getScreen();

    //     // Verify initial selection is on the first option
    //     assertEquals(0, shopScreen.getSelectedOptionIndex());

    //     // Press enter key to select the current option
    //     gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
    //     gameRunner.step();
    //     gameRunner.setHeldDownKeys(Set.of());
    //     gameRunner.step();

    //     var sizeUpgradeScreen = (SizeUpgradeScreen) gameRunner.game.getScreen();
    //     // Verify that the organelle upgrade screen is displayed
    //     assertTrue(sizeUpgradeScreen instanceof SizeUpgradeScreen);
    // }

    // @Test
    // public void shopScreenUpdatesTrackersCorrectly() {
    //     // Create game and move to shop screen
    //     var gameRunner = GameRunner.create();
    //     gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
    //     gameRunner.step();
    //     gameRunner.setHeldDownKeys(Set.of());
    //     gameRunner.step();
    //     gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q));
    //     gameRunner.step();
    //     gameRunner.setHeldDownKeys(Set.of());
    //     gameRunner.step(); 

    //     var shopScreen = (ShopScreen) gameRunner.game.getScreen();
    //     var cell = shopScreen.getPlayerCell();

    //     // Verify initial trackers are correct
    //     assertEquals(0, (shopScreen.getSizeTracker() - 100) / 100);
    //     assertEquals(30, shopScreen.getATPTracker());

    //     cell.setCellATP(50);

    //     // Press enter key to choose size upgrade option
    //     gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
    //     gameRunner.step();
    //     gameRunner.setHeldDownKeys(Set.of());
    //     gameRunner.step();

    //     // Press enter key to buy first size upgrade
    //     gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
    //     gameRunner.step();
    //     gameRunner.setHeldDownKeys(Set.of());
    //     gameRunner.step();
        
    //     // Press enter key to upgrade organelle
    //     gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q));
    //     gameRunner.step();
    //     gameRunner.setHeldDownKeys(Set.of());
    //     gameRunner.step();
        
    //     // Verify atp and size tracker is updated correctly
    //     assertEquals(0, shopScreen.getATPTracker());
    //     assertEquals(1, (shopScreen.getSizeTracker() - 100) / 100);
    // }

    @Test
    public void optionCardHighlightingWorks() {
        // Create game and move to shop screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step(); 

        var shopScreen = (ShopScreen) gameRunner.game.getScreen();

        // Verify initial selection is on the first option
        assertEquals(0, shopScreen.getSelectedOptionIndex());

        // Press right arrow key to navigate to the organelle option
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.RIGHT));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertEquals(1, shopScreen.getSelectedOptionIndex());

        // Verify that the second option is highlighted
        assertTrue(shopScreen.isHighlighted(shopScreen.getOptionCards(1)));

        // Press left arrow key to navigate back to the first option
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.LEFT));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertEquals(0, shopScreen.getSelectedOptionIndex());

        // Verify that the first option is highlighted
        assertTrue(shopScreen.isHighlighted(shopScreen.getOptionCards(0)));
    }
}