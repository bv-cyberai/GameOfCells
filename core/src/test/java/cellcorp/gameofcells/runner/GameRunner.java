package cellcorp.gameofcells.runner;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.providers.FakeGraphicsProvider;
import cellcorp.gameofcells.providers.FakeInputProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;
import cellcorp.gameofcells.screens.MainMenuScreen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Runs the game with the given inputs, for the given amounts of time.
 */
public class GameRunner {
    public static final int TICKS_PER_SECOND = 60;
    public static final float DELTA_TIME = 1 / (float) TICKS_PER_SECOND;

    public final Main game;
    public final FakeInputProvider inputProvider;
    public ConfigProvider configProvider;

    private int ticksElapsed = 0;

    /**
     * Creates a GameRunner.
     */
    public GameRunner(Main game, FakeInputProvider inputProvider) {
        this.game = game;
        this.inputProvider = inputProvider;
    }

    /**
     * Constructs a new `GameRunner`.
     * This is a
     * <a href=https://en.wikipedia.org/wiki/Factory_method_pattern>"factory method"</a>,
     * although here we just use it to make local variables in the constructor,
     * and do some initialization of the `Main`, not for the reasons they list.
     *
     * @return The new GameRunner
     */
    public static GameRunner create() {
        var inputProvider = new FakeInputProvider();
        var graphicsProvider = new FakeGraphicsProvider();
        var assetManager = Mockito.mock(AssetManager.class);

        var font = Mockito.mock(BitmapFont.class);
        var fontData = Mockito.mock(BitmapFont.BitmapFontData.class);
        Mockito.when(font.getData()).thenReturn(fontData);
        Mockito.when(font.getColor()).thenReturn(Color.WHITE);

        Mockito.when(assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class)).thenReturn(font);
        Mockito.when(assetManager.get("rubik.fnt", BitmapFont.class)).thenReturn(font);

        Mockito.when(assetManager.get(AssetFileNames.WASD_ARROWS_ICON, Texture.class)).thenReturn(Mockito.mock(Texture.class));
        Mockito.when(assetManager.get(AssetFileNames.SPACE_ENTER_ICON, Texture.class)).thenReturn(Mockito.mock(Texture.class));

        var camera = Mockito.mock(OrthographicCamera.class);
        var viewport = Mockito.mock(FitViewport.class);
        var configProvider = new ConfigProvider();
        var game = new Main(inputProvider, graphicsProvider, assetManager, camera, viewport, configProvider);
        game.create();
        return new GameRunner(game, inputProvider);
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

    /**
     * Run the game for a simulated number of seconds.
     */
    public void runForSeconds(float seconds) {
        int steps = (int) (seconds * TICKS_PER_SECOND);
        for (int i = 0; i < steps; i++) {
            step();
        }
    }

    /**
     * Set the keys that are currently held down.
     * Subsequent calls to this method erase previously held-down keys.
     *
     * @param keys The set of held-down keys.
     *             Gdx represents these as ints, but use `Input.Keys.SOME_KEY_NAME` to name them.
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

    /**
     * Performs the correct sequence of inputs to move from the main menu screen to the gameplay screen.
     * Returns the gameplay screen.
     */
    public GamePlayScreen moveToGameplayScreen() {
        assertInstanceOf(MainMenuScreen.class, game.getScreen());

        // Save and restore caller's held-down keys.
        var callerKeys = new HashSet<>(inputProvider.getHeldDownKeys());
        setHeldDownKeys(Set.of(Input.Keys.ENTER));
        step();
        setHeldDownKeys(callerKeys);

        assertInstanceOf(GamePlayScreen.class, game.getScreen());
        return (GamePlayScreen) game.getScreen();
    }
}
