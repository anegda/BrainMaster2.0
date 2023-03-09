package com.example.brainmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.Locale;

public class Menu extends AppCompatActivity {
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

        //ESTABLECER TEMA UTILIZANDO PREFERENCIAS
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
        int[] logos={R.drawable.botones, R.drawable.palabras, R.drawable.trofeo};
        String [] nombres={getString(R.string.botones), getString(R.string.palabras), getString(R.string.ranking)};
        double [] dificultad={2, 1, 0};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ListView juegos = (ListView) findViewById(R.id.listaRanking);
        AdaptadorListView eladap = new AdaptadorListView(getApplicationContext(), nombres, logos, dificultad);
        juegos.setAdapter(eladap);

        //ONCLICK DE LA LISTA
        juegos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    startActivity(new Intent(Menu.this, JuegoBotonesTablero.class));
                }
                else if(position==1){
                    startActivity(new Intent(Menu.this, JuegoPalabrasTablero.class));
                }
                else if(position==2){
                    startActivity(new Intent(Menu.this, Ranking.class));
                }
            }
        });

        //BOTÓN DE AJUSTES
        ImageButton btn_ajustes = (ImageButton) findViewById(R.id.ajustes);
        btn_ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Menu.this, Ajustes.class));
            }
        });

        //BOTÓN COMPARTIR
        ImageButton btn_compartir = (ImageButton) findViewById(R.id.share);
        btn_compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //https://www.programaenlinea.net/crear-boton-compartir-android/
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