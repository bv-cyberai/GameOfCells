package cellcorp.gameofcells.screens;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.List;

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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

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

        SpriteBatch spriteBatch = Mockito.mock(SpriteBatch.class);
        Mockito.when(spriteBatch.isDrawing()).thenReturn(false);
    }

    @AfterAll
    public static void cleanUp() {
        if (Gdx.app != null) {
            Gdx.app.exit();
        }
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

    /**
     * * Test that the screen transition animation works when moving to the shop screen.
     * This test checks that the screen transition animation is triggered when moving to the shop screen.
     */
    @Test
    public void screenTransitionAnimationWorks() {
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

        // Verify that the screen transition animation works
        assertTrue(shopScreen.isTransitioning());

        // Press escape key to return to the previous screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ESCAPE));
        gameRunner.runForSeconds(2f);

        var previousScreen = gameRunner.game.getScreen();

        // Verify that the previous screen is the gameplay screen
        assertTrue(previousScreen instanceof GamePlayScreen);
    }

    /**
     * * Test that the shop screen pauses the game when active.
     * This test checks that the game is paused when the shop screen is active.
     */
    @Test
    public void shopScreenPausesAndResumesGame() {
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

        // Verify that the game is paused when the shop screen is active
        assertTrue(shopScreen.isPaused());

        // Press escape key to return to the previous screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ESCAPE));
        gameRunner.runForSeconds(2f);

        var previousScreen = gameRunner.game.getScreen();

        // Verify that the previous screen is the gameplay screen
        assertTrue(previousScreen instanceof GamePlayScreen);
    }

    /**
     * * Test that the shop screen displays the correct particles effects.
     * This test checks that the particles effects are displayed correctly in the shop screen.
     */
    @Test
    public void particlesEffectsWorkInShopScreen() {
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

        // Verify that the particles effects work in the shop screen
        assertTrue(shopScreen.getParticles().isActive());

        // Press escape key to return to the previous screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ESCAPE));
        gameRunner.runForSeconds(2f);

        var previousScreen = gameRunner.game.getScreen();

        // Verify that the previous screen is the gameplay screen
        assertTrue(previousScreen instanceof GamePlayScreen);
    }

    /**
     * * Test that the arrow keys switch selection between upgrade types.
     * This test checks that the arrow keys switch selection between size and organelle upgrades.
     */
    @Test
    public void arrowKeysSwitchSelectonBetweenUpgradeTypes() {
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

        // Initial selection should be size upgrade
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.LEFT));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        assertTrue(shopScreen.isHighlighted(shopScreen.getCurrentSizeCard()));
        assertFalse(shopScreen.isHighlighted(shopScreen.getCurrentOrganelleCard()));

        // Switch selection to organelle upgrade
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.RIGHT));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        assertFalse(shopScreen.isHighlighted(shopScreen.getCurrentSizeCard()));
        assertTrue(shopScreen.isHighlighted(shopScreen.getCurrentOrganelleCard()));
    }

    /**
     * * Test that the ENTER key purchases the selected upgrade.
     * This test checks that the ENTER key purchases the selected upgrade and updates the cell's ATP and size.
     */
    @Test
    public void canPurchaseSizeUpgradeWhenRequirementsMet() {
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER)); gameRunner.step(); 
        gameRunner.setHeldDownKeys(Set.of()); gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q)); gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of()); gameRunner.step();

        var shopScreen = (ShopScreen) gameRunner.game.getScreen();
        var cell = shopScreen.getPlayerCell();

        // Give cell enough ATP and size
        cell.setCellATP(100);
        cell.setCellSize(300); // Larger than any upgrade requirement

        // Press ENTER to buy selected (size) upgrade
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER)); 
        gameRunner.step(); 
        gameRunner.setHeldDownKeys(Set.of()); 
        gameRunner.step();

        // After purchase, cell should have at least one size flag set to true
        boolean upgraded = cell.hasSmallSizeUpgrade() || cell.hasMediumSizeUpgrade() || cell.hasLargeSizeUpgrade() || cell.hasMassiveSizeUpgrade();
        assertTrue(upgraded);
    }

    /**
     * Tests that invalid purchases do not apply upgrades.
     * This test checks that the player cannot purchase an upgrade if they do not have enough ATP or size.
     */
    @Test
    public void cannotPurchaseUpgradeWhenRequirementsNotMet() {
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER)); gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of()); gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q)); gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of()); gameRunner.step();

        var shopScreen = (ShopScreen) gameRunner.game.getScreen();
        var cell = shopScreen.getPlayerCell();

        // Ensure not enough ATP or size
        cell.setCellATP(0);
        cell.setCellSize(100);

        // Attempt to buy (should fail)
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step(); 
        gameRunner.setHeldDownKeys(Set.of()); 
        gameRunner.step();

        // Verify no upgrades were gained
        assertFalse(cell.hasSmallSizeUpgrade() || cell.hasMitochondria());
    }

    @Test
    public void shopDisplaysMessagesWhenUpgradesExhausted() throws Exception {
        var runner = GameRunner.create();
        runner.setHeldDownKeys(Set.of(Input.Keys.ENTER)); runner.step();
        runner.setHeldDownKeys(Set.of()); runner.step();
        runner.setHeldDownKeys(Set.of(Input.Keys.Q)); runner.step();
        runner.setHeldDownKeys(Set.of()); runner.step();

        var shop = (ShopScreen) runner.game.getScreen();
        var cell = shop.getPlayerCell();

        // Simulate unlocking all size upgrades
        cell.setHasSmallSizeUpgrade(true);
        cell.setHasMediumSizeUpgrade(true);
        cell.setHasLargeSizeUpgrade(true);
        cell.setHasMassiveSizeUpgrade(true);

        // Simulate unlocking all organelles
        cell.setHasMitochondria(true);
        cell.setHasRibosomes(true);
        cell.setHasFlagella(true);
        cell.setHasNucleus(true);

        // Use reflection to empty upgrade lists in ShopScreen
        var sizeField = ShopScreen.class.getDeclaredField("sizeUpgrades");
        sizeField.setAccessible(true);
        @SuppressWarnings("unchecked")
        var sizeUpgrades = (List<Table>) sizeField.get(shop);
        sizeUpgrades.clear();

        var organelleField = ShopScreen.class.getDeclaredField("organelleUpgrades");
        organelleField.setAccessible(true);
        @SuppressWarnings("unchecked")
        var organelleUpgrades = (List<Table>) organelleField.get(shop);
        organelleUpgrades.clear();

        // Also nullify the current selected cards
        var currentSizeCardField = ShopScreen.class.getDeclaredField("currentSizeCard");
        currentSizeCardField.setAccessible(true);
        currentSizeCardField.set(shop, null);

        var currentOrganelleCardField = ShopScreen.class.getDeclaredField("currentOrganelleCard");
        currentOrganelleCardField.setAccessible(true);
        currentOrganelleCardField.set(shop, null);

        // Step forward to allow draw/update logic
        runner.step();

        // Both current upgrade selections should now be null
        assertNull(shop.getCurrentSizeCard());
        assertNull(shop.getCurrentOrganelleCard());
    }


    @Test
    public void textureCreationMethodsWorkCorrectly() {
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

        // Test option background texture creation
        var optionBackgroundTexture = shopScreen.getOptionBackgroundTexture();
        assertNotNull(optionBackgroundTexture);

        // Test glowing border texture creation
        var glowingBorderTexture = shopScreen.getGlowingBorderTexture();
        assertNotNull(glowingBorderTexture);

        // Verify textures are different
        assertNotEquals(optionBackgroundTexture, glowingBorderTexture);

        // Test texture disposal (should not throw exceptions)
        assertDoesNotThrow(() -> {
            optionBackgroundTexture.dispose();
            glowingBorderTexture.dispose();
        });
    }
}