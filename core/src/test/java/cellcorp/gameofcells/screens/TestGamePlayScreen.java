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

import cellcorp.gameofcells.FakeInputProvider;
import cellcorp.gameofcells.Main;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/// Tests for [GamePlayScreen]
public class TestGamePlayScreen {

    @Test
    public void pressingEnterStartsGame() {
        // Create a new GamePlayScreen, giving it our input provider to use, instead of the default one.
        var inputProvider = new FakeInputProvider();
        // `GamePlayScreen` expects a game, camera, and viewport.
        // I claim that none of these should affect the outcome of this test,
        // so I use Mockito to create fake versions of each, which return nothing.
        // For more information, see the class testing book or ask Mark.
        var game = Mockito.mock(Main.class);
        var camera = Mockito.mock(OrthographicCamera.class);
        var viewport = Mockito.mock(FitViewport.class);
        var screen = new GamePlayScreen(game, inputProvider, camera, viewport);

        // Initially, check that the start screen message is shown.
        assertTrue(screen.getMessage().equals("Press Enter to Start"), "Expected start message");

        // Simulate pressing ENTER to start the game.
        inputProvider.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        screen.handleInput();   // Process input (ENTER IS PRESSED)
        screen.update(0.016f);        // Update the screen

        // Now, check that the message has change to the game-playing message.
        assertTrue(screen.getMessage().equals("Game is now playing..."), "Expected game message");
    }
}
