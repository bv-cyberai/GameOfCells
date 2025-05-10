package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.graphics.Color;

public class LowHealthSource implements NotificationSource {
    private static final float HEALTH_THRESHOLD = 30;
    private static final String TEXT = "DANGER: Low Health!!!";
    private static final Float DURATION = null;
    private static final Color DARK_RED = new Color(0.7f, 0, 0, 1);
    private static final Color COLOR = DARK_RED;

    private final NotificationManager notificationManager;

    public LowHealthSource(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @Override
    public Notification createNotification(GamePlayScreen gamePlayScreen) {
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();
        var health = gamePlayScreen.getCell().getCellHealth();
        if (0 < health && health < HEALTH_THRESHOLD && !notificationManager.hasNotificationFrom(this)) {
            return new Notification(graphicsProvider, assetManager, this, TEXT, DURATION, COLOR);
        } else {
            return null;
        }
    }

    @Override
    public boolean cancelNotifications(GamePlayScreen gamePlayScreen) {
        var health = gamePlayScreen.getCell().getCellHealth();
        return (health == 0 || health >= HEALTH_THRESHOLD);
    }
}
