package cellcorp.gameofcells.objects;

import com.badlogic.gdx.graphics.Color;

public class Notification {
    private String message;
    private float duration;
    private float elapsedTime;
    private Color color;
    private float alpha;

    private boolean manuallyExpired = false; // Flag to indicate if the notification was manually expired

    public Notification(String message, float duration, Color color) {
        this.message = message;
        this.duration = duration;
        this.color = color != null ? color : Color.WHITE; // Default to white if no color is provided
        this.alpha = 0; // Start transparent for fade-in effect
    }

    /**
     * * Updates the notification's state.
     * This method updates the elapsed time and calculates the alpha value for fade-in/out effect.
     * @param deltaTime
     * @return
    */
    public boolean update(float deltaTime) {
        if (manuallyExpired) {
            alpha = Math.max(0, alpha - deltaTime * 2); // Fade out if manually expired
            return alpha <= 0; // If manually expired, return true immediately
        }

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

    /**
     * * Get the elapsed time since the notification was created.
     * This value is used to determine if the notification has expired.
     * @return The elapsed time of the notification.
     */
    public float getElapsedTime() {
        return elapsedTime;
    }

    /**
     * * Get the duration of the notification.
     * This value is used to determine how long the notification will be displayed before it expires.
     * @return The duration of the notification.
     */
    public float getDuration() {
        return duration;
    }

    /**
     * * * Check if the notification has been manually expired.
     * This method checks if the notification has been manually expired by the user.
     */
    public void expire() {
        this.manuallyExpired = true; // Set the flag to indicate manual expiration
    }
}
