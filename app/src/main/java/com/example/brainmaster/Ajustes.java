package com.example.brainmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import java.util.Locale;

public class Ajustes extends AppCompatActivity {
    int tema=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //QUITAMOS LA ACTION BAR
        getSupportActionBar().hide();

        //ESTABLECER IDIOMA USANDO PREFERENCIAS
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idiomapref","es");
        cambiarIdioma(idioma);

        //ESTABLECER TEMA UTILIZANDO PREFERENCIAS
        String tema = prefs.getString("temapref","1");
        if(tema.equals("1")) {
            Log.d("DAS",tema+" 1");
            setTheme(R.style.Theme_BrainMaster);
        }
        else if(tema.equals("2")){
            Log.d("DAS",tema+" 2");
            setTheme(R.style.Theme_BrainMasterSummer);
        }
        else if(tema.equals("3")){
            Log.d("DAS",tema+" 3");
            setTheme(R.style.Theme_BrainMasterPunk);
        }
        else{
            Log.d("DAS",tema+" 4");
            setTheme(R.style.Theme_BrainMaster);
        }

        //CREAR INTERFAZ
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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

    //AL SALIR ABRIMOS LA VENTANA DE MENÚ
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(R.drawable.logo).setTitle(getString(R.string.salir)).setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Ajustes.this, Menu.class));
                        finish();
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}