package cellcorp.gameofcells.screens;

import com.badlogic.gdx.Screen;

/**
 * An extension of LibGDX `Screen` class, to support direct calls to the sub-methods of `render()`.
 * All Game of Cells screens should implement this class.
 */
public interface GameOfCellsScreen extends Screen {
    /**
     * Responds to key-presses, updating game state.
     * @param deltaTimeSeconds The time passed since the last call to `handleInput`, in seconds.
     */
    void handleInput(float deltaTimeSeconds);

    /**
     * Updates game state that does not depend on input.
     * @param deltaTimeSeconds The time passed since the last call to `update`, in seconds.
     */
    void update(float deltaTimeSeconds);

    /**
     * Draws the screen.
     * This doesn't take in `deltaTime`, because this should be a "dumb" method.
     * It should use properties that have already been calculated in `handleInput` or `update`.
     * If any calculations occur in `draw()`, tests will not see them,
     * and integration tests will become out-of-sync with actual gameplay.
     */
    void draw();
}
