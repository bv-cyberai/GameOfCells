package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.objects.Zone;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.graphics.Color;

/**
 * AcidZoneSource Class
 * <p>
 * Creates warning notifications when the player is in an acid zone.
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class AcidZoneSource implements NotificationSource {
    private static final String CONFIG_KEY = "acidZoneNotification";
    private static final String DEFAULT_TEXT = "DANGER: Acid zone! You're taking damage!";
    private static final Float DURATION = null;
    private static final Color COLOR = Color.WHITE;

    private final NotificationManager notificationManager;
    private final String text;

    /**
     * Create an acid zone source.
     */
    public AcidZoneSource(ConfigProvider configProvider, NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
        text = configProvider.getStringOrDefault(CONFIG_KEY, DEFAULT_TEXT);
    }

    @Override
    public Notification createNotification(GamePlayScreen gamePlayScreen) {
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();
        if (cellInAcidZone(gamePlayScreen) && !notificationManager.hasNotificationFrom(this)) {
            return new Notification(graphicsProvider, assetManager, this, text, DURATION, COLOR);
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
