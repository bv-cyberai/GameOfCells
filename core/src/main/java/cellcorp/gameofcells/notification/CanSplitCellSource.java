package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.graphics.Color;

public class CanSplitCellSource implements NotificationSource {
    private static final String TEXT = "Press U to divide the cell.";
    private static final Float DURATION = null;
    private static final Color COLOR = Color.WHITE;

    private final NotificationManager notificationManager;

    public CanSplitCellSource(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @Override
    public Notification createNotification(GamePlayScreen gamePlayScreen) {
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();
        if (gamePlayScreen.getCell().hasNucleus()
                && !gamePlayScreen.getCell().hasSplit()
                && !notificationManager.hasNotificationFrom(this)) {
            return new Notification(graphicsProvider, assetManager, this, TEXT, DURATION, COLOR);
        } else {
            return null;
        }
    }

    @Override
    public boolean cancelNotifications(GamePlayScreen gamePlayScreen) {
        return gamePlayScreen.getCell().hasSplit();
    }
}
