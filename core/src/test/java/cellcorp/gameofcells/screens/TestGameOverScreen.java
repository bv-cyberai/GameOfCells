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
import cellcorp.gameofcells.objects.Stats;
import cellcorp.gameofcells.runner.GameRunner;

import org.mockito.Mockito;

public class TestGameOverScreen {

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
     * Test that the GameOverScreen is initialized correctly.
     */
    @Test
    public void testScreenInitialization() {
        GameRunner gameRunner = GameRunner.create();
        Stats stats = new Stats();
        GameOverScreen gameOverScreen = new GameOverScreen(
            gameRunner.inputProvider,
            gameRunner.game.getAssetManager(),
            gameRunner.game.getGraphicsProvider(),
            gameRunner.game,
            gameRunner.configProvider,
            stats
        );

        assertNotNull(gameOverScreen);
        assertEquals(stats, gameOverScreen.getStats());
    }

    /**
     * Test that the GameOverScreen is created successfully and is not null.
     */
    @Test
    public void testGameOverScreenNotNull() {
        GameRunner gameRunner = GameRunner.create();
        // Normally we'd simulate game over condition, but for testing we'll directly set the screen
        Stats stats = new Stats();
        GameOverScreen gameOverScreen = new GameOverScreen(
            gameRunner.inputProvider,
            gameRunner.game.getAssetManager(),
            gameRunner.game.getGraphicsProvider(),
            gameRunner.game,
            gameRunner.configProvider,
            stats
        );
        gameRunner.game.setScreen(gameOverScreen);

        assertInstanceOf(GameOverScreen.class, gameRunner.game.getScreen());
    }

    /**
     * Test that pressing SPACE or R returns to the main menu.
     */
    @Test
    public void testGameOverScreenReturnsToMainMenuOnSpaceOrR() {
        GameRunner gameRunner = GameRunner.create();
        Stats stats = new Stats();
        GameOverScreen gameOverScreen = new GameOverScreen(
            gameRunner.inputProvider,
            gameRunner.game.getAssetManager(),
            gameRunner.game.getGraphicsProvider(),
            gameRunner.game,
            gameRunner.configProvider,
            stats
        );
        gameRunner.game.setScreen(gameOverScreen);

        assertInstanceOf(GameOverScreen.class, gameRunner.game.getScreen());

        // Test with SPACE
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());

        // Set back to GameOverScreen to test R key
        gameRunner.game.setScreen(gameOverScreen);
        assertInstanceOf(GameOverScreen.class, gameRunner.game.getScreen());

