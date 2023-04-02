package com.example.brainmaster;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Locale;

public class Preferencias extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    /**
     * Codigo basado en los apuntes de egela: Tema 08 - Almacenamiento de información local
     **/

    @Override
    public void onCreatePreferences (Bundle savedInstanceState, String rootKey){
        addPreferencesFromResource(R.xml.pref_config);
    }

    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String s){
        switch (s) {
            case "switch":
                Boolean notis = sharedPreferences.getBoolean("switch",false);
                if(notis){
                    /**
                     * El uso de un AlarmManager y un BroadcastReceiver fue implementado antes de dar la clase correspondiente.
                     * Codigo basado en: https://www.youtube.com/watch?v=nl-dheVpt8o
                     * Autor: Lemubit Academy
                     * Modificado por Ane García para adaptarlo a las necesidades de mi proyecto.
                     **/
                    //NOTIFICACIÓN PERIÓDICA
                    Intent intent = new Intent(getContext(), ReminderBroadcast.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),0,intent,PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);

                    long timeAtButtonClick = System.currentTimeMillis();

                    // EL USO DEL ALARM MANAGER LO OBTUVE DE LA DOCUMENTACIÓN OFICIAL
                    // https://developer.android.com/training/scheduling/alarms?hl=es-419
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeAtButtonClick,1000*60*60*24, pendingIntent);
                }else{
                    //CANCELAMOS LA NOTIFICACIÓN DIARIA, INFORMACIÓN EN LA DOCUMENTACIÓN OFICIAL
                    // https://developer.android.com/training/scheduling/alarms?hl=es-419
                    Intent intent = new Intent(getContext(), ReminderBroadcast.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);

                    alarmManager.cancel(pendingIntent);
                }
                break;
            case "temapref":
                startActivity(getActivity().getIntent());
                getActivity().finish();
                break;
            case "idiomapref":
                //CAMBIO DE IDIOMA
                //CÓDIGO BASADO EN LOS APUNTES DE EGELA: Laboratorio 02 - Trabajo con interfaces gráficas e idiomas
                String idioma = sharedPreferences.getString("idiomapref","es");

                Log.d("DAS",idioma);
                Locale nuevaloc = new Locale(idioma);
                Locale.setDefault(nuevaloc);
                Configuration configuration = getContext().getResources().getConfiguration();
                configuration.setLocale(nuevaloc);
                configuration.setLayoutDirection(nuevaloc);

                Context context = getContext().createConfigurationContext(configuration);
                getContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
                startActivity(getActivity().getIntent());
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
