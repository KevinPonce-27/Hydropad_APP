package com.psdev.iot_hydropad;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.provider.Settings;

public class NotificationTurbidity {

    private static final String CHANNEL_ID = "turbidity_channel";
    private final Context context;

    public NotificationTurbidity(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    public void sendNotification(int turbidityLevel) {
        String contentTitle = "Nivel de Turbidez del Agua";
        String contentText;

        if (turbidityLevel >= 0 && turbidityLevel <= 10) {
            contentText = "Limpia y apta al consumo.";
        } else if (turbidityLevel <= 20) {
            contentText = "Poco sucia y posiblemente apta al consumo.";
        } else if (turbidityLevel <= 50) {
            contentText = "Medianamente sucia y no apta al consumo.";
        } else if (turbidityLevel <= 200) {
            contentText = "Sucia, es posible que debas limpiar los sensores.";
        } else {
            contentText = "Totalmente sucia, necesitas limpiar los sensores urgentemente.";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_water_10) // Cambia este ícono con el ícono de tu app.
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(3, builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Turbidity Channel";
            String description = "Channel for water turbidity notification";
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
