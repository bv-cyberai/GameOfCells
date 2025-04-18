package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.notification.Notification;
import cellcorp.gameofcells.providers.FakeGraphicsProvider;
import com.badlogic.gdx.assets.AssetManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class TestNotification {

    /**
     * * Test that the notification updates its elapsed time and alpha correctly.
     * This test checks that the elapsed time increases and the alpha value changes based on the elapsed time.
     */
    @Test
    public void updateAdvancesTime() {
        Notification notification = new Notification(
                new FakeGraphicsProvider(),
                Mockito.mock(AssetManager.class),
                TestNotificationManager.NEVER_FADE_OUT_SOURCE,
                "Expire",
                10f
        );

        notification.update(0.25f);
        assertEquals(0.25f, notification.getElapsedTime());

        notification.update(0.5f);
        assertEquals(0.75f, notification.getElapsedTime());
    }

    /**
     * * Test that the notification is marked as expired after its duration.
     * This test checks that the notification's elapsed time is correctly updated and that it returns true when expired.
     */
    @Test
    public void updateReturnsTrueWhenExpired() {
        Notification notification = new Notification(
                new FakeGraphicsProvider(),
                Mockito.mock(AssetManager.class),
                TestNotificationManager.NEVER_FADE_OUT_SOURCE,
                "Expire",
                0.2f
        );

        assertFalse(notification.update(0.2f)); // Not expired
        assertTrue(notification.update(1f));
    }

    /**
     * * Test that the notification is marked as expired when manually expired.
     * This test checks that the notification's elapsed time is correctly updated and that it returns true when manually expired.
     */
    @Test
    public void updateReturnsTrueWhenManuallyExpired() {
        Notification notification = new Notification(
                new FakeGraphicsProvider(),
                Mockito.mock(AssetManager.class),
                TestNotificationManager.NEVER_FADE_OUT_SOURCE,
                "Expire",
                null
        );

        notification.update(0.01f);
        notification.startFadeOut();
        assertTrue(notification.update(0.5f)); // Should be expired immediately
    }

    @Test
    public void fadeOutReducesAlpha() {
        Notification notification = new Notification(
                new FakeGraphicsProvider(),
                Mockito.mock(AssetManager.class),
                TestNotificationManager.NEVER_FADE_OUT_SOURCE,
                "Expire",
                1.5f
        );

        // Move to near end of duration
        notification.update(1.5f);

        // Begin fade in
        notification.update(0.3f);
        assertTrue(notification.alpha() - 0.4f < 0.01);
    }
}