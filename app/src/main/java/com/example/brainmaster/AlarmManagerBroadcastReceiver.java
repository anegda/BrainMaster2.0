package com.example.brainmaster;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //ELEGIMOS LA FRASE
        ArrayList<String> frases = new ArrayList<String>();
        InputStream fich = context.getResources().openRawResource(R.raw.frases);
        BufferedReader buff = new BufferedReader(new InputStreamReader(fich));
        try {
            String linea = buff.readLine();
            while( linea != null) {
                frases.add(linea);
                linea = buff.readLine();
            }
            buff.close();
            fich.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.shuffle(frases);
        String frase = frases.get(0);

        //CAMBIAMOS EL TEXTVIEW
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);
        remoteViews.setTextViewText(R.id.etiquetaWidget, frase);
        ComponentName tipowidget = new ComponentName(context, Widget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(tipowidget, remoteViews);
    }
}
