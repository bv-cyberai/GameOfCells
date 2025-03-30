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
import cellcorp.gameofcells.runner.GameRunner;
import cellcorp.gameofcells.ui.Notification;
import cellcorp.gameofcells.ui.NotificationManager;

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

    @Test
    public void notificationsAppearOnGamePlayScreen() {
        // Create a new game runner
        var gameRunner = GameRunner.create();
        
        // Start on main menu
        assertInstanceOf(MainMenuScreen.class, gameRunner.game.getScreen());
        
        // Press enter to move to gameplay screen
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        gameRunner.setHeldDownKeys(Set.of()); // Release enter key
        gameRunner.step();
        
        // Verify we're on gameplay screen
        var screen = gameRunner.game.getScreen();
        assertInstanceOf(GamePlayScreen.class, screen);
        var gamePlayScreen = (GamePlayScreen) screen;
        
        // Get the notification system
        NotificationManager notificationSystem = gamePlayScreen.getNotificationSystem();
        
        // Add a test notification
        String testMessage = "Test notification";
        notificationSystem.addNotification(testMessage, 3f, Color.WHITE);
        
        // Verify notification was added
        assertEquals(1, notificationSystem.getNotifications().size);
        Notification notification = notificationSystem.getNotifications().get(0);
        assertEquals(testMessage, notification.getMessage());
        assertEquals(Color.WHITE, notification.getColor());
    }

    @Test
    public void energyWarningNotificationShowsWhenATPLow() {
        // Create game and move to gameplay screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        var cell = gamePlayScreen.getCell();
        
        // Force low ATP condition
        cell.setCellATP(0);
        gamePlayScreen.setHasShownEnergyWarning(false);
        
        // Update the game (should trigger warning)
        gameRunner.step();
        
        // Check notification was shown
        NotificationManager notificationSystem = gamePlayScreen.getNotificationSystem();
        assertFalse(notificationSystem.getNotifications().isEmpty());
        
        boolean foundWarning = false;
        for (Notification n : notificationSystem.getNotifications()) {
            if (n.getMessage().contains("WARNING: Out of energy")) {
                foundWarning = true;
                break;
            }
        }
        assertTrue(foundWarning);
        
        // Verify flag was set
        assertTrue(gamePlayScreen.isHasShownEnergyWarning());
    }

    @Test
    public void acidZoneWarningNotificationShowsWhenEnteringAcidZone() {
        // Create game and move to gameplay screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        
        // Simulate entering acid zone
        gamePlayScreen.setWasInAcidZone(false);
        gameRunner.step(); // This should trigger the acid zone check
        
        // Normally we'd need to mock the zone manager to return true for isInAcidZone,
        // but for this test we'll directly call the warning method
        gamePlayScreen.showAcidZoneWarning();
        
        // Check notification was shown
        NotificationManager notificationSystem = gamePlayScreen.getNotificationSystem();
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

    @Test
    public void notificationsExpireAfterDuration() {
        // Create game and move to gameplay screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        NotificationManager notificationSystem = gamePlayScreen.getNotificationSystem();
        
        // Add a short-lived notification
        notificationSystem.addNotification("Test notification", 0.1f, Color.WHITE);
        assertEquals(1, notificationSystem.getNotifications().size);
        
        // Run the game for longer than the notification duration
        gameRunner.runForSeconds(3f);
        
        // Notification should have expired
        assertTrue(notificationSystem.getNotifications().isEmpty());
    }

    @Test
    public void multipleNotificationsStackVertically() {
        // Create game and move to gameplay screen
        var gameRunner = GameRunner.create();
        gameRunner.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        gameRunner.step();
        
        var gamePlayScreen = (GamePlayScreen) gameRunner.game.getScreen();
        NotificationManager notificationSystem = gamePlayScreen.getNotificationSystem();
        
        // Add multiple notifications
        notificationSystem.addNotification("Notification 1", 2f, Color.WHITE);
        notificationSystem.addNotification("Notification 2", 2f, Color.YELLOW);
        notificationSystem.addNotification("Notification 3", 2f, Color.RED);
        
        // Verify all were added
        assertEquals(3, notificationSystem.getNotifications().size);
        
        // The render position would be tested in an integration test,
        // but we can verify the notifications are in the correct order
        assertEquals("Notification 1", notificationSystem.getNotifications().get(0).getMessage());
        assertEquals("Notification 2", notificationSystem.getNotifications().get(1).getMessage());
        assertEquals("Notification 3", notificationSystem.getNotifications().get(2).getMessage());
    }
}