package com.example.brainmaster;

import android.Manifest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    int pTema;
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

        //CREAMOS LA ACTIVIDAD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //NOTIFICACIONES PERMISOS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
        }

        //BOTÓN REGISTRO
        Button btn_reg = (Button) findViewById(R.id.btn_registrar);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Registro.class);
                startActivity(intent);
            }
        });

        //BOTÓN LOGIN
        Button btn_log = (Button) findViewById(R.id.btn_entrar);
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });

        //DIÁLOGO DEL BOTÓN IDIOMAS
        ImageButton btn_idiomas = (ImageButton) findViewById(R.id.idiomas);
        btn_idiomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.idiomas));
                final CharSequence[] opciones = {"English", "Español", "Euskera"};
                builder.setSingleChoiceItems(opciones, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            pIdioma="en";
                        }
                        else if(i==1){
                            pIdioma="es";
                        }
                        else{
                            pIdioma="eu";
                        }
                        cambiarIdioma(pIdioma);
                        getIntent().putExtra("idiomaAct",pIdioma);
                        finish();
                        startActivity(getIntent());
                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
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