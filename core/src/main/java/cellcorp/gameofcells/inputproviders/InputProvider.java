package cellcorp.gameofcells.inputproviders;

import com.badlogic.gdx.Input;

/// Provides input information, just like calling `Gdx.input` does.
///
///  But tests can give game objects a different version, so they can say what keys are held down,
///  and won't crash.
///
///  If you need methods from [com.badlogic.gdx.Input], add them here and call the method in [DefaultInputProvider]
public interface InputProvider {
    /// Returns whether the key is pressed.
    ///
    /// @param key The key code as found in [Input.Keys].
    boolean isKeyPressed(int key);
}
