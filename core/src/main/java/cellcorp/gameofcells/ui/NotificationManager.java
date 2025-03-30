package cellcorp.gameofcells.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NotificationManager {
    private Array<Notification> notifications;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private NotificationManager notificationSystem;
    

    public NotificationManager(BitmapFont rubikFont) {
        this.notifications = new Array<>();
        this.font = rubikFont;
        this.glyphLayout = new GlyphLayout();
        this.notificationSystem = this; // Initialize the notification system
    }

    /**
     * * * Add a notification to the notification system.
     * This method is used to add a notification to the notification system with a specified message, duration, and color.
     * @param message
     * @param duration
     * @param color
     */
    public void addNotification(String message, float duration, Color color) {
        notifications.add(new Notification(message, duration, color));
    }

    /**
     * * * Add a notification to the notification system.
     * This method is used to add a notification to the notification system with a specified message, duration, and color.
     * @param deltaTime
     */
    public void update(float deltaTime) {
        for (int i = notifications.size - 1; i >= 0; i--) {
            if (notifications.get(i).update(deltaTime)) {
                notifications.removeIndex(i); // Remove expired notification
            }
        }
    }

    /**
     * * Render the notifications on the screen.
     * This method is used to render the notifications on the screen using the provided SpriteBatch.
     * @param batch
     */
    public void render(SpriteBatch batch) {
        float y = Gdx.graphics.getHeight() - 50; // Start from the top of the screen

        for (Notification notification : notifications) {
            Color color = notification.getColor();
            if (color == null) {
                color = Color.WHITE; // Default color if none is set
            }
            font.setColor(color);
            // Calculate text width properly for centering
            glyphLayout.setText(font, notification.getMessage());
            float x = (Gdx.graphics.getWidth() - glyphLayout.width) / 2; // Center the message horizontally
            
            font.draw(batch, glyphLayout, x, y); // Draw the notification
            y -= 50; // Stack notifications vertically
        }
        font.setColor(Color.WHITE); // Reset font size
    }

    /**
     * * Get the notification system.
     * This method is used to retrieve the notification system for the game.
     * @return
     */
    public NotificationManager getNotificationSystem() {
        return notificationSystem; // Get a specific notification by index
    }

    /**
     * * Get the notifications array.
     * This method is used to retrieve the notifications array for the notification system.
     * @return
     */
    public Array<Notification> getNotifications() {
        return notifications; // Get all notifications
    }

    /**
     * * Set the notifications array.
     * This method is used to set the notifications array for the notification system.
     * @param notifications
     */
    public void setNotifications(Array<Notification> notifications) {
        this.notifications = notifications; // Set the notifications array
    }
}
