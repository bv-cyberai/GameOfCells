package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.graphics.Color;

public class LowAtpSource implements NotificationSource {
    private static final int ATP_THRESHOLD = 20;
    private static final String TEXT = "WARNING: Low ATP. Find some glucose.";
    private static final Float DURATION = null;
    private static final Color COLOR = Color.YELLOW;

    private final NotificationManager notificationManager;

    public LowAtpSource(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @Override
    public Notification createNotification(GamePlayScreen gamePlayScreen) {
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();
        var atp = gamePlayScreen.getCell().getCellATP();
        if (0 < atp && atp < ATP_THRESHOLD && !notificationManager.hasNotificationFrom(this)) {
            return new Notification(graphicsProvider, assetManager, this, TEXT, DURATION, COLOR);
        } else {
            return null;
        }
    }

    @Override
    public boolean cancelNotifications(GamePlayScreen gamePlayScreen) {
        var atp = gamePlayScreen.getCell().getCellATP();
        return (atp == 0 || atp >= ATP_THRESHOLD);
    }
}
