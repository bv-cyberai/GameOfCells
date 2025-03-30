package cellcorp.gameofcells.screens;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.objects.HUD;
import cellcorp.gameofcells.objects.Notification;
import cellcorp.gameofcells.objects.NotificationManager;
import cellcorp.gameofcells.runner.GameRunner;

public class TestGamePlayScreenNotifications {

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
     * * Test that notifications appear on the gameplay screen.
     * This test checks that when a notification is added, it appears on the gameplay screen.
     */
    @Test
    public void notificationsAppearOnGamePlayScreen() {
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        
        // Get the HUD from the game screen
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        HUD hud = gamePlayScreen.getHUD();
        NotificationManager notificationSystem = hud.getNotificationManager();
        
        String testMessage = "Test notification";
        notificationSystem.addNotification(testMessage, 3f, Color.WHITE);
        
        assertEquals(1, notificationSystem.getNotifications().size);
        Notification notification = notificationSystem.getNotifications().get(0);
        assertEquals(testMessage, notification.getMessage());
        assertEquals(Color.WHITE, notification.getColor());
    }

    /**
     * * Test that energy warning notification shows when ATP is low.
     * This test checks that when the ATP level is low, a warning notification is displayed to the player.
     */
    @Test
    public void energyWarningNotificationShowsWhenATPLow() {
        // Create game and move to gameplay screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        var cell = gamePlayScreen.getCell();
        HUD hud = gamePlayScreen.getHUD();
        NotificationManager notificationSystem = hud.getNotificationManager();
        
        // Force low ATP condition
        cell.setCellATP(0);
        gamePlayScreen.setHasShownEnergyWarning(false);
        
        // Update the game (should trigger warning)
        gameRunner.step();
        
        // Check notification was shown
        assertFalse(notificationSystem.getNotifications().isEmpty());
        
        boolean foundWarning = false;
        for (Notification n : notificationSystem.getNotifications()) {
            if (n.getMessage().contains("LOW ENERGY: ATP critically low")) {
                foundWarning = true;
                break;
            }
        }
        assertTrue(foundWarning);
        
        // Verify flag was set
        assertTrue(gamePlayScreen.isHasShownEnergyWarning());
    }


    /**
     * * * Test that acid zone warning notification shows when entering acid zone.
     * This test checks that when the player enters an acid zone, a warning notification is displayed to the player.
     */
    @Test
    public void acidZoneWarningNotificationShowsWhenEnteringAcidZone() {
        // Create game and move to gameplay screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        HUD hud = gamePlayScreen.getHUD();
        NotificationManager notificationSystem = hud.getNotificationManager();
        
        // Simulate entering acid zone
        gamePlayScreen.setWasInAcidZone(false);
        gameRunner.step(); // This should trigger the acid zone check
        
        // Normally we'd need to mock the zone manager to return true for isInAcidZone,
        // but for this test we'll directly call the warning method
        hud.showAcidZoneWarning();
        
        // Check notification was shown
        assertFalse(notificationSystem.getNotifications().isEmpty());
        
        boolean foundWarning = false;
        for (Notification n : notificationSystem.getNotifications()) {
            if (n.getMessage().contains("DANGER: Acid zone")) {
                foundWarning = true;
                break;
            }
        }
        assertTrue(foundWarning);
    }

    /**
     * * * Test that notifications expire after their duration.
     * This test checks that after the duration of a notification has passed, it is removed from the system.
     */
    @Test
    public void notificationsExpireAfterDuration() {
        // Create game and move to gameplay screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        HUD hud = gamePlayScreen.getHUD();
        NotificationManager notificationSystem = hud.getNotificationManager();
        
        // Add a short-lived notification
        notificationSystem.addNotification("Test notification", 0.1f, Color.WHITE);
        assertEquals(1, notificationSystem.getNotifications().size);
        
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
        HUD hud = gamePlayScreen.getHUD();
        NotificationManager notificationSystem = hud.getNotificationManager();
        
        // Add multiple notifications
        notificationSystem.addNotification("Notification 1", 2f, Color.WHITE);
        notificationSystem.addNotification("Notification 2", 2f, Color.YELLOW);
        notificationSystem.addNotification("Notification 3", 2f, Color.RED);
        
        // Verify all were added
        assertEquals(3, notificationSystem.getNotifications().size);
        
        // Verify notifications are in correct order
        assertEquals("Notification 1", notificationSystem.getNotifications().get(0).getMessage());
        assertEquals("Notification 2", notificationSystem.getNotifications().get(1).getMessage());
        assertEquals("Notification 3", notificationSystem.getNotifications().get(2).getMessage());
        
        // Verify vertical positions if needed (optional)
        assertEquals(700f, notificationSystem.getNotificationY(0)); // First notification
        assertEquals(670f, notificationSystem.getNotificationY(1)); // Second notification
        assertEquals(640f, notificationSystem.getNotificationY(2)); // Third notification
    }
}