package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.objects.size.SmallSizeUpgrade;
import cellcorp.gameofcells.providers.ConfigProvider;
import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.graphics.Color;

/**
 * CanBuyFirstUpgradeSource Class
 * <p>
 * Controls the popup indicating that the player can purchase
 * their first upgrade
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 02/18/2025
 * @course CIS 405
 * @assignment GameOfCells
 */
public class CanBuyFirstUpgradeSource implements NotificationSource {
    private static final String CONFIG_KEY = "canBuyFirstUpgradeNotification";
    private static final String DEFAULT_TEXT = "You can buy an upgrade!\nPress Q to open the shop";
    private static final Float DURATION = null;
    private static final Color COLOR = Color.WHITE;

    private final NotificationManager notificationManager;
    private final String text;

    /**
     * Create this source.
     */
    public CanBuyFirstUpgradeSource(ConfigProvider configProvider, NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
        this.text = configProvider.getStringOrDefault(CONFIG_KEY, DEFAULT_TEXT);
    }

    @Override
    public Notification createNotification(GamePlayScreen gamePlayScreen) {
        var graphicsProvider = gamePlayScreen.getGraphicsProvider();
        var assetManager = gamePlayScreen.getAssetManager();

        var cell = gamePlayScreen.getCell();
        var atp = cell.getCellATP();
        if (atp >= SmallSizeUpgrade.ATP_COST
            && !cell.hasSmallSizeUpgrade()
            && !notificationManager.hasNotificationFrom(this)) {
            return new Notification(graphicsProvider, assetManager, this, text, DURATION, COLOR);
        } else {
            return null;
        }
    }

    @Override
    public boolean cancelNotifications(GamePlayScreen gamePlayScreen) {
        var cell = gamePlayScreen.getCell();
        var atp = cell.getCellATP();
        return atp < SmallSizeUpgrade.ATP_COST || cell.hasSmallSizeUpgrade();
    }
}
