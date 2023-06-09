package com.example.brainmaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        //ELEGIMOS LA FRASE SEGÚN EL IDIOMA (por defecto español)
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String idioma = prefs.getString("idiomapref","es");
        InputStream fich = context.getResources().openRawResource(R.raw.frases);

        if(idioma.equals("es")){
            fich = context.getResources().openRawResource(R.raw.frases);
        }else if(idioma.equals("en")){
            fich = context.getResources().openRawResource(R.raw.sentences);
        }else if(idioma.equals("eu")){
            fich = context.getResources().openRawResource(R.raw.esaldiak);
        }
        BufferedReader buff = new BufferedReader(new InputStreamReader(fich));
        ArrayList<String> frases = new ArrayList<String>();
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
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.etiquetaWidget, frase);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 7475, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Calendar calendar = Calendar.getInstance();
        long triggerAtMillis = calendar.getTimeInMillis();
        am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, 60*1000, pi);
    }

    @Override
    public void onDisabled(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 7475, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        am.cancel(pi);
    }

}