package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.graphics.Color;

public class NoAtpSource implements NotificationSource {
    private static final String TEXT = "DANGER: No ATP! You're dying!";
    private static final Float DURATION = null;
    private static final Color COLOR = Color.RED;

    private final NotificationManager notificationManager;

    public NoAtpSource(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @Override
    public Notification createNotification(GamePlayScreen gamePlayScreen) {
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();
        var atp = gamePlayScreen.getCell().getCellATP();
        if (atp == 0 && !notificationManager.hasNotificationFrom(this)) {
            return new Notification(graphicsProvider, assetManager, this, TEXT, DURATION, COLOR);
        } else {
            return null;
        }
    }

    @Override
    public boolean cancelNotifications(GamePlayScreen gamePlayScreen) {
        var atp = gamePlayScreen.getCell().getCellATP();
        return (atp > 0);
    }
}
