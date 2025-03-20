package cellcorp.gameofcells.providers;

import com.badlogic.gdx.Gdx;

import java.util.HashMap;
import java.util.Map;

/// Implementation of [InputProvider] which calls `Gdx.input` for everything.
public class DefaultInputProvider implements InputProvider {
    private final Map<Integer, Boolean> previousKeyStates = new HashMap<>();

    @Override
    public boolean isKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
    }

    @Override
    public boolean isKeyJustPressed(int key) {
        boolean isPressed = Gdx.input.isKeyPressed(key);
        boolean wasPressed = previousKeyStates.getOrDefault(key, false);
        previousKeyStates.put(key, isPressed);
        return isPressed && !wasPressed;
    }
}
