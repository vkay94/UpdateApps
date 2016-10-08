package de.vkay.updateapps.Benachrichtigungen;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import de.vkay.updateapps.Main;
import de.vkay.updateapps.R;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //String topicName = Sonst.splitGetSecond("/topics/", remoteMessage.getFrom());

        showNotification(
                remoteMessage.getData().get("pMessage"),
                remoteMessage.getData().get("pTitle"));

    }

    private void showNotification(String message, String title) {

        Intent i = new Intent(this, Main.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("notification_recieved", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_update_notification)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        manager.notify(0,builder.build());
    }
}
