package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.utils.Null;

/**
 * A source of notifications.
 * createNotifications queries game state to determine whether to create a new notification.
 * cancelNotifications queries game state to determine whether to cancel all notifications from this source.
 */
public interface NotificationSource {

    /**
     * Determine whether this source should create a notification, and creates it.
     *
     * @param gamePlayScreen Logically, should take in game state. But we have what we have.
     * @return A notification, if one should be created, or null if none should be created.
     */
    @Null
    Notification createNotification(GamePlayScreen gamePlayScreen);

    /**
     * Determine whether all notifications from this source should be cancelled
     * (for example, for the acid notification source, because the cell left an acid zone).
     */
    boolean cancelNotifications(GamePlayScreen gamePlayScreen);
}
