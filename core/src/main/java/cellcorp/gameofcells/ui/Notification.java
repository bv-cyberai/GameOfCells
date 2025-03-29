package cellcorp.gameofcells.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Notification {
    private String message;
    private float duration;
    private float elapsedTime;
    private Color color;
    private float alpha;

    public Notification(String message, float duration, Color color) {
        this.message = message;
        this.duration = duration;
        this.elapsedTime = 0;
        this.color = color;
        this.alpha = 0; // Start transparent for fade-in effect
    }

    /**
     * * Updates the notification's state.
     * This method updates the elapsed time and calculates the alpha value for fade-in/out effect.
     * @param deltaTime
     * @return
    */
    public boolean update(float deltaTime) {
        elapsedTime += deltaTime;

        // Fade in/out logic
        if (elapsedTime < 0.5f) {
            alpha = Math.min(1, elapsedTime * 2); // Fade in
        } else if (elapsedTime > duration - 0.5f) {
            alpha = Math.max(0, (duration - elapsedTime) * 2); // Fade out
        } else {
            alpha = 1; // Fully visible in the middle of the duration
        }

        return elapsedTime >= duration; // Return true if the notification is expired
    }

    /**
     * * Renders the notification on the screen.
     * This method uses the provided SpriteBatch and BitmapFont to draw the notification message at the specified position.
     * @param batch
     * @param font
     * @param x
     * @param y
    */
    public void render(SpriteBatch batch, BitmapFont font, float x, float y) {
        Color oldColor = font.getColor();
        font.setColor(color.r, color.g, color.b, alpha);
        font.draw(batch, message, x, y);
        font.setColor(oldColor); // Reset to old color
    }

    /**
     * * Get the message of the notification.
     * This message is displayed to the user when the notification is rendered.
     * @return The message of the notification.
    */
    public String getMessage() {
        return message;
    }

    /**
     * * Get the color of the notification.
     * This color is used to render the notification text.
     * @return The color of the notification.
    */
    public Color getColor() {
        return color;
    }

    /**
     * * Get the alpha value for the notification.
     * This value is between 0 and 1, where 0 is fully transparent and 1 is fully opaque.
     * @return The alpha value of the notification.
    */
    public float getAlpha() {
        return alpha;
    }
}
