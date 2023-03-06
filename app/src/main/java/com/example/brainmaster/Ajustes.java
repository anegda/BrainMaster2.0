package com.example.brainmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.Locale;

public class Ajustes extends AppCompatActivity {
    int tema=1;
    String pIdioma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //MANTENER IDIOMA EN HORIZONTAL
        if (savedInstanceState != null) {
            pIdioma = savedInstanceState.getString("idiomaAct");
            cambiarIdioma(pIdioma);
        }
        else{
            Locale locale = getResources().getConfiguration().getLocales().get(0);
            pIdioma = locale.getLanguage();
            getIntent().putExtra("idiomaAct",pIdioma);
        }

        //APLICAR TEMA
        if (getIntent().hasExtra("bundle") && savedInstanceState==null){
            savedInstanceState = getIntent().getExtras().getBundle("bundle");
            tema = getIntent().getExtras().getInt("tema");
        }

        if(tema==1) {
            setTheme(R.style.Theme_BrainMaster);

        }
        else if(tema==2){
            setTheme(R.style.Theme_BrainMasterSummer);
        }
        else{
            setTheme(R.style.Theme_BrainMasterPunk);
        }

        //CREAR INTERFAZ
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        //DESPLEGABLES
        //https://code.tutsplus.com/es/tutorials/how-to-add-a-dropdown-menu-in-android-studio--cms-37860
        Spinner temas = findViewById(R.id.temasDes);
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this, R.array.temas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        temas.setAdapter(adapter);

        //NOTIFICACIÓN PERIÓDICA
        //https://www.youtube.com/watch?v=nl-dheVpt8o
        //https://developer.android.com/training/scheduling/alarms?hl=es-419
        //https://www.tutorialspoint.com/how-to-create-everyday-notifications-at-certain-time-in-android
        Button btn_notis = (Button) findViewById(R.id.btn_notis);
        btn_notis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Ajustes.this, ReminderBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(Ajustes.this,0,intent,PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                long timeAtButtonClick = System.currentTimeMillis();

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeAtButtonClick,1000*60*60*24, pendingIntent);
            }
        });

        //APLICAR CONFIGURACIÓN Y PREFERENCIAS
        Button btn_guardar = (Button) findViewById(R.id.btn_guardar);
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Notificaciones
                NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(Ajustes.this,"notifySimple");
                elBuilder.setSmallIcon(R.drawable.logo)
                        .setContentTitle(getString(R.string.noti1Titulo))
                        .setContentText(getString(R.string.noti1Contenido))
                        .setVibrate(new long[] {0,500,100,1000})
                        .setAutoCancel(true);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    NotificationChannel elCanal = new NotificationChannel("notifySimple","Canal notificación simple",NotificationManager.IMPORTANCE_DEFAULT);
                    elCanal.setDescription("Canal notificación simple");
                    elCanal.enableLights(true);
                    elCanal.setLightColor(Color.RED);
                    elCanal.setVibrationPattern(new long[]{0, 500, 100, 1000});
                    elCanal.enableVibration(true);
                    elManager.createNotificationChannel(elCanal);
                }
                elManager.notify(11, elBuilder.build());

                //Temas
                Spinner temas = findViewById(R.id.temasDes);
                String temaElegido = temas.getSelectedItem().toString();
                String[] temaElegidoS = temaElegido.split(" ");
                int valor = Integer.parseInt(temaElegidoS[0]);

                Bundle temp_bundle = new Bundle();
                onSaveInstanceState(temp_bundle);
                Intent intent = new Intent(Ajustes.this, Ajustes.class);
                intent.putExtra("bundle", temp_bundle);
                intent.putExtra("tema", valor);
                setResult(RESULT_OK, intent);

                startActivity(intent);
                finish();
            }
        });

        //COMPARTIR
        //https://www.programaenlinea.net/crear-boton-compartir-android/
        ImageButton btn_compartir = (ImageButton) findViewById(R.id.btn_compartir);
        btn_compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent compartir = new Intent(android.content.Intent.ACTION_SEND);
                compartir.setType("text/plain");
                compartir.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                compartir.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.compartirContenido));
                startActivity(Intent.createChooser(compartir, "Compartir vía"));
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        String idiomaAct = getIntent().getStringExtra("idiomaAct");
        savedInstanceState.putString("idiomaAct", idiomaAct);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String idiomaAct = savedInstanceState.getString("idiomaAct");
        pIdioma = idiomaAct;
    }

    protected void cambiarIdioma(String idioma){
        Log.d("DAS",idioma);
        Locale nuevaloc = new Locale(idioma);
        Locale.setDefault(nuevaloc);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }
}