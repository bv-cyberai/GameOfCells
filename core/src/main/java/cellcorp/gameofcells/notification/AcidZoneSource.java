package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.objects.Zone;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.graphics.Color;

/**
 * Creates warning notifications when the player is in an acid zone.
 */
public class AcidZoneSource implements NotificationSource {
    private static final String TEXT = "DANGER: Acid zone! You're taking damage!";
    private static final Float DURATION = null;
    private static final Color COLOR = Color.YELLOW;

    private final NotificationManager notificationManager;

    public AcidZoneSource(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @Override
    public Notification createNotification(GamePlayScreen gamePlayScreen) {
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();
        if (cellInAcidZone(gamePlayScreen) && !notificationManager.hasNotificationFrom(this)) {
            return new Notification(graphicsProvider, assetManager, this, TEXT, DURATION, COLOR);
        } else {
            return null;
        }
    }

    @Override
    public boolean cancelNotifications(GamePlayScreen gamePlayScreen) {
        return !(cellInAcidZone(gamePlayScreen));
    }

    private boolean cellInAcidZone(GamePlayScreen gamePlayScreen) {
        var zoneManager = gamePlayScreen.getZoneManager();
        var cell = gamePlayScreen.getCell();
        return zoneManager.distanceToNearestAcidZone(cell.getX(), cell.getY())
                .map(d -> d <= Zone.ZONE_RADIUS)
                .orElse(false);
    }
}
