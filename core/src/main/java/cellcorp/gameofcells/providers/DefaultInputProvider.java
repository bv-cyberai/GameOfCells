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

        // Update state immediately
        previousKeyStates.put(key, isPressed);

        // Return true only if the key was JUST pressed
        boolean result = isPressed && !wasPressed;

        // If this was a "just pressed" event, mark it as "already pressed" for next frame
        if (result) {
            previousKeyStates.put(key, true);
        }

        return result;
    }

    @Override
    public boolean isKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
    }
}
