package cellcorp.gameofcells.screens;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.badlogic.gdx.math.Vector2;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Chunk;
import cellcorp.gameofcells.objects.Zone;
import cellcorp.gameofcells.runner.GameRunner;

public class TestGamePlayScreen {

    @BeforeAll
    public static void setUpLibGDX() {
        System.setProperty("com.badlogic.gdx.backends.headless.disableNativesLoading", "true");
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

    @Test
    public void gameStartsOnMainMenuAndTransitionsToGamePlay() {
        var gameRunner = GameRunner.create();
        
        // Verify initial state
        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());
        
        // Press enter to transition
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        
        // Verify transition
        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());
    }

    @Test
    public void cellMovementUpdatesPosition() {
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step(); // Move to gameplay
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        var initialX = gamePlayScreen.getCell().getX();
        var initialY = gamePlayScreen.getCell().getY();
        
        // Move right
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.RIGHT));
        gameRunner.step();
        
        // Verify position changed
        assertTrue(gamePlayScreen.getCell().getX() > initialX);
        assertEquals(initialY, gamePlayScreen.getCell().getY());
        
        // Move up
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.UP));
        gameRunner.step();
        
        // Verify position changed
        assertTrue(gamePlayScreen.getCell().getY() > initialY);
    }

    // @Test
    // public void pauseGameFreezesUpdates() {
    //     var gameRunner = GameRunner.create();
    //     gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
    //     gameRunner.step(); // Move to gameplay
        
    //     var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
    //     var initialX = gamePlayScreen.getCell().getX();
        
    //     // Pause the game
    //     gameRunner.setHeldDownKeys(Set.of(Input.Keys.P));
    //     gameRunner.step();
        
    //     // Try to move while paused
    //     gameRunner.setHeldDownKeys(Set.of(Input.Keys.RIGHT));
    //     gameRunner.step();
        
    //     // Position shouldn't change
    //     assertEquals(initialX, gamePlayScreen.getCell().getX());
    // }

    @Test
    public void shopScreenTransitionWorks() {
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step(); // Move to gameplay
        
        // Press Q for shop
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.Q));
        gameRunner.step();
        
        assertInstanceOf(ShopScreen.class, gameRunner.game.getScreen());
    }

    // @Test
    // public void glucoseCollisionIncreasesATP() {
    //     var gameRunner = GameRunner.create();
    //     gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
    //     gameRunner.step(); // Move to gameplay
        
    //     var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
    //     var initialATP = gamePlayScreen.getCell().getCellATP();
        
    //     // Force a glucose collision (would normally need to mock/spawn glucose)
    //     gamePlayScreen.reportGlucoseCollision();
        
    //     // ATP should increase
    //     assertTrue(gamePlayScreen.getCell().getCellATP() > initialATP);
    // }

    @Test
    public void acidZoneReducesHealth() {
        // Create game and move to gameplay screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step(); // Move to gameplay
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        var cell = gamePlayScreen.getCell();

        cell.setHasShownAcidZonePopup(true);

        var zoneManager = gamePlayScreen.getSpawnManager().getZoneManager();
        
        // Set cell to full health at position (1000,1000)
        cell.setCellHealth(100);
        cell.moveTo(1000, 1000);
        
        // Create acid zone exactly where the cell is
        Chunk testChunk = Chunk.fromWorldCoords(1000, 1000);
        Vector2 zonePos = new Vector2(1000, 1000);
        zoneManager.getAcidZones().put(testChunk, 
            new Zone(gamePlayScreen.getAssetManager(), 
                    AssetFileNames.ACID_ZONE, 
                    zonePos.x, 
                    zonePos.y));
        
        // Run for 1 second (should take damage twice at 0.5s intervals)
        gameRunner.runForSeconds(1f);
        
        // Health should be less than 100
        assertTrue(cell.getCellHealth() < 100, 
            "Cell health should decrease when in acid zone");
        
        // Should take approximately 10 damage (max damage per second)
        assertEquals(96, cell.getCellHealth(), 
            "Cell should take ~10 damage after 1 second in acid zone");
    }

    @Test
    public void gameOverTransitionWorks() {
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step(); // Move to gameplay
        
        // Press G for game over
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.G));
        gameRunner.step();
        
        assertInstanceOf(GameOverScreen.class, gameRunner.game.getScreen());
    }

    @Test
    public void screenResizeUpdatesViewport() {
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step(); // Move to gameplay
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        int testWidth = 800;
        int testHeight = 600;
        
        // Resize should not throw
        assertDoesNotThrow(() -> gamePlayScreen.resize(testWidth, testHeight));
    }

    @Test
    public void gamePlayScreenDisposeDoesNotThrow() {
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step(); // Move to gameplay
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        
        // Dispose should not throw
        assertDoesNotThrow(() -> gamePlayScreen.dispose());
    }

    @Test
    public void gamePlayScreenPauseDoesNotThrow() {
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step(); // Move to gameplay
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        
        // Pause should not throw
        assertDoesNotThrow(() -> gamePlayScreen.pause());
    }

    @Test
    public void gamePlayScreenResumeDoesNotThrow() {
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step(); // Move to gameplay
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        
        // Resume should not throw
        assertDoesNotThrow(() -> gamePlayScreen.resume());
    }

    @Test
    public void gamePlayScreenShowDoesNotThrow() {
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step(); // Move to gameplay
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        
        // Show should not throw
        assertDoesNotThrow(() -> gamePlayScreen.show());
    }
}