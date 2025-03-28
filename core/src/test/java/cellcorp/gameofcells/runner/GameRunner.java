package cellcorp.gameofcells.runner;

import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.providers.FakeGraphicsProvider;
import cellcorp.gameofcells.providers.FakeInputProvider;
import cellcorp.gameofcells.Main;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Set;

public class GameRunner {
    public static final int TICKS_PER_SECOND = 60;
    public static final float DELTA_TIME = 1 / (float) TICKS_PER_SECOND;

    public final Main game;
    public final FakeInputProvider inputProvider;
    public ConfigProvider configProvider;

    private int ticksElapsed = 0;

    /**
     * Constructs a new `GameRunner`.
     * This is a
     * <a href=https://en.wikipedia.org/wiki/Factory_method_pattern>"factory method"</a>,
     * although here we just use it to make local variables in the constructor,
     * and do some initialization of the `Main`, not for the reasons they list.
     * @return The new GameRunner
     */
    public static GameRunner create() {
        var inputProvider = new FakeInputProvider();
        var graphicsProvider = new FakeGraphicsProvider();
        var assetManager = Mockito.mock(AssetManager.class);
        var camera = Mockito.mock(OrthographicCamera.class);
        var viewport = Mockito.mock(FitViewport.class);
        var ConfigProvider = new ConfigProvider();
        var game = new Main(inputProvider, graphicsProvider, assetManager, camera, viewport,ConfigProvider);
        game.create();
        return new GameRunner(game, inputProvider);
    }

    public GameRunner(Main game, FakeInputProvider inputProvider) {
        this.game = game;
        this.inputProvider = inputProvider;
    }

    /**
     * Step the game forward a tick.
     * Like {@link Main#render()}, but won't crash your test.
     */
    public void step() {
        game.handleInput(DELTA_TIME);
        game.update(DELTA_TIME);
        ticksElapsed += 1;
    }

    public void runForSeconds(float seconds) {
        int steps = (int)seconds * TICKS_PER_SECOND;
        for (int i = 0; i < steps; i++) {
            step();
        }
    }

    /**
     *
     * Set the keys that are currently held down.
     * Subsequent calls to this method erase previously held-down keys.
     * @param keys The set of held-down keys.
     *  Gdx represents these as ints, but use `Input.Keys.SOME_KEY_NAME` to name them.
     */
    public void setHeldDownKeys(Set<Integer> keys) {
        inputProvider.setHeldDownKeys(keys);
    }

    /**
     * Gets the number of "game ticks" that have elapsed since game start.
     * A "game tick" is a single `render()` cycle -- handling input, updating, and (except in tests) drawing.
     */
    public int getTicksElapsed() {
        return this.ticksElapsed;
    }
}
