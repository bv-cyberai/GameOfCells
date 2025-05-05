package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.runner.GameRunner;
import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestMainMenuScreen {

    @BeforeAll
    public static void setUpLibGDX() {
        System.setProperty("com.badlogic.gdx.backends.headless.disableNativesLoading", "true");
        // Initialize headless LibGDX
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(
                new ApplicationListener() {
                    @Override
                    public void create() {
                    }

                    @Override
                    public void resize(int width, int height) {
                    }

                    @Override
                    public void render() {
                    }

                    @Override
                    public void pause() {
                    }

                    @Override
                    public void resume() {
                    }

                    @Override
                    public void dispose() {
                    }
                }, config
        );

        // Mock the graphics provider
        Gdx.graphics = Mockito.mock(Graphics.class);
        Mockito.when(Gdx.graphics.getWidth()).thenReturn(Main.DEFAULT_SCREEN_WIDTH);
        Mockito.when(Gdx.graphics.getHeight()).thenReturn(Main.DEFAULT_SCREEN_HEIGHT);

        // Mock Gdx.app for exit
        Gdx.app = Mockito.mock(Application.class);
        Mockito.doNothing().when(Gdx.app).exit();

        GL20 gl20 = Mockito.mock(GL20.class);
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
    }

    @Test
    public void testMainMenuScreenInitialState() {
        // Create a game runner and get the main menu screen
        GameRunner gameRunner = GameRunner.create();
        var screen = gameRunner.game.getScreen();
        assertInstanceOf(MainMenuScreen.class, screen);

        MainMenuScreen mainMenuScreen = (MainMenuScreen) screen;

        // Verify initial state
        assertEquals(0, mainMenuScreen.getSelectedOption());
        assertEquals(0f, mainMenuScreen.getInactivityTimer());
        assertEquals(4, mainMenuScreen.getMenuOptionCount());
        assertEquals(20f, mainMenuScreen.getInactivityTimeout());
        assertNotNull(mainMenuScreen.getParticles());
        assertNotNull(mainMenuScreen.getViewport());
        assertNotNull(mainMenuScreen.getInputProvider());
        assertNotNull(mainMenuScreen.getGame());
        assertNotNull(mainMenuScreen.getAssetManager());
    }

    @Test
    public void testMenuNavigation() {
        GameRunner gameRunner = GameRunner.create();
        MainMenuScreen mainMenuScreen = (MainMenuScreen) gameRunner.game.getScreen();

        // Initial selection should be 0 (Start Game)
        assertEquals(0, mainMenuScreen.getSelectedOption());

        // Press DOWN key - should move to option 3 (Game Info)
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of()); // Release key
        gameRunner.step();
        assertEquals(2, mainMenuScreen.getSelectedOption());

        // Press DOWN key again - should move to option 4 (Exit)
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of()); // Release key
        gameRunner.step();
        assertEquals(3, mainMenuScreen.getSelectedOption());

        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of()); // Release key
        gameRunner.step();
        assertEquals(3, mainMenuScreen.getSelectedOption()); // Still be exit option

        gameRunner.setHeldDownKeys(Set.of(Input.Keys.UP));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of()); // Release key
        gameRunner.step();
        assertEquals(2, mainMenuScreen.getSelectedOption()); // Should be on Game Info option

        gameRunner.setHeldDownKeys(Set.of(Input.Keys.UP));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of()); // Release key
        gameRunner.step();
        assertEquals(0, mainMenuScreen.getSelectedOption()); // Should be on Start Game option

        // Test alternate keys (W/S for UP/DOWN)
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.W));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of()); // Release key
        gameRunner.step();
        assertEquals(0, mainMenuScreen.getSelectedOption());

        gameRunner.setHeldDownKeys(Set.of(Input.Keys.S));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of()); // Release key
        gameRunner.step();
        assertEquals(2, mainMenuScreen.getSelectedOption());
    }

    @Test
    public void testMainMenuSelection() {
        GameRunner gameRunner = GameRunner.create();
        MainMenuScreen mainMenuScreen = (MainMenuScreen) gameRunner.game.getScreen();

        // Test selecting "Start Game" (option 0)
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());

        // Reset to main menu
        gameRunner.game.setScreen(mainMenuScreen);

        // Move to "Game Info" (option 1)
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Test selecting "Game Info"
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        assertInstanceOf(GameInfoControlsScreen.class, gameRunner.game.getScreen());

        // Reset to main menu - IMPORTANT: Create fresh instance
        mainMenuScreen = new MainMenuScreen(
                gameRunner.inputProvider,
                gameRunner.game.getGraphicsProvider(),
                gameRunner.game,
                gameRunner.game.getAssetManager(),
                gameRunner.game.getCamera(),
                gameRunner.game.getViewport(),
                gameRunner.configProvider
        );
        gameRunner.game.setScreen(mainMenuScreen);

        // Move to "Exit" (option 2)
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Test exit - should remain on main menu screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());
    }

    @Test
    public void testSpaceKeySelection() {
        GameRunner gameRunner = GameRunner.create();
        MainMenuScreen mainMenuScreen = (MainMenuScreen) gameRunner.game.getScreen();

        // Test selecting "Start Game" with SPACE key
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        gameRunner.step();
        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());

        // Reset to main menu
        gameRunner.game.setScreen(mainMenuScreen);

        // Move to "Game Info" (option 1)
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Test selecting "Game Info" with SPACE key
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        gameRunner.step();
        assertInstanceOf(GameInfoControlsScreen.class, gameRunner.game.getScreen());
    }

    @Test
    public void testInactivityTimer() {
        GameRunner gameRunner = GameRunner.create();
        MainMenuScreen mainMenuScreen = (MainMenuScreen) gameRunner.game.getScreen();

        // Initial inactivity timer should be 0
        assertEquals(0f, mainMenuScreen.getInactivityTimer());

        // Run for 10 seconds without input - timer should increase
        gameRunner.runForSeconds(10f);
        assertTrue(mainMenuScreen.getInactivityTimer() >= 10f);

        // Simulate key press to reset timer
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ANY_KEY));
        gameRunner.step();

        // Inactivity timer should be very small (one frame's worth)
        assertTrue(mainMenuScreen.getInactivityTimer() < 0.1f);

        // Run for more than timeout (20s) - should transition to AttractScreen
        gameRunner.runForSeconds(21f);
        assertInstanceOf(AttractScreen.class, gameRunner.game.getScreen());
    }

    @Test
    public void testWASDNavigation() {
        GameRunner gameRunner = GameRunner.create();
        MainMenuScreen mainMenuScreen = (MainMenuScreen) gameRunner.game.getScreen();

        // Test W key for up navigation
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.W));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertEquals(0, mainMenuScreen.getSelectedOption());

        // Test S key for down navigation
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.S));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertEquals(2, mainMenuScreen.getSelectedOption()); // 2 because skip load game

        // Test S key for down navigation again
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.S));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertEquals(3, mainMenuScreen.getSelectedOption());

        // Test W key for up navigation
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.W));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertEquals(2, mainMenuScreen.getSelectedOption());
    }

    @Test
    public void testEscapeKeyDoesNothing() {
        GameRunner gameRunner = GameRunner.create();
        MainMenuScreen mainMenuScreen = (MainMenuScreen) gameRunner.game.getScreen();

        // Press ESC key - should not change screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ESCAPE));
        gameRunner.step();
        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());

        // Should still be on main menu with same selection
        assertEquals(0, mainMenuScreen.getSelectedOption());
    }

    @Test
    public void testMenuWrapAround() {
        GameRunner gameRunner = GameRunner.create();
        MainMenuScreen mainMenuScreen = (MainMenuScreen) gameRunner.game.getScreen();

        gameRunner.setHeldDownKeys(Set.of(Input.Keys.UP));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertEquals(0, mainMenuScreen.getSelectedOption()); // Should not wrap around

        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertEquals(3, mainMenuScreen.getSelectedOption()); // Should be on exit option

        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertEquals(3, mainMenuScreen.getSelectedOption()); // Should not wrap around
    }
}
