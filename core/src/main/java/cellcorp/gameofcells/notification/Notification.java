package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.AssetFileNames;
import cellcorp.gameofcells.providers.GraphicsProvider;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Null;

public class Notification {
    private static final float FADE_OUT_DURATION_SECONDS = 0.5f;
    private static final float FONT_SCALE = 0.25f;

    private final NotificationSource source;

    /**
     * Duration of this notification. If null, this notification lasts until cancelled.
     */
    @Null
    private final Float durationSeconds;
    private final Color color;
    private final Label label;
    private final String text;

    private State state = State.RUNNING;
    private float elapsedTime;

    public Notification(GraphicsProvider graphicsProvider, AssetManager assetManager, NotificationSource source, String message, @Null Float durationSeconds) {
        this(graphicsProvider, assetManager, source, message, durationSeconds, Color.WHITE);
    }

    public Notification(GraphicsProvider graphicsProvider, AssetManager assetManager, NotificationSource source, String text, @Null Float durationSeconds, Color color) {
        this.source = source;
        this.durationSeconds = durationSeconds;
        this.text = text;
        this.color = color;

        this.label = label(graphicsProvider, assetManager, text);
    }

    private Label label(GraphicsProvider graphicsProvider, AssetManager assetManager, String text) {
        var font = assetManager.get(AssetFileNames.HUD_FONT, BitmapFont.class);
        var labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = graphicsProvider.createLabel(text, labelStyle);
        label.setFontScale(FONT_SCALE);
        label.setColor(color);
        return label;
    }

    public Label getLabel() {
        return this.label;
    }

    public NotificationSource source() {
        return this.source;
    }

    /**
     * If not already fading out, starts notification fade-out.
     */
    public void startFadeOut() {
        if (state != State.FADING_OUT) {
            this.elapsedTime = 0;
            this.state = State.FADING_OUT;
        }
    }

    /**
     * * Updates the notification's state.
     * This method updates the elapsed time and calculates the alpha value for fade-in/out effect.
     *
     * @return Whether this notification has expired
     */
    public boolean update(float deltaTime) {
        if (state == State.RUNNING) {
            if (durationSeconds != null) {
                elapsedTime += deltaTime;
                if (elapsedTime >= durationSeconds) {
                    startFadeOut();
                }
            }
            return false;
        } else {
            // State.FADING_OUT
            elapsedTime += deltaTime;
            label.setColor(color.r, color.g, color.b, alpha());
            return elapsedTime >= FADE_OUT_DURATION_SECONDS;
        }
    }

    /**
     * Public for test use only.
     * <p>
     * Get current color alpha, based on elapsed time.
     *
     * @return alpha, in range 0 <= alpha <= 1
     */
    public float alpha() {
        float secondsRemaining = FADE_OUT_DURATION_SECONDS - elapsedTime;
        if (secondsRemaining < FADE_OUT_DURATION_SECONDS) {
            return secondsRemaining / FADE_OUT_DURATION_SECONDS;
        } else {
            return 1;
        }
    }

    /**
     * For test use only.
     */
    public float getElapsedTime() {
        return elapsedTime;
    }

    /**
     * For test use only.
     */
    public String text() {
        return this.text;
    }

    private enum State {
        RUNNING,
        FADING_OUT,
    }
}
