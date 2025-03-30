package cellcorp.gameofcells.ui;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.badlogic.gdx.graphics.Color;

import cellcorp.gameofcells.objects.Notification;

public class TestNotification {
    
    /**
     * * Test that the notification initializes correctly.
     * This test checks that the notification's message, duration, color, elapsed time, and alpha are set correctly upon creation.
     */
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

    /**
     * * Test that the notification updates its elapsed time and alpha correctly.
     * This test checks that the elapsed time increases and the alpha value changes based on the elapsed time.
     */
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

    /**
     * * Test that the notification is marked as expired after its duration.
     * This test checks that the notification's elapsed time is correctly updated and that it returns true when expired.
     */
    @Test
    public void updateReturnsTrueWhenExpired() {
        Notification notification = new Notification("Test", 1f, Color.WHITE);
        
        assertFalse(notification.update(0.9f)); // Not expired
        assertTrue(notification.update(0.2f)); // Now elapsed (0.9 + 0.2 > 1)
    }

    /**
     * * Test that the notification fades in correctly.
     * This test checks that the alpha value increases from 0 to 1 over the first half of the duration.
     */
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