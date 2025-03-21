package cellcorp.gameofcells;

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

import cellcorp.gameofcells.providers.InputProvider;

import java.util.HashSet;
import java.util.Set;

/// Fake [InputProvider] for use in tests.
///
/// To test behavior when a key is held down, pass a [FakeInputProvider]
/// to the game object under test, call [#setHeldDownKeys],
/// then call the method(s) under test.
public class FakeInputProvider implements InputProvider {

    /// Set of keys which were just pressed, according to our test.
    private final Set<Integer> justPressedKeys = new HashSet<>();

    /// Set of keys which are currently held down, according to our test.
    private Set<Integer> heldDown = Set.of();

    /// Set the keys that are currently held down.
    /// Subsequent calls to this method erase previously held-down keys.
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
    /// @param heldDown The set of keys.
    ///                                                 Gdx represents these as ints, but use `Input.Keys.SOME_KEY_NAME` to name them.
    public void setHeldDownKeys(Set<Integer> keys) {
        for (Integer key : keys) {
            if (!heldDown.contains(key)) {
                justPressedKeys.add(key);
            }
        }

        justPressedKeys.retainAll(keys);

        // Update the heldDown set
        this.heldDown = keys;
    }

    public boolean isKeyPressed(int key) {
        return heldDown.contains(key);
    }

    @Override
    public boolean isKeyJustPressed(int key) {
        boolean wasJustPressed = justPressedKeys.contains(key);
        justPressedKeys.remove(key); // Remove the key from the set, so it's not just pressed next time
        return wasJustPressed;
    }
}
