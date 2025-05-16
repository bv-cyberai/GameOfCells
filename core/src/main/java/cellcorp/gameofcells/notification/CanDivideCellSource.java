package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.graphics.Color;

/**
 * CanDivideCellSource Class
 * <p>
 * Controls the popup indicating that the player can divide
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */

public class CanDivideCellSource implements NotificationSource {
    private static final String CONFIG_KEY = "canDivideCellNotification";
    private static final String DEFAULT_TEXT = "Press U to divide the cell.";
    private static final Float DURATION = null;
    private static final Color COLOR = Color.WHITE;

    private final NotificationManager notificationManager;
    private final String text;

    /**
     * Creates this source
     */
    public CanDivideCellSource(ConfigProvider configProvider, NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
        this.text = configProvider.getStringOrDefault(CONFIG_KEY, DEFAULT_TEXT);
    }

    @Override
    public Notification createNotification(GamePlayScreen gamePlayScreen) {
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();
        if (gamePlayScreen.getCell().hasNucleus()
            && !gamePlayScreen.getCell().hasSplit()
            && !notificationManager.hasNotificationFrom(this)) {
            return new Notification(graphicsProvider, assetManager, this, text, DURATION, COLOR);
        } else {
            return null;
        }
    }

    @Override
    public boolean cancelNotifications(GamePlayScreen gamePlayScreen) {
        return gamePlayScreen.getCell().hasSplit();
    }
}
