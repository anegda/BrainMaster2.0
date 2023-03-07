package com.example.brainmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;
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

        //CREAR INTERFAZ
        int[] logos={R.drawable.botones, R.drawable.palabras, R.drawable.trofeo};
        String [] nombres={getString(R.string.botones), getString(R.string.palabras), getString(R.string.ranking)};
        double [] dificultad={2, 1, 0};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ListView juegos = (ListView) findViewById(R.id.lista);
        AdaptadorListView eladap = new AdaptadorListView(getApplicationContext(), nombres, logos, dificultad);
        juegos.setAdapter(eladap);

        //ONCLICK DE LA LISTA
        juegos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    startActivity(new Intent(Menu.this, JuegoBotonesTablero.class));
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