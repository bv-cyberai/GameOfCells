package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.Chunk;
import cellcorp.gameofcells.objects.Glucose;
import cellcorp.gameofcells.objects.Zone;
import cellcorp.gameofcells.runner.GameRunner;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPopupInfoScreen {
    @BeforeAll
    public static void setUpLibGDX() {
        System.setProperty("com.badlogic.gdx.backends.headless.disableNativesLoading", "true");
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
    public void testGlucosePopupAppearsOnFirstCollision() {
        var runner = GameRunner.create();
        var input = runner.inputProvider;

        input.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();
        var screen = (GamePlayScreen) runner.game.getScreen();

        // Spawn glucose, collide with it
        screen.getGlucoseManager().getGlucoses().get(new Chunk(0, 0)).add(new Glucose(screen.getAssetManager(), 0, 0));
        runner.step();

        assertTrue(screen.getGlucoseCollisionPopup().wasShown());
    }

    @Test
    public void testAcidZonePopupAppearsOnFirstCollision() {
        var runner = GameRunner.create();
        var input = runner.inputProvider;

        input.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();
        var screen = (GamePlayScreen) runner.game.getScreen();

        // Spawn glucose, collide with it
        screen.getCell().setHasSmallSizeUpgrade(true);
        screen.getZoneManager().getAcidZones().put(new Chunk(0, 0), new Zone(screen.getAssetManager(), "", 0, 0));
        runner.step();

        assertTrue(screen.getAcidZonePopup().wasShown());
    }

    @Test
    public void testBasicZonePopupAppearsOnFirstCollision() {
        var runner = GameRunner.create();
        var input = runner.inputProvider;

        input.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();
        var screen = (GamePlayScreen) runner.game.getScreen();

        // Spawn glucose, collide with it
        screen.getZoneManager().getBasicZones().put(new Chunk(0, 0), new Zone(screen.getAssetManager(), "", 0, 0));
        runner.step();

        assertTrue(screen.getBasicZonePopup().wasShown());
    }

    @Test
    public void testPopupClosesOnKeyPress() {
        var runner = GameRunner.create();
        var input = runner.inputProvider;

        input.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();
        var screen = (GamePlayScreen) runner.game.getScreen();

        // Spawn glucose, collide with it
        screen.getZoneManager().getBasicZones().put(new Chunk(0, 0), new Zone(screen.getAssetManager(), "", 0, 0));
        runner.step();

        var glucosePopup = screen.getGlucoseCollisionPopup();
        input.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();
        assertFalse(glucosePopup.isVisible());
    }
}
