package cellcorp.gameofcells.objects;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.badlogic.gdx.graphics.Color;

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
     * * Test that the notification is marked as expired when manually expired.
     * This test checks that the notification's elapsed time is correctly updated and that it returns true when manually expired.
     */
    @Test
    public void updateReturnsTrueWhenManuallyExpired() {
        Notification notification = new Notification("Test", 1f, Color.WHITE);
        
        // Manually expire the notification
        notification.setManuallyExpired(true);
        
        assertTrue(notification.update(0.5f)); // Should be expired immediately
    }

    /**
     * * Test that the notification fades out correctly.
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

    /**
     * * Test that the notification fades in correctly.
     * This test checks that the alpha value decreases from 1 to 0 over the second half of the duration.
     */
    @Test
    public void fadeInWorksCorrectly() {
        Notification notification = new Notification("Test", 2f, Color.WHITE);
        
        // Move to near end of duration
        notification.update(1.5f);
        assertEquals(1f, notification.getAlpha());
        
        // Begin fade in
        notification.update(0.5f);
        assertEquals(0.0f, notification.getAlpha());
    }

    /**
     * * Test that the notification's color is set correctly.
     * This test checks that the notification's color is set to the specified value upon creation.
     */
    @Test
    public void notificationColorIsSetCorrectly() {
        Color color = Color.BLUE;
        Notification notification = new Notification("Test", 1f, color);
        
        assertEquals(color, notification.getColor());
    }

    /**
     * * Test that the notification's message is set correctly.
     * This test checks that the notification's message is set to the specified value upon creation.
     */
    @Test
    public void notificationMessageIsSetCorrectly() {
        String message = "Test Message";
        Notification notification = new Notification(message, 1f, Color.WHITE);
        
        assertEquals(message, notification.getMessage());
    }

    /**
     * * Test that the notification's duration is set correctly.
     * This test checks that the notification's duration is set to the specified value upon creation.
     */
    @Test
    public void notificationDurationIsSetCorrectly() {
        float duration = 3.0f;
        Notification notification = new Notification("Test", duration, Color.WHITE);
        
        assertEquals(duration, notification.getDuration());
    }

    /**
     * * Test that the notification's alpha value is set correctly.
     * This test checks that the notification's alpha value is set to 0 upon creation.
     */
    @Test
    public void notificationAlphaIsSetCorrectly() {
        Notification notification = new Notification("Test", 1f, Color.WHITE);
        
        assertEquals(0.0f, notification.getAlpha());
    }

}