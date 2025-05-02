package cellcorp.gameofcells.providers;

/*
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 *
 * @date 02/27/2025
 * @course CIS 405
 * @assignment GameOfCells
 */

import com.badlogic.gdx.Input;

import java.util.Set;

/// Fake [InputProvider] for use in tests.
///
/// To test behavior when a key is held down, pass a [FakeInputProvider]
/// to the game object under test, call [#setHeldDownKeys],
/// then call the method(s) under test.
public class FakeInputProvider implements InputProvider {

    /// Set of keys which are currently held down, according to our test.
    private Set<Integer> heldDown = Set.of();

    /// Set the keys that are currently held down.
    /// Subsequent calls to this method erase previously held-down keys.
    ///
    /// The most recent set of held-down keys will be returned from both
    /// `FakeInputProvider.isKeyPressed()` and `FakeInputProvider.isKeyJustPressed`
    ///
    /// Example:
    /// ```java
    /// // Hold down space and right-arrow keys.
    /// fakeInputProvider.setHeldDownKeys(Set.of(
    ///     Input.keys.SPACE,
    ///     Input.keys.RIGHT
    ///));
    /// // Call game code
    /// ...
    /// // Reset, so nothing is held down
    /// fakeInputProvider.setHeldDownKeys(Set.of())
    ///```
    ///
    /// @param heldDown The set of held-down keys.
    ///                  Gdx represents these as ints, but use `Input.Keys.SOME_KEY_NAME` to name them.
    public void setHeldDownKeys(Set<Integer> heldDown) {
        // Previous implementation made it impossible to perform certain sequences of just-held-down keys
        // It's simpler to have both methods return the same thing, and works for all existing tests.
        this.heldDown = heldDown;
    }

    @Override
    public boolean isKeyPressed(int key) {
        if (key == Input.Keys.ANY_KEY) {
            return !heldDown.isEmpty();
        }
        return heldDown.contains(key);
    }

    @Override
    public boolean isKeyJustPressed(int key) {
        if (key == Input.Keys.ANY_KEY) {
            return !heldDown.isEmpty();
        }
        return heldDown.contains(key);
    }
}
