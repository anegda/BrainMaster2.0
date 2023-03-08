package com.example.brainmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class JuegoPalabrasTablero extends AppCompatActivity {
    String pIdioma;
    static ClasePalabrasJuego juego;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //MANTENER ELEMENTOS EN HORIZONTAL
        if (savedInstanceState != null) {
            pIdioma = savedInstanceState.getString("idiomaAct");
            cambiarIdioma(pIdioma);
        }
        else{
            Locale locale = getResources().getConfiguration().getLocales().get(0);
            pIdioma = locale.getLanguage();
            getIntent().putExtra("idiomaAct",pIdioma);
            juego = new ClasePalabrasJuego(this);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_palabras_tablero);

        TextView palabraText = (TextView) findViewById(R.id.palabraText);
        String palabraAct = juego.getPalabra();
        palabraText.setText(palabraAct);

        //AÑADIMOS FUNCIONALIDAD AL BOTÓN NUEVO
        Button btn_nuevo = (Button) findViewById(R.id.btn_nuevo);
        btn_nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView palabraText = (TextView) findViewById(R.id.palabraText);
                String palabraAct = (String) palabraText.getText();

                boolean seguir = juego.comprobarRespuesta(false,palabraAct);
                if(!seguir){
                    miBD GestorBD = new miBD(JuegoPalabrasTablero.this, "BrainMaster", null, 1);
                    SQLiteDatabase bd = GestorBD.getWritableDatabase();
                    String nombre = "anegda";
                    int puntos =juego.getPuntos();
                    bd.execSQL("INSERT INTO Partidas ('usuario', 'puntos') VALUES ('" + nombre + "'," + puntos + ")");
                    finish();
                }
                //NUEVA PALABRA
                String nuevaPalabra = juego.getPalabra();
                palabraText.setText(nuevaPalabra);
            }
        });

        //AÑADIMOS FUNCIONALIDAD AL BOTÓN VISTO
        Button btn_visto = (Button) findViewById(R.id.btn_visto);
        btn_visto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView palabraText = (TextView) findViewById(R.id.palabraText);
                String palabraAct = (String) palabraText.getText();

                boolean seguir = juego.comprobarRespuesta(true,palabraAct);
                Log.d("DAS", palabraAct);
                if(!seguir){
                    finish();
                }
                //NUEVA PALABRA
                String nuevaPalabra = juego.getPalabra();
                palabraText.setText(nuevaPalabra);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        String idiomaAct = getIntent().getStringExtra("idiomaAct");
        savedInstanceState.putString("idiomaAct", idiomaAct);

        //GUARDAR PALABRA ACTUAL
        TextView palabraText = (TextView) findViewById(R.id.palabraText);
        String palabraAct = (String) palabraText.getText();
        savedInstanceState.putString("palabraAct", palabraAct);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String idiomaAct = savedInstanceState.getString("idiomaAct");
        pIdioma = idiomaAct;

        //PONGO LA PALABRA
        String palabraAct = savedInstanceState.getString("palabraAct");
        TextView palabraText = (TextView) findViewById(R.id.palabraText);
        palabraText.setText(palabraAct);
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