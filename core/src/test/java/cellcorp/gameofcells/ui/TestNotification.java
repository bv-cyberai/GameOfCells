package cellcorp.gameofcells.ui;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.badlogic.gdx.graphics.Color;

public class TestNotification {
    
    @Test
    public void notificationInitializesCorrectly() {
        String message = "Test";
        float duration = 2.5f;
        Color color = Color.RED;
        
        Notification notification = new Notification(message, duration, color);
        
        assertEquals(message, notification.getMessage());
        assertEquals(color, notification.getColor());
        assertEquals(0, notification.getElapsedTime());
        assertEquals(0, notification.getAlpha()); // Should start transparent
    }

    @Test
    public void updateAdvancesTimeAndAlpha() {
        Notification notification = new Notification("Test", 1f, Color.WHITE);
        
        // First update (fade in)
        notification.update(0.25f);
        assertEquals(0.25f, notification.getElapsedTime());
        assertEquals(0.5f, notification.getAlpha()); // 0.25*2 (linear fade in)
        
        // Second update (fully visible)
        notification.update(0.5f);
        assertEquals(0.75f, notification.getElapsedTime());
        assertEquals(0.5f, notification.getAlpha());
    }

    @Test
    public void updateReturnsTrueWhenExpired() {
        Notification notification = new Notification("Test", 1f, Color.WHITE);
        
        assertFalse(notification.update(0.9f)); // Not expired
        assertTrue(notification.update(0.2f)); // Now elapsed (0.9 + 0.2 > 1)
    }

    @Test
    public void fadeOutWorksCorrectly() {
        Notification notification = new Notification("Test", 2f, Color.WHITE);
        
        // Move to near end of duration
        notification.update(0.8f);
        assertEquals(1f, notification.getAlpha());
        
        // Begin fade out
        notification.update(0.3f);
        assertEquals(1.0f, notification.getAlpha());
    }
}