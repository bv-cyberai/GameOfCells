package cellcorp.gameofcells.objects;

import cellcorp.gameofcells.Main;
import cellcorp.gameofcells.notification.Notification;
import cellcorp.gameofcells.notification.NotificationSource;
import cellcorp.gameofcells.runner.GameRunner;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestNotificationManager {

    public static final NotificationSource NEVER_FADE_OUT_SOURCE = new NotificationSource() {
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
     * * Test that the notification system starts empty.
     * This test checks that when the notification system is created, it starts with no notifications.
     */
    @Test
    public void systemStartsEmpty() {
        var runner = GameRunner.create();
        runner.inputProvider.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        runner.step();
        var system = ((GamePlayScreen) runner.game.getScreen()).getHUD().getNotificationManager();

        assertTrue(system.getNotifications().isEmpty());
    }

    /**
     * * Test that notifications are removed after their duration.
     * This test checks that after the duration of a notification has passed, it is removed from the system.
     */
    @Test
    public void updateRemovesExpiredNotifications() {
        var runner = GameRunner.create();
        runner.inputProvider.setHeldDownKeys(Set.of(Input.Keys.ENTER));
        runner.step();
        var gamePlayScreen = (GamePlayScreen) runner.game.getScreen();
        var system = gamePlayScreen.getHUD().getNotificationManager();
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();

        // Add a short-lived notification
        system.getNotifications().add(new Notification(graphicsProvider, assetManager, NEVER_FADE_OUT_SOURCE, "timeout", 0.2f));

        // Update past expiration
        runner.runForSeconds(2);
        assertTrue(system.getNotifications().isEmpty());
    }
}