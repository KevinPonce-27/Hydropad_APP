package com.psdev.iot_hydropad;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.provider.Settings;


public class NotificationPH {

    private static final String CHANNEL_ID = "ph_channel";
    private final Context context;



    public NotificationPH(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    public void sendNotification(float phLevel) {
        String contentTitle;
        String contentText;

        if (phLevel >= 0 && phLevel <= 5 || phLevel >= 8 && phLevel <= 14) {
            contentTitle = "Nivel de pH no seguro";
            contentText = "El agua no es consumible. pH: " + phLevel;
        } else if (phLevel >= 6 && phLevel <= 7) {
            contentTitle = "Nivel de pH seguro";
            contentText = "El agua es potable y totalmente consumible. pH: " + phLevel;
        } else {
            contentTitle = "Nivel de pH desconocido";
            contentText = "No se puede determinar la calidad del agua. pH: " + phLevel;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_water_10) // Cambia este ícono con el ícono de tu app.
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
                 builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);



        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(2, builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PH Channel";
            String description = "Channel for pH notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}