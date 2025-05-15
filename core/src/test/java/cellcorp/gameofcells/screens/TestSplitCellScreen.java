package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.runner.GameRunner;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class TestSplitCellScreen {
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

        GL20 gl20 = Mockito.mock(GL20.class);
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;

        SpriteBatch spriteBatch = Mockito.mock(SpriteBatch.class);
        Mockito.when(spriteBatch.isDrawing()).thenReturn(false);
    }

    private void moveToSplitCellScreen(GameRunner runner) {
        runner.moveToGameplayScreen();

        // Use `n` debug key to give the cell its nucleus
        runner.setHeldDownKeys(Set.of(Input.Keys.N));
        runner.step();

        // Dismiss the pop-up
        runner.setHeldDownKeys(Set.of(Input.Keys.SPACE));
        runner.step();

        // Now that we have the upgrade, split the cell with `u`
        runner.setHeldDownKeys(Set.of(Input.Keys.U));
        runner.step();
    }

    @Test
    public void canMoveToCellSplitScreen() {
        var runner = GameRunner.create();
        moveToSplitCellScreen(runner);

        assertInstanceOf(SplitCellScreen.class, runner.game.getScreen());
    }
}
