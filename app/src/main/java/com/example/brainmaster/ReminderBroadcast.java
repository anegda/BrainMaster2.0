package com.example.brainmaster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class ReminderBroadcast extends BroadcastReceiver {
    /**
     * El uso de un AlarmManager y un BroadcastReceiver fue implementado antes de dar la clase correspondiente.
     * Codigo basado en: https://www.youtube.com/watch?v=nl-dheVpt8o
     * Autor: Lemubit Academy
     * Modificado por Ane García para adaptarlo a las necesidades de mi proyecto.
     **/
    @Override
    public void onReceive(Context context, Intent intent) {
        //CREAMOS LA NOTIFICACIÓN
        //Codigo basado en los apuntes de egela: Tema 05 - Dialogs y notificaciones
        Notification.Builder builder = new Notification.Builder(context, "notifyDaily");
        builder.setSmallIcon(R.drawable.logo)
                .setContentTitle(context.getResources().getString(R.string.noti2Titulo))
                .setContentText(context.getResources().getString(R.string.noti2Contenido));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel("notifyDaily", "Canal Notificación Diaria" , importance) ;
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel) ;
        }
        notificationManager.notify(200, builder.build());
    }
}