package com.example.brainmaster;

import android.Manifest;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //NOTIFICACIONES PERMISOS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
        }

        //BOTONES LOGIN Y REGISTRO
        Button btn_reg = (Button) findViewById(R.id.btn_registrar);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //NOTIFICACIÓN PERIÓDICA
                //https://www.youtube.com/watch?v=nl-dheVpt8o
                //https://developer.android.com/training/scheduling/alarms?hl=es-419
                //https://www.tutorialspoint.com/how-to-create-everyday-notifications-at-certain-time-in-android
                Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                long timeAtButtonClick = System.currentTimeMillis();

                alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick+5000, pendingIntent);

                startActivity(new Intent(MainActivity.this, Registro.class));
            }
        });

        Button btn_log = (Button) findViewById(R.id.btn_entrar);
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        //DIÁLOGO DEL BOTÓN IDIOMAS
        ImageButton btn_idiomas = (ImageButton) findViewById(R.id.idiomas);
        btn_idiomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DAS","Botón pulsado");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.idiomas));
                final CharSequence[] opciones = {"English", "Español", "Euskera"};
                builder.setSingleChoiceItems(opciones, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            Locale nuevaloc = new Locale("en");
                            Locale.setDefault(nuevaloc);
                            Configuration configuration = getBaseContext().getResources().getConfiguration();
                            configuration.setLocale(nuevaloc);
                            configuration.setLayoutDirection(nuevaloc);

                            Context context = getBaseContext().createConfigurationContext(configuration);
                            getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

                            finish();
                            startActivity(getIntent());
                        }
                        else if(i==1){
                            Locale nuevaloc = new Locale("es");
                            Locale.setDefault(nuevaloc);
                            Configuration configuration = getBaseContext().getResources().getConfiguration();
                            configuration.setLocale(nuevaloc);
                            configuration.setLayoutDirection(nuevaloc);

                            Context context = getBaseContext().createConfigurationContext(configuration);
                            getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

                            finish();
                            startActivity(getIntent());
                        }
                        else{
                            Locale nuevaloc = new Locale("eu");
                            Locale.setDefault(nuevaloc);
                            Configuration configuration = getBaseContext().getResources().getConfiguration();
                            configuration.setLocale(nuevaloc);
                            configuration.setLayoutDirection(nuevaloc);

                            Context context = getBaseContext().createConfigurationContext(configuration);
                            getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

                            finish();
                            startActivity(getIntent());
                        }
                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
            }
        });
    }
}