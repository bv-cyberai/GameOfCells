package cellcorp.gameofcells.screens;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.runner.GameRunner;

import org.mockito.Mockito;

public class TestAttractScreen {

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
    public void TestAttractScreenIntialState() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.runForSeconds(21f);

        var attractScreen = gameRunner.game.getScreen();
        assertTrue(attractScreen instanceof AttractScreen, "AttractScreen should be the current screen");
    }

    // Test to check if the simulation is running
    @Test
    public void TestAttractScreenSimulationRunning() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.runForSeconds(21f);

        var attractScreen = (AttractScreen) gameRunner.game.getScreen();
        assertTrue(attractScreen.isSimulationRunning(), "AttractScreen should be in simulation mode");
    }

    // Test to check if the cell moves towards the target
    // This test will run the game for a few seconds and check if the cell has moved.
    @Test
    public void testCellMovesTowardTarget() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.runForSeconds(21f);

        var attractScreen = (AttractScreen) gameRunner.game.getScreen();
        var cell = attractScreen.getCell();

        // Manually move the cell to a known position
        cell.moveTo(0f, 0f);

        // Manually set a far target position
        // Using reflection since targetX/Y are private
        try {
            java.lang.reflect.Field targetXField = AttractScreen.class.getDeclaredField("targetX");
            java.lang.reflect.Field targetYField = AttractScreen.class.getDeclaredField("targetY");
            targetXField.setAccessible(true);
            targetYField.setAccessible(true);
            targetXField.setFloat(attractScreen, 500f);
            targetYField.setFloat(attractScreen, 500f);
        } catch (Exception e) {
            fail("Failed to set targetX/Y via reflection: " + e.getMessage());
        }

        float initialX = cell.getX();
        float initialY = cell.getY();

        // Simulate movement
        attractScreen.update(1f); // 1 second

        float newX = cell.getX();
        float newY = cell.getY();

        boolean movedX = Math.abs(newX - initialX) > 1e-3;
        boolean movedY = Math.abs(newY - initialY) > 1e-3;

        assertTrue(movedX || movedY, "Cell should have moved towards target");
    }

    // Test to check if the glucose count is correct
    // This test will run the game for a few seconds and check if the glucose list size is 10.
    @Test
    public void testGlucoseCountIsCorrect() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.runForSeconds(21f);

        var attractScreen = (AttractScreen) gameRunner.game.getScreen();
        assertEquals(10, attractScreen.getGlucoseList().size(), "There should be 10 glucose objects initialized");
    }

    // Test to check if the animation time is incremented correctly
    // This test will run the game for a few seconds and check if the animation time has increased.
    @Test
    public void testAnimationTimeIncrements() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.runForSeconds(21f);

        var attractScreen = (AttractScreen) gameRunner.game.getScreen();
        float initialTime = attractScreen.getAnimationTime();

        attractScreen.update(0.5f);
        float laterTime = attractScreen.getAnimationTime();

        assertTrue(laterTime > initialTime, "Animation time should have increased after update");
    }

    // Test to check if the simulation stops when the screen is hidden
    @Test
    public void testSimulationStopsOnHide() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.runForSeconds(21f);

        var attractScreen = (AttractScreen) gameRunner.game.getScreen();
        attractScreen.hide();

        assertFalse(attractScreen.isSimulationRunning(), "Simulation should stop after hide() is called");
    }

    // Test to check if the target changes when the cell reaches it
    // This test is a bit tricky because the target is set randomly in the original code.
    // We will set a known target and check if it changes after the cell reaches it.
    // We will use reflection to set the targetX and targetY fields directly.
    // This is not ideal, but it's necessary because the original code doesn't provide a way to set the target directly.
    @Test
    public void testTargetChangesWhenReached() {
        GameRunner gameRunner = GameRunner.create();
        gameRunner.runForSeconds(21f);

        var attractScreen = (AttractScreen) gameRunner.game.getScreen();
        var cell = attractScreen.getCell();

        // Set a known target
        float forcedTargetX = 300f;
        float forcedTargetY = 300f;

        try {
            java.lang.reflect.Field targetXField = AttractScreen.class.getDeclaredField("targetX");
            java.lang.reflect.Field targetYField = AttractScreen.class.getDeclaredField("targetY");
            targetXField.setAccessible(true);
            targetYField.setAccessible(true);
            targetXField.setFloat(attractScreen, forcedTargetX);
            targetYField.setFloat(attractScreen, forcedTargetY);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }

        // Move the cell very close to target (within 5 units)
        cell.moveTo(forcedTargetX - 2f, forcedTargetY - 2f);

        float oldTargetX = attractScreen.getTargetX();
        float oldTargetY = attractScreen.getTargetY();

        // This should trigger a target update due to proximity
        attractScreen.update(0.1f);

        float newTargetX = attractScreen.getTargetX();
        float newTargetY = attractScreen.getTargetY();

        boolean changed = Math.abs(newTargetX - oldTargetX) > 1e-3 || Math.abs(newTargetY - oldTargetY) > 1e-3;
        assertTrue(changed, "Target should change after cell reaches it");
    }
}
