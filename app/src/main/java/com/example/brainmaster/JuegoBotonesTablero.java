package com.example.brainmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class JuegoBotonesTablero extends AppCompatActivity implements FragmentBotonesTablero.listenerDelFragment{
    static ArrayList<Integer> solucion;
    static ClaseBotonesJuego juego;
    static String pIdioma;

    Runnable ronda = new Runnable() {
        @Override
        public void run() {
            ArrayList<Integer> ronda = juego.getSecuencia();
            long step = 2000;
            long numRonda = 0;
            Handler handler = new Handler();
            pararUsoBotones();
            for (Integer i : ronda) {
                //ESPERAMOS ANTES DE ENCENDERLO
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int btn_id = getResources().getIdentifier("button" + Integer.toString(i), "id", getPackageName());
                        Button btn_act = (Button) findViewById(btn_id);
                        btn_act.setBackgroundColor(Color.RED);
                    }
                },1000+step*numRonda);

                //ESPERAMOS UN SEGUNDO ANTES DE APAGARLO
                handler.postDelayed(new Runnable() {
                    public void run() {
                        int btn_id = getResources().getIdentifier("button" + Integer.toString(i), "id", getPackageName());
                        Button btn_act = (Button) findViewById(btn_id);
                        btn_act.setBackgroundColor(getColor(R.color.purple_500));
                    }
                }, 2000+step*numRonda);
                numRonda = numRonda +1;
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reanudarUsoBotones();
                }
            }, 1000 + step * numRonda);
        }
    };
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
            juego = new ClaseBotonesJuego();
            solucion = new ArrayList<Integer>();
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_botones_tablero);

        Handler handler = new Handler();
        handler.postDelayed(ronda,1000);

        Button btn_enter = findViewById(R.id.btn_enter);
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(juego.comparar(solucion)){
                    solucion = new ArrayList<Integer>();
                    Handler handler = new Handler();
                    handler.postDelayed(ronda,1000);
                }
                else{
                    miBD GestorBD = new miBD(JuegoBotonesTablero.this, "BrainMaster", null, 1);
                    SQLiteDatabase bd = GestorBD.getWritableDatabase();
                    String nombre = "anegda";
                    int puntos =juego.getPuntos();
                    bd.execSQL("INSERT INTO Partidas ('usuario', 'puntos') VALUES ('" + nombre + "'," + puntos + ")");
                    finish();
                }
            }
        });
    }

    @Override
    public void enviarInformacion(int num) {
        Log.d("DAS",Integer.toString(num));
        solucion.add(num);
    }

    public void pararUsoBotones(){
        //QUITAMOS LA FUNCIONALIDAD
        findViewById(R.id.button1).setEnabled(false);
        findViewById(R.id.button2).setEnabled(false);
        findViewById(R.id.button3).setEnabled(false);
        findViewById(R.id.button4).setEnabled(false);
        findViewById(R.id.button5).setEnabled(false);
        findViewById(R.id.button6).setEnabled(false);
        findViewById(R.id.button7).setEnabled(false);
        findViewById(R.id.button8).setEnabled(false);
        findViewById(R.id.button9).setEnabled(false);
    }

    public void reanudarUsoBotones(){
        findViewById(R.id.button1).setEnabled(true);
        findViewById(R.id.button2).setEnabled(true);
        findViewById(R.id.button3).setEnabled(true);
        findViewById(R.id.button4).setEnabled(true);
        findViewById(R.id.button5).setEnabled(true);
        findViewById(R.id.button6).setEnabled(true);
        findViewById(R.id.button7).setEnabled(true);
        findViewById(R.id.button8).setEnabled(true);
        findViewById(R.id.button9).setEnabled(true);
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