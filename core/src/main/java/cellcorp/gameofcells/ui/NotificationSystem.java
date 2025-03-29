package cellcorp.gameofcells.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NotificationSystem {
    private Array<Notification> notifications;
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    public NotificationSystem(BitmapFont rubikFont) {
        this.notifications = new Array<>();
        this.font = rubikFont;
        this.glyphLayout = new GlyphLayout();
    }

    public void addNotification(String message, float duration, Color color) {
        notifications.add(new Notification(message, duration, color));
    }

    public void update(float deltaTime) {
        for (int i = notifications.size - 1; i >= 0; i--) {
            if (notifications.get(i).update(deltaTime)) {
                notifications.removeIndex(i); // Remove expired notification
            }
        }
    }

    public void render(SpriteBatch batch) {
        float y = Gdx.graphics.getHeight() - 50; // Start from the top of the screen

        for (Notification notification : notifications) {
            font.setColor(notification.getColor());
            // Calculate text width properly for centering
            glyphLayout.setText(font, notification.getMessage());
            float x = (Gdx.graphics.getWidth() - glyphLayout.width) / 2; // Center the message horizontally
            
            font.draw(batch, glyphLayout, x, y); // Draw the notification
            y -= 50; // Stack notifications vertically
        }
        font.setColor(Color.WHITE); // Reset font size
    }

    public void dispose() {
        font.dispose(); // Dispose of the font when done
        for (Notification notification : notifications) {
            notification.dispose(); // Dispose of each notification
        }
    }
}
