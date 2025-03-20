package cellcorp.gameofcells.providers;

import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import java.util.Map;

/// Implementation of [InputProvider] which calls `Gdx.input` for everything.
public class DefaultInputProvider implements InputProvider {
    private final Map<Integer, Boolean> previousKeyStates = new HashMap<>();

    @Override
    public boolean isKeyJustPressed(int key) {
        boolean isPressed = Gdx.input.isKeyPressed(key);
        boolean wasPressed = previousKeyStates.getOrDefault(key, false);

        // Update the previous key state
        previousKeyStates.put(key, isPressed);

        // Return true only if the key was just pressed
        return isPressed && !wasPressed;
    }

    @Override
    public boolean isKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
    }
}
