package cellcorp.gameofcells.notification;

import cellcorp.gameofcells.screens.GamePlayScreen;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;
import java.util.List;


public class NotificationManager {
    private static final float PADDING = 10;

    private final List<NotificationSource> sources;

    private final GamePlayScreen gamePlayScreen;
    private final List<Notification> notifications;
    private final Table table = new Table();

    public NotificationManager(GamePlayScreen gamePlayScreen) {
        sources = List.of(
                new AcidZoneSource(this),
                new LowAtpSource(this),
                new NoAtpSource(this),
                new LowHealthSource(this),
                new CanSplitCellSource(this)
        );

        this.gamePlayScreen = gamePlayScreen;
        this.notifications = new ArrayList<>();
    }

    public Table getTable() {
        return table;
    }

    /**
     * Add a notification to the notification system.
     * This method is used to add a notification to the notification system with a specified message, duration, and color.
     */
    public void update(float deltaTime) {
        for (var source : sources) {
            var notification = source.createNotification(gamePlayScreen);
            if (notification != null) {
                addNotification(notification);
            }
            if (source.cancelNotifications(gamePlayScreen)) {
                cancelNotifications(source);
            }
        }

        List<Notification> notificationsToDelete = new ArrayList<>();
        for (var notification : notifications) {
            var deleteNotification = notification.update(deltaTime);
            if (deleteNotification) {
                notificationsToDelete.add(notification);
            }
        }

        if (!notificationsToDelete.isEmpty()) {
            notifications.removeAll(notificationsToDelete);
            rebuildTable();
        }
    }

    /**
     * Determines whether the given notification source has any existing notifications.
     */
    public boolean hasNotificationFrom(NotificationSource source) {
        return notifications.stream().anyMatch(notification -> notification.source() == source);
    }

    /**
     * Adds notification to notification list, and rebuilds the table.
     */
    private void addNotification(Notification notification) {
        notifications.add(notification);
        rebuildTable();
    }

    private void cancelNotifications(NotificationSource source) {
        for (var notification : notifications) {
            if (notification.source() == source) {
                notification.startFadeOut();
            }
        }
    }

    private void rebuildTable() {
        table.clear();
        for (var notification : notifications) {
            table.row().padBottom(PADDING);
            table.add(notification.getLabel());
        }
    }

    /**
     * For test use only.
     */
    public List<Notification> getNotifications() {
        return notifications;
    }
}