        // Test with R
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.R));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of());
        gameRunner.step();

        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());
    }

    /**
     * Test that the stats are displayed correctly.
     */
    @Test
    public void testGameOverScreenStatsDisplay() {
        GameRunner gameRunner = GameRunner.create();
        Stats stats = new Stats();
        // Set some test stats
        stats.gameTimer = 120.5f;
        stats.glucoseCollected = 42;
        stats.atpGenerated = 100;
        stats.distanceMoved = 1500;
        stats.setSize(3); // Large
        stats.organellesPurchased = 5;

        GameOverScreen gameOverScreen = new GameOverScreen(
            gameRunner.inputProvider,
            gameRunner.game.getAssetManager(),
            gameRunner.game.getGraphicsProvider(),
            gameRunner.game,
            gameRunner.configProvider,
            stats
        );
        gameRunner.game.setScreen(gameOverScreen);

        // Since we can't directly test rendering in headless mode,
        // we'll verify the stats object is properly passed through
        assertSame(stats, gameOverScreen.getStats());
        assertEquals(120.5f, gameOverScreen.getStats().gameTimer);
        assertEquals(42, gameOverScreen.getStats().glucoseCollected);
        assertEquals(100, gameOverScreen.getStats().atpGenerated);
        assertEquals(1500, gameOverScreen.getStats().distanceMoved);
        assertEquals(3, gameOverScreen.getStats().getSize());
        assertEquals(5, gameOverScreen.getStats().organellesPurchased);
    }

    /**
     * Test that the screen handles input without exceptions.
     */
    @Test
    public void testGameOverScreenHandleInput() {
        GameRunner gameRunner = GameRunner.create();
        Stats stats = new Stats();
        GameOverScreen gameOverScreen = new GameOverScreen(
            gameRunner.inputProvider,
            gameRunner.game.getAssetManager(),
            gameRunner.game.getGraphicsProvider(),
            gameRunner.game,
            gameRunner.configProvider,
            stats
        );

        assertDoesNotThrow(() -> {
            gameOverScreen.handleInput(0.016f);
        });
    }

    /**
     * Test that the screen can be resized without exceptions.
     */
    @Test
    public void testGameOverScreenResize() {
        GameRunner gameRunner = GameRunner.create();
        Stats stats = new Stats();
        GameOverScreen gameOverScreen = new GameOverScreen(
            gameRunner.inputProvider,
            gameRunner.game.getAssetManager(),
            gameRunner.game.getGraphicsProvider(),
            gameRunner.game,
            gameRunner.configProvider,
            stats
        );

        assertDoesNotThrow(() -> {
            gameOverScreen.resize(800, 600);
        });
    }

    /**
     * Test that the screen handles edge cases in stats correctly.
     * This includes cases where stats are zero or negative.
     */
    @Test
    public void testEdgeCaseStats() {
        GameRunner gameRunner = GameRunner.create();
        Stats stats = new Stats();
        // Set some edge case stats
        stats.gameTimer = 0f;
        stats.glucoseCollected = 0;
        stats.atpGenerated = 0;
        stats.distanceMoved = 0;
        stats.setSize(0); // Tiny
        stats.organellesPurchased = 0;

        GameOverScreen gameOverScreen = new GameOverScreen(
            gameRunner.inputProvider,
            gameRunner.game.getAssetManager(),
            gameRunner.game.getGraphicsProvider(),
            gameRunner.game,
            gameRunner.configProvider,
            stats
        );
        gameRunner.game.setScreen(gameOverScreen);

        assertSame(stats, gameOverScreen.getStats());
        assertEquals(0f, gameOverScreen.getStats().gameTimer);
        assertEquals(0, gameOverScreen.getStats().glucoseCollected);
        assertEquals(0, gameOverScreen.getStats().atpGenerated);
        assertEquals(0, gameOverScreen.getStats().distanceMoved);
        assertEquals(0, gameOverScreen.getStats().getSize());
        assertEquals(0, gameOverScreen.getStats().organellesPurchased);
    }

    /**
     * Test that the screen can be disposed without exceptions.
     */
    @Test
    public void testGameOverScreenDispose() {
        GameRunner gameRunner = GameRunner.create();
        Stats stats = new Stats();
        GameOverScreen gameOverScreen = new GameOverScreen(
            gameRunner.inputProvider,
            gameRunner.game.getAssetManager(),
            gameRunner.game.getGraphicsProvider(),
            gameRunner.game,
            gameRunner.configProvider,
            stats
        );

        assertDoesNotThrow(() -> {
            gameOverScreen.dispose();
        });
    }

    /**
     * Test that the screen can be updated without exceptions.
     * This test is not very meaningful in headless mode, but we include it for completeness.
     */
    @Test
    public void testGameOverScreenUpdate() {
        GameRunner gameRunner = GameRunner.create();
        Stats stats = new Stats();
        GameOverScreen gameOverScreen = new GameOverScreen(
            gameRunner.inputProvider,
            gameRunner.game.getAssetManager(),
            gameRunner.game.getGraphicsProvider(),
            gameRunner.game,
            gameRunner.configProvider,
            stats
        );

        assertDoesNotThrow(() -> {
            gameOverScreen.update(0.016f);
        });
    }

}