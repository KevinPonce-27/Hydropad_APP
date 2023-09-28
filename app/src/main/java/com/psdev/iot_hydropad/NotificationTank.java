package com.psdev.iot_hydropad;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.provider.Settings;

public class NotificationTank {

    private static final String CHANNEL_ID = "tank_channel";
    private final Context context;

    public NotificationTank(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    public void sendNotification(int tankLevel) {
        String contentTitle = "Nivel del Tanque";
        String contentText;

        if (tankLevel == 0) {
            contentText = "El tanque está vacío.";
        } else if (tankLevel <= 20) {
            contentText = "El tanque está en nivel muy bajo.";
        } else if (tankLevel <= 50) {
            contentText = "El tanque se encuentra a la mitad.";
        } else if (tankLevel == 100) {
            contentText = "El tanque se encuentra lleno.";
        } else {
            contentText = "Nivel del tanque: " + tankLevel + "%.";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_water_10) // Cambia este ícono con el ícono de tu app.
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1, builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Tank Channel";
            String description = "Channel for tank level notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI,
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build());

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
