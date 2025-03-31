package cellcorp.gameofcells.screens;


import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.runner.GameRunner;

import org.mockito.Mockito;

public class TestGameInfoControlsScreen {

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
     * Test the initial state of the GameInfoControlsScreen.
     * This test checks that the screen is displayed correctly and that the
     * selected option is set to the first option (0).
     */
    @Test
    public void TestGameInfoControlsScreenInitialState() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        var gameInfoControlsScreen = gameRunner.game.getScreen();
        assertInstanceOf(GameInfoControlsScreen.class, gameInfoControlsScreen);

        GameInfoControlsScreen gameInfoControlsScreen2 = (GameInfoControlsScreen) gameInfoControlsScreen;

        // Check that the screen is displayed correctly
        assertEquals("Game Info:\n" +
                "Welcome to Game of Cells!\n" +
                "Control a cell and explore the microscopic world.\n" +
                "Controls:\n" +
                "Arrow Keys - Move the cell\n" +
                "Enter - Select/Confirm\n" +
                "Escape - Pause/Return to Menu\n\n" +
                "Press any key to return...", gameInfoControlsScreen2.getMessage()
        );
    }

    /**
     * Test that the GameInfoControlsScreen handles input correctly.
     * This test checks that when a key is pressed, the screen transitions back to the settings screen.
     * It verifies that the screen is no longer the GameInfoControlsScreen after the key press.
     */
    @Test
    public void TestGameInfoControlsScreenHandleInput() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        var gameInfoControlsScreen = gameRunner.game.getScreen();
        assertInstanceOf(GameInfoControlsScreen.class, gameInfoControlsScreen);

        GameInfoControlsScreen gameInfoControlsScreen2 = (GameInfoControlsScreen) gameInfoControlsScreen;

        // Simulate pressing any key returns to the settings screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ANY_KEY));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Verify that the screen has transitioned back to the settings screen
        assertNotEquals(gameInfoControlsScreen2, gameRunner.game.getScreen());
    }

    @Test
    public void TestGameInfoControlsScreenDispose() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        var gameInfoControlsScreen = gameRunner.game.getScreen();
        assertInstanceOf(GameInfoControlsScreen.class, gameInfoControlsScreen);

        GameInfoControlsScreen gameInfoControlsScreen2 = (GameInfoControlsScreen) gameInfoControlsScreen;

        // Dispose of the screen
        gameInfoControlsScreen2.dispose();

        // Verify that the screen is disposed correctly
        assertDoesNotThrow(() -> {
            gameInfoControlsScreen2.getSpriteBatch().dispose();
        });
        assertDoesNotThrow(() -> {
            gameInfoControlsScreen2.getParticles().dispose();
        });

    }
    
}
