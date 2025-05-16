package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.graphics.Color;

/**
 * LowATPSource Class
 * <p>
 * Controls the popup indicating that the player is Low on ATP
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class LowATPSource implements NotificationSource {
    private static final String CONFIG_KEY = "lowATPNotification";
    private static final String DEFAULT_TEXT = "WARNING: Low ATP. Find some glucose.";
    private static final int ATP_THRESHOLD = 20;
    private static final Float DURATION = null;
    private static final Color COLOR = Color.WHITE;

    private final NotificationManager notificationManager;
    private final String text;

    public LowATPSource(ConfigProvider configProvider, NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
        this.text = configProvider.getStringOrDefault(CONFIG_KEY, DEFAULT_TEXT);
    }

    @Override
    public Notification createNotification(GamePlayScreen gamePlayScreen) {
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();
        var atp = gamePlayScreen.getCell().getCellATP();
        if (0 < atp && atp < ATP_THRESHOLD && !notificationManager.hasNotificationFrom(this)) {
            return new Notification(graphicsProvider, assetManager, this, text, DURATION, COLOR);
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
