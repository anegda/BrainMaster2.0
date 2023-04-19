package com.example.brainmaster;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);
        Calendar calendario = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("HH:mm:ss");
        String horaconformato = formato.format(calendario.getTime());
        remoteViews.setTextViewText(R.id.etiquetaWidget, horaconformato);
        ComponentName tipowidget = new ComponentName(context, Widget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(tipowidget, remoteViews);
    }
}
