package cellcorp.gameofcells.objects;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.runner.GameRunner;
import cellcorp.gameofcells.screens.GameInfoControlsScreen;
import cellcorp.gameofcells.screens.SettingsScreen;

public class TestSettingsScreen {
    
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

        // Mock Gdx.app for exit
        Gdx.app = Mockito.mock(Application.class);
        Mockito.doNothing().when(Gdx.app).exit();

        GL20 gl20 = Mockito.mock(GL20.class);
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
    }


    /**
     * Test the initial state of the SettingsScreen.
     * This test checks that the screen is displayed correctly and that the
     * selected option is set to the first option (0).
     * It also verifies that the screen can handle input correctly by simulating
     * pressing the down arrow key and checking that the selected option changes.
     */
    @Test
    public void testSettingsScreenInitialState() {
        // Create a game runner and get the settings screen
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        var settingsScreen = gameRunner.game.getScreen();
        assertInstanceOf(SettingsScreen.class, settingsScreen);

        SettingsScreen settings = (SettingsScreen) settingsScreen;
        assertEquals(0, settings.getSelectedOption());

        // Simulate pressing the down arrow key
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Verify that the selected option has changed
        assertEquals(1, settings.getSelectedOption());
    }

    /**
     * Test the navigation functionality of the SettingsScreen.
     * This test checks that the user can navigate through the settings options
     * using the up and down arrow keys.
     * It verifies that the selected option changes correctly when the keys are
     * pressed.
     * It also checks that the selected option wraps around when reaching the
     * beginning or end of the options list.
     */
    @Test
    public void testSettingsScreenNavigation() {
        // Create a game runner and get the settings screen
        GameRunner gameRunner = GameRunner.create();

        // Simulate pressing the down arrow key
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        var settingsScreen = gameRunner.game.getScreen();

        // Verify that the screen is an instance of SettingsScreen
        assertInstanceOf(SettingsScreen.class, settingsScreen);

        SettingsScreen settings = (SettingsScreen) settingsScreen;

        // Simulate pressing the down arrow key
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Verify that the selected option has changed
        assertEquals(1, settings.getSelectedOption());

        // Simulate pressing the up arrow key
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.UP));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Verify that the selected option has changed back to 0
        assertEquals(0, settings.getSelectedOption());

        // Simulate pressing the up arrow key again
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.UP));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Verify that the selected option has wrapped around to the last option
        assertEquals(1, settings.getSelectedOption());
    }

    /**
     * Test the exit functionality of the SettingsScreen.
     * This test checks that when the "Back" option is selected and the enter key
     * is pressed, the screen exits and returns to the main menu.
     * It also verifies that the screen is no longer an instance of SettingsScreen
     * after the exit.
     */
    @Test
    public void testSettingsScreenExit() {
        // Create a game runner and get the settings screen
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        var settingsScreen = gameRunner.game.getScreen();

        // Verify that the screen is an instance of SettingsScreen
        assertInstanceOf(SettingsScreen.class, settingsScreen);

        SettingsScreen settings = (SettingsScreen) settingsScreen;

        // Simulate pressing the enter key to exit
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Verify that the screen has exited
        assertNotEquals(settings, gameRunner.game.getScreen());
    }

    /**
     * Test the functionality of the SettingsScreen when the "Game Info & Controls"
     * option is selected.
     * This test checks that when the "Game Info & Controls" option is selected and
     * the enter key is pressed, the screen transitions to the GameInfoControlsScreen.
     * It also verifies that the screen is no longer an instance of SettingsScreen
     * after the transition.
     */
    @Test
    public void testSettingsScreenGameInfoControls() {
        // Create a game runner and get the settings screen
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        var settingsScreen = gameRunner.game.getScreen();

        // Verify that the screen is an instance of SettingsScreen
        assertInstanceOf(SettingsScreen.class, settingsScreen);

        SettingsScreen settings = (SettingsScreen) settingsScreen;

        // Simulate pressing the enter key to select "Game Info & Controls"
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        GameInfoControlsScreen gameInfoControlsScreen = (GameInfoControlsScreen) gameRunner.game.getScreen();

        // Verify that the screen has transitioned to GameInfoControlsScreen
        assertNotEquals(settings, gameRunner.game.getScreen());
        assertEquals(gameInfoControlsScreen, gameRunner.game.getScreen());
    }
}
