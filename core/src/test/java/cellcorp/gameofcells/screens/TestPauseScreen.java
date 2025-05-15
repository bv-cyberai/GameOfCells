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

public class TestPauseScreen {

    @BeforeAll
    public static void setUpLibGDX() {
        System.setProperty("com.badlogic.gdx.backends.headless.disableNativesLoading", "true");
        // Initialize headless LibGDX
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationListener() {
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
     * Test that the PauseScreen is created successfully and is not null.
     */
    @Test
    public void testPauseScreenNotNull() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.P));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        assertInstanceOf(PauseScreen.class, gameRunner.game.getScreen());
    }

    /**
     * Test that pressing ESCAPE or P resumes the game.
     */
    @Test
    public void testPauseScreenResumesOnEscapeOrP() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.P));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        assertInstanceOf(PauseScreen.class, gameRunner.game.getScreen());

        // Test with ESCAPE
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ESCAPE));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());

        // Test with P
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.P));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertInstanceOf(PauseScreen.class, gameRunner.game.getScreen());

        gameRunner.setHeldDownKeys(Set.of(Input.Keys.P));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());
    }

    /**
     * Test that the menu navigation works correctly.
     */
    @Test
    public void testPauseScreenMenuNavigation() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.P));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        PauseScreen pauseScreen = (PauseScreen) gameRunner.game.getScreen();
        int initialSelection = pauseScreen.getMenuSystem().getSelectedOptionIndex();

        // Test down navigation
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertEquals(initialSelection + 1, pauseScreen.getMenuSystem().getSelectedOptionIndex());

        // Test up navigation
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.UP));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        assertEquals(initialSelection, pauseScreen.getMenuSystem().getSelectedOptionIndex());
    }

    /**
     * Test that selecting "Resume" returns to gameplay.
     */
    @Test
    public void testPauseScreenResumeOption() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.P));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Select "Resume" option
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        assertInstanceOf(GamePlayScreen.class, gameRunner.game.getScreen());
    }

    /**
     * Test that selecting "Controls" opens the GameInfoControlsScreen.
     */
    @Test
    public void testPauseScreenControlsOption() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.P));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Navigate to Controls option
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Select Controls option
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        assertInstanceOf(GameInfoControlsScreen.class, gameRunner.game.getScreen());
    }

    /**
     * Test that selecting "Quit to Menu" returns to the main menu.
     */
    @Test
    public void testPauseScreenQuitToMenuOption() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.P));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Navigate to Quit to Menu option
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.DOWN));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        // Select Quit to Menu option
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());
    }

    /**
     * Test that the particle system updates correctly.
     */
    @Test
    public void testPauseScreenParticleSystem() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.P));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        PauseScreen pauseScreen = (PauseScreen) gameRunner.game.getScreen();

        assertDoesNotThrow(() -> {
            pauseScreen.update(1f);
        });

        assertNotNull(pauseScreen.getParticles());
    }
}
