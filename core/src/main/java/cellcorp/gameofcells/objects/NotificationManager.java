package cellcorp.gameofcells.objects;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import java.util.HashMap;
import java.util.Map;


public class NotificationManager {
    private Array<Notification> notifications;
    private float verticalSpacing; // Space between notifications
    private float baseYPosition; // Base Y position for notifications
    private Map<String, Float> notificationCooldowns; // Cooldowns for notifications
    private float cooldownDuration = 1f; // Cooldown duration for notifications (in seconds)

    public NotificationManager(BitmapFont rubikFont) {
        this.notifications = new Array<>();
        this.verticalSpacing = 30f; // Adjust this value as needed
        this.baseYPosition = 700f; // Should match NOTIFICATION_Y in HUD
        this.notificationCooldowns = new HashMap<>(); // Initialize cooldowns map
    }

    /**
     * * * Add a notification to the notification system.
     * This method is used to add a notification to the notification system with a specified message, duration, and color.
     * @param message
     * @param duration
     * @param color
     */
    public void addNotification(String message, float duration, Color color) {
        // Check if this message is on cooldown
        if (notificationCooldowns.containsKey(message)) {
            float remainingCooldown = notificationCooldowns.get(message);
            if (remainingCooldown > 0) {
                return; // Skip adding duplicate notification
            }
        }

        notifications.add(new Notification(message, duration, color));
        notificationCooldowns.put(message, cooldownDuration); // Set cooldown for this message
    }

    /**
     * * * Add a notification to the notification system.
     * This method is used to add a notification to the notification system with a specified message, duration, and color.
     * @param deltaTime
     */
    public void update(float deltaTime) {
        // Update cooldowns
        for (Map.Entry<String, Float> entry : notificationCooldowns.entrySet()) {
            float remaining = entry.getValue() - deltaTime;
            notificationCooldowns.put(entry.getKey(), Math.max(0, remaining));
        }

        // Update notifications
        for (int i = notifications.size - 1; i >= 0; i--) {
            if (notifications.get(i).update(deltaTime)) {
                notifications.removeIndex(i); // Remove expired notification
            }
        }
    }

    /**
     * * * Render the notifications on the screen.
     * This method is used to render the notifications on the screen using a specified SpriteBatch.
     */
    public void render() {}

    /**
     * * Get the notifications array.
     * This method is used to retrieve the notifications array for the notification system.
     * @return
     */
    public Array<Notification> getNotifications() {
        return notifications; // Get all notifications
    }

    /**
     * * Get the Y position for a specific notification.
     * This method is used to calculate the Y position for a specific notification based on its index.
     * @param index
     * @return
     */
    public float getNotificationY(int index) {
        return baseYPosition - (index * verticalSpacing); // Calculate Y position for each notification
    }

    /**
     * * Get the vertical spacing between notifications.
     * This method is used to retrieve the vertical spacing between notifications.
     * @return
     */
    public float getVerticalSpacing() {
        return verticalSpacing; // Get vertical spacing
    }

    /**
     * * Set the vertical spacing between notifications.
     * This method is used to set the vertical spacing between notifications.
     * @param verticalSpacing
     */
    public void setVerticalSpacing(float verticalSpacing) {
        this.verticalSpacing = verticalSpacing; // Set vertical spacing
    }

    /**
     * * Get the base Y position for notifications.
     * This method is used to retrieve the base Y position for notifications.
     * @return
     */
    public float getBaseYPosition() {
        return baseYPosition; // Get base Y position
    }

    /**
     * * Set the base Y position for notifications.
     * This method is used to set the base Y position for notifications.
     * @param baseYPosition
     */
    public void setBaseYPosition(float baseYPosition) {
        this.baseYPosition = baseYPosition; // Set base Y position
    }

    /**
     * * Clear all notifications.
     * This method is used to clear all notifications from the notification system.
     */
    public void clearNotifications() {
        notifications.clear(); // Clear all notifications
        notificationCooldowns.clear(); // Clear cooldowns
    }

    /**
     * * Get the cooldown duration for notifications.
     * This method is used to retrieve the cooldown duration for notifications.
     * @return
     */
    public float getCooldownDuration() {
        return cooldownDuration; // Get cooldown duration
    }

    /**
     * * Set the cooldown duration for notifications.
     * This method is used to set the cooldown duration for notifications.
     * @param cooldownDuration
     */
    public void setCooldownDuration(float cooldownDuration) {
        this.cooldownDuration = cooldownDuration; // Set cooldown duration
    }
}
