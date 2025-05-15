package cellcorp.gameofcells.screens;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.notification.Notification;
import cellcorp.gameofcells.notification.NotificationManager;
import cellcorp.gameofcells.notification.NotificationSource;
import cellcorp.gameofcells.runner.GameRunner;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestGamePlayScreenNotifications {

    /**
     * A dummy notification source that never creates or cancels any notifications
     */
    private final NotificationSource dummyNotificationSource = new NotificationSource() {
        @Override
        public Notification createNotification(GamePlayScreen gamePlayScreen) {
            return null;
        }

        @Override
        public boolean cancelNotifications(GamePlayScreen gamePlayScreen) {
            return false;
        }
    };

    @BeforeAll
    public static void setUpLibGDX() {
        System.setProperty("com.badlogic.gdx.backends.headless.disableNativesLoading", "true");
        // Initialize headless LibGDX
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(
            new ApplicationListener() {
                @Override
                public void create() {
                }

                @Override
                public void resize(int width, int height) {
                }

                @Override
                public void render() {
                }

                @Override
                public void pause() {
                }

                @Override
                public void resume() {
                }

                @Override
                public void dispose() {
                }
            }, config
        );

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
     * * Test that energy warning notification shows when ATP is low.
     * This test checks that when the ATP level is low, a warning notification is displayed to the player.
     */
    @Test
    public void energyWarningNotificationShowsWhenATPLow() {
        // Setup game
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step(); // Move to gameplay screen

        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        var cell = gamePlayScreen.getCell();
        var manager = gamePlayScreen.getHUD().getNotificationManager();

        // Reset state
        cell.setCellATP(15); // Force ATP below threshold (20)

        // Run multiple updates to ensure systems process
        gameRunner.step();

        assertEquals(1, manager.getNotifications().size());
        // Table of warnings should have some element.
        assertFalse(manager.getTable().getCells().isEmpty());
    }

    /**
     * Test that notifications expire after their duration.
     * This test checks that after the duration of a notification has passed, it is removed from the system.
     */
    @Test
    public void notificationsExpireAfterDuration() {
        // Create game and move to gameplay screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();
        NotificationManager notificationSystem = gamePlayScreen.getHUD().getNotificationManager();

        // Add a short-lived notification
        var notification = new Notification(graphicsProvider, assetManager, dummyNotificationSource, "Time out", 0.01f);
        notificationSystem.getNotifications().add(notification);

        // Run the game for longer than the notification duration
        gameRunner.runForSeconds(3f);

        // Notification should have expired
        assertTrue(notificationSystem.getNotifications().isEmpty());
    }

    /**
     * * * Test that multiple notifications stack vertically.
     * This test checks that when multiple notifications are added, they are displayed in a stack vertically.
     */
    @Test
    public void multipleNotificationsStackVertically() {
        // Create game and move to gameplay screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();

        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();
        NotificationManager manager = gamePlayScreen.getHUD().getNotificationManager();
        var notifications = manager.getNotifications();

        // Add multiple notifications
        notifications.add(new Notification(graphicsProvider, assetManager, dummyNotificationSource, "Notification 1", null, Color.WHITE));
        notifications.add(new Notification(graphicsProvider, assetManager, dummyNotificationSource, "Notification 2", null, Color.WHITE));
        notifications.add(new Notification(graphicsProvider, assetManager, dummyNotificationSource, "Notification 3", null, Color.WHITE));

        // Verify all were added
        assertEquals(3, manager.getNotifications().size());

        gameRunner.step();

        // Verify notifications are in correct order
        assertEquals("Notification 1", notifications.get(0).text());
        assertEquals("Notification 2", notifications.get(1).text());
        assertEquals("Notification 3", notifications.get(2).text());
    }
}
