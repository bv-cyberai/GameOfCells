package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.graphics.Color;

/**
 * CanBuyFirstUpgradeSource Class
 * <p>
 * Controls the popup indicating that the player is losing health
 * due to the lack of ATP.
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class NoATPSource implements NotificationSource {
    private static final String CONFIG_KEY = "noATPNotification";
    private static final String DEFAULT_TEXT = "DANGER: No ATP! You're losing health!";
    private static final Float DURATION = null;
    private static final Color COLOR = Color.RED;

    private final NotificationManager notificationManager;
    private final String text;

    public NoATPSource(ConfigProvider configProvider, NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
        this.text = configProvider.getStringOrDefault(CONFIG_KEY, DEFAULT_TEXT);
    }

    @Override
    public Notification createNotification(GamePlayScreen gamePlayScreen) {
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();
        var atp = gamePlayScreen.getCell().getCellATP();
        if (atp == 0 && !notificationManager.hasNotificationFrom(this)) {
            return new Notification(graphicsProvider, assetManager, this, text, DURATION, COLOR);
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
