package cellcorp.gameofcells.screens;

/*
 * @author Brendon Vinyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 *
 * @date 02/27/2025
 * @course CIS 405
 * @assignment GameOfCells
 */

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.providers.FakeGraphicsProvider;
import cellcorp.gameofcells.providers.FakeInputProvider;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

/// Tests for [GamePlayScreen]
public class TestGamePlayScreen {

    // Test that the game starts at the start screen.
    // This test creates a new GamePlayScreen instance and checks that the message is correct.
    @Test
    public void gameStartsAtStartScreen() {
        // Create a new GamePlayScreen instance with mock dependencies.
        var inputProvider = new FakeInputProvider();
        var graphicsProvider = new FakeGraphicsProvider();
        var game = Mockito.mock(Main.class);
        var assetManager = Mockito.mock(AssetManager.class);
        var camera = Mockito.mock(OrthographicCamera.class);
        var viewport = Mockito.mock(FitViewport.class);
        var screen = new GamePlayScreen(inputProvider, graphicsProvider, game, assetManager, camera, viewport);

        // Check that the game initially starts at the start screen with the correct message.
        assertEquals("Press Enter to Start", screen.getMessage(), "Expected start message");
    }

    // deprecating test
    // @Test
    // public void pressingSpaceOnceChangesText() {
    //     // Create a new GamePlayScreen, giving it our input provider to use, instead of the default one.
    //     var inputProvider = new FakeInputProvider();
    //     // `GamePlayScreen` expects a game, assetManager, camera, and viewport.
    //     // I claim that none of these should affect the outcome of this test,
    //     // so I use Mockito to create fake versions of each, which return nothing.
    //     // For more information, see the class testing book or ask Mark.
    //     var game = Mockito.mock(Main.class);
    //     var assetManager = Mockito.mock(AssetManager.class);
    //     var camera = Mockito.mock(OrthographicCamera.class);
    //     var viewport = Mockito.mock(FitViewport.class);
    //     var screen = new GamePlayScreen(game, assetManager, inputProvider, camera, viewport);
    //     // Hold down space, and call update once.
    //     inputProvider.setHeldDownKeys(Set.of(
    //         Input.Keys.SPACE,
    //         Input.Keys.RIGHT // It should be fine to hold down another key as well.
    //     ));
    //     // Screen.render expects a deltaTime -- the amount of time that has passed.
    //     // Let's say it was 1 second
    //     var delta_time = 1.0f;
    //     // In actual game code, `screen.render` is called, which calls `handleInput`, `update`, and `draw`.
    //     // To match that, we'll always call `screen.update` right after `screen.handleInput`.
    //     screen.handleInput(delta_time);
    //     screen.update(delta_time);
    //     // Reset held down keys, then call update a few more times.
    //     inputProvider.setHeldDownKeys(Set.of());
    //     screen.handleInput(delta_time);
    //     screen.update(delta_time);
    //     screen.handleInput(delta_time);
    //     screen.update(delta_time);
    //     // Test that the game screen is a `GamePlayScreen`, and that the message has been updated.
    //     assertEquals("Spacebar was pressed!", screen.getMessage());
    // }
}
