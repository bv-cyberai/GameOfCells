package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.graphics.Color;

/**
 * LowHealthSource Class
 * <p>
 * Controls the popup indicating that the player is low on health
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class LowHealthSource implements NotificationSource {
    private static final String CONFIG_KEY = "lowHealthNotification";
    private static final String DEFAULT_TEXT = "DANGER: Low Health!!!";
    private static final float HEALTH_THRESHOLD = 30;
    private static final Float DURATION = null;
    private static final Color DARK_RED = new Color(0.7f, 0, 0, 1);
    private static final Color COLOR = DARK_RED;

    private final NotificationManager notificationManager;
    private final String text;

    public LowHealthSource(ConfigProvider configProvider, NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
        this.text = configProvider.getStringOrDefault(CONFIG_KEY, DEFAULT_TEXT);
    }

    @Override
    public Notification createNotification(GamePlayScreen gamePlayScreen) {
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();
        var health = gamePlayScreen.getCell().getCellHealth();
        if (0 < health && health < HEALTH_THRESHOLD && !notificationManager.hasNotificationFrom(this)) {
            return new Notification(graphicsProvider, assetManager, this, text, DURATION, COLOR);
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
