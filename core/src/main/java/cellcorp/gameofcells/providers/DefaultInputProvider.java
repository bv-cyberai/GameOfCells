package cellcorp.gameofcells.providers;

import com.badlogic.gdx.Gdx;

/// Implementation of [InputProvider] which calls `Gdx.input` for everything.
public class DefaultInputProvider implements InputProvider {

    @Override
    public boolean isKeyJustPressed(int key) {
        return Gdx.input.isKeyJustPressed(key);
    }

    @Override
    public boolean isKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
    }
}
