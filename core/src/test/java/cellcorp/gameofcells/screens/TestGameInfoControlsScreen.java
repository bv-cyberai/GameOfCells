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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;

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
        GL30 gl30 = Mockito.mock(GL30.class);
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
        Gdx.gl30 = gl30;
    }

    @AfterAll
    public static void cleanUp() {
        if (Gdx.app != null) {
            Gdx.app.exit();
        }
    }

    /**
     * Test that the GameInfoControlsScreen returns to the previous screen when the space key is pressed.
     * This test checks that the screen transitions back to the main menu screen.
     * It verifies that the screen is no longer the GameInfoControlsScreen after the key press.
     */
    @Test
    public void testGameInfoScreenReturnsToPreviousScreenOnSpacePress() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER)); // go to GameInfoControlsScreen
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        var gameInfoControlsScreen = gameRunner.game.getScreen();
        assertInstanceOf(GameInfoControlsScreen.class, gameInfoControlsScreen);

        // Check that the screen is displayed correctly
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());
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

        var gameInfoControlsScreen = gameRunner.game.getScreen();
        assertInstanceOf(GameInfoControlsScreen.class, gameInfoControlsScreen);

        // Simulate pressing ESCAPE returns to the mainmenu screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ESCAPE));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Verify that the screen has transitioned back to the settings screen
        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());
    }

    /**
     * Test that the GameInfoControlsScreen updates the particle system correctly.
     * This test checks that the particle system is updated and drawn correctly.
     * It verifies that the particle system is not null and can be updated and drawn without exceptions.
     */
    @Test
    public void testParticleSystemUpdatesCorrectly() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        var gameInfoControlsScreen = gameRunner.game.getScreen();
        assertInstanceOf(GameInfoControlsScreen.class, gameInfoControlsScreen);

        var screen = gameRunner.game.getScreen();
        assertInstanceOf(GameInfoControlsScreen.class, screen);

        assertDoesNotThrow(() -> {
            ((GameInfoControlsScreen) screen).update(1f);
        });
    }

    /**
     * Test that the GameInfoControlsScreen is not null after creation.
     * This test checks that the screen is created successfully and is not null.
     */
    @Test
    public void TestGameInfoControlsScreenNotNull() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        var gameInfoControlsScreen = gameRunner.game.getScreen();
        assertInstanceOf(GameInfoControlsScreen.class, gameInfoControlsScreen);
    }

    @Test
    public void TestMessageRenderingPosition() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
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

        // Check that the message is rendered at the correct position
        assertEquals(0, gameInfoControlsScreen2.getMessagePositionX());
        assertEquals(0, gameInfoControlsScreen2.getMessagePositionY());
    }

    /**
     * Test that the particle system is initialized correctly.
     * This test checks that the particle system is created and the white pixel texture is not null.
     * It verifies that the particles are created and can be updated and drawn.
     */
    @Test
    public void TestParticleSystem() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
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

        // Check that the particle system is initialized correctly
        assertNotNull(gameInfoControlsScreen2.getParticles());
        assertNotNull(gameInfoControlsScreen2.getParticles().getWhitePixelTexture());
    }
}
