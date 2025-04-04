package cellcorp.gameofcells.objects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import cellcorp.gameofcells.Main;

import org.mockito.Mockito;

public class TestNotificationManager {
    
@BeforeAll
    public static void setUpLibGDX() {
        System.setProperty("com.badlogic.gdx.backends.headless.disableNativesLoading", "true");
        // Initialize headless LibGDX
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationListener() {
            @Override public void create() {}
            @Override public void resize(int width, int height) {}
            @Override public void render() {}
            @Override public void pause() {}
            @Override public void resume() {}
            @Override public void dispose() {}
        }, config);

        // Mock the graphics provider
        Gdx.graphics = Mockito.mock(Graphics.class);
        Mockito.when(Gdx.graphics.getWidth()).thenReturn(Main.DEFAULT_SCREEN_WIDTH);
        Mockito.when(Gdx.graphics.getHeight()).thenReturn(Main.DEFAULT_SCREEN_HEIGHT);

        GL20 gl20 = Mockito.mock(GL20.class);
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
    }

    @AfterAll
    public static void cleanUp() {
        if (Gdx.app != null) {
            Gdx.app.exit();
        }
    }

    /**
     * * Test that the notification system starts empty.
     * This test checks that when the notification system is created, it starts with no notifications.
     */
    @Test
    public void systemStartsEmpty() {
        BitmapFont mockFont = Mockito.mock(BitmapFont.class);
        NotificationManager system = new NotificationManager(mockFont, 30f, 700f);
        
        assertTrue(system.getNotifications().isEmpty());
    }

    /**
     * * Test that notifications are added correctly.
     * This test checks that when a notification is added, it is stored in the system with the correct message and color.
     */
    @Test
    public void addNotificationWorks() {
        BitmapFont mockFont = Mockito.mock(BitmapFont.class);
        NotificationManager system = new NotificationManager(mockFont, 30f, 700f);
        
        system.addNotification("Test", 1f, Color.RED);
        assertEquals(1, system.getNotifications().size);
        
        Notification added = system.getNotifications().get(0);
        assertEquals("Test", added.getMessage());
        assertEquals(Color.RED, added.getColor());
    }

    /**
     * * Test that notifications are removed after their duration.
     * This test checks that after the duration of a notification has passed, it is removed from the system.
     */
    @Test
    public void updateRemovesExpiredNotifications() {
        BitmapFont mockFont = Mockito.mock(BitmapFont.class);
        NotificationManager system = new NotificationManager(mockFont, 30f, 700f);
        
        // Add a short-lived notification
        system.addNotification("Temp", 0.1f, Color.WHITE);
        assertEquals(1, system.getNotifications().size);
        
        // Update past expiration
        system.update(0.2f);
        assertTrue(system.getNotifications().isEmpty());
    }

    /**
     * * Test that notifications are removed after their duration.
     * This test checks that after the duration of a notification has passed, it is removed from the system.
     */
    @Test
    public void updatePreservesActiveNotifications() {
        BitmapFont mockFont = Mockito.mock(BitmapFont.class);
        NotificationManager system = new NotificationManager(mockFont, 30f, 700f);
        
        system.addNotification("Long", 2f, Color.GREEN);
        system.update(1f);
        
        assertEquals(1, system.getNotifications().size);
        assertEquals("Long", system.getNotifications().get(0).getMessage());
    }

    /**
     * * Test that multiple notifications stack in order.
     * This test checks that when multiple notifications are added, they are stored in the order they were added.
     */
    @Test
    public void multipleNotificationsStackInOrder() {
        BitmapFont mockFont = Mockito.mock(BitmapFont.class);
        NotificationManager system = new NotificationManager(mockFont, 30f, 700f);
        
        system.addNotification("First", 1f, Color.RED);
        system.addNotification("Second", 1f, Color.GREEN);
        
        assertEquals(2, system.getNotifications().size);
        assertEquals("First", system.getNotifications().get(0).getMessage());
        assertEquals("Second", system.getNotifications().get(1).getMessage());
    }

    /**
     * * Test that the notification system can be cleared.
     * This test checks that when the clear method is called, all notifications are removed from the system.
     */
    @Test
    public void clearRemovesAllNotifications() {
        BitmapFont mockFont = Mockito.mock(BitmapFont.class);
        NotificationManager system = new NotificationManager(mockFont, 30f, 700f);
        
        system.addNotification("ToClear", 1f, Color.BLUE);
        assertEquals(1, system.getNotifications().size);
        
        system.clearNotifications();
        assertTrue(system.getNotifications().isEmpty());
    }
}