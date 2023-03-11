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
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
            long step = 1000;
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
                        if(btn_act!=null) {
                            btn_act.setBackgroundColor(Color.RED);
                        }
                    }
                }, 1000 + step * numRonda);

                //ESPERAMOS UN SEGUNDO ANTES DE APAGARLO
                handler.postDelayed(new Runnable() {
                    public void run() {
                        int btn_id = getResources().getIdentifier("button" + Integer.toString(i), "id", getPackageName());
                        Button btn_act = (Button) findViewById(btn_id);
                        if(btn_act!=null) {
                            btn_act.setBackgroundColor(getColor(R.color.purple_500));
                        }
                    }
                }, 1500 + step * numRonda);
                numRonda = numRonda + 1;
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
                    //SI ESTÁ EN HORIZONTAL ACTUALIZAR EL OTRO FRAGMENT
                    int orientation = getResources().getConfiguration().orientation;
                    if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                        FragmentBotonesInfo elotro = (FragmentBotonesInfo) getSupportFragmentManager().findFragmentById(R.id.fragmentBotonesInfo);
                        elotro.actualizarPuntuacion(juego.getPuntos());
                    }

                    //NUEVA RONDA
                    solucion = new ArrayList<Integer>();
                    Handler handler = new Handler();
                    handler.postDelayed(ronda,1000);
                }
                else{
                    //INTRODUCIMOS LA PUNTUACIÓN A LA BD
                    miBD GestorBD = new miBD(JuegoBotonesTablero.this, "BrainMaster", null, 1);
                    SQLiteDatabase bd = GestorBD.getWritableDatabase();
                    String nombre = "anegda";
                    int puntos =juego.getPuntos();
                    bd.execSQL("INSERT INTO Partidas ('usuario', 'puntos', 'tipo') VALUES ('" + nombre + "'," + puntos + ", 'Botones')");

                    //DIÁLOGO DICIENDO QUE HAS PERDIDO
                    new AlertDialog.Builder(JuegoBotonesTablero.this).setIcon(R.drawable.logo).setTitle(getString(R.string.perder)).setMessage(getString(R.string.puntuacion)+" "+Integer.toString(puntos)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(JuegoBotonesTablero.this, Menu.class));
                        }
                    }).show();

                }
            }
        });

        //BOTÓN QUE EXPLICA LAS REGLAS
        ImageButton btn_reglasB = findViewById(R.id.btn_reglasB);
        btn_reglasB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(JuegoBotonesTablero.this).setIcon(R.drawable.logo).setTitle(getString(R.string.reglas)).setMessage(getString(R.string.reglasMB)).show();
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
        Button btn1 = (Button) findViewById(R.id.button1);
        Button btn2 = (Button) findViewById(R.id.button2);
        Button btn3 = (Button) findViewById(R.id.button3);
        Button btn4 = (Button) findViewById(R.id.button4);
        Button btn5 = (Button) findViewById(R.id.button5);
        Button btn6 = (Button) findViewById(R.id.button6);
        Button btn7 = (Button) findViewById(R.id.button7);
        Button btn8 = (Button) findViewById(R.id.button8);
        Button btn9 = (Button) findViewById(R.id.button9);

        if(btn1!=null && btn2!=null && btn3!=null && btn4!=null && btn5!=null && btn6!=null && btn7!=null && btn8!=null && btn9!=null) {
            btn1.setEnabled(false);
            btn2.setEnabled(false);
            btn3.setEnabled(false);
            btn4.setEnabled(false);
            btn5.setEnabled(false);
            btn6.setEnabled(false);
            btn7.setEnabled(false);
            btn8.setEnabled(false);
            btn9.setEnabled(false);
        }
    }

    public void reanudarUsoBotones(){
        //QUITAMOS LA FUNCIONALIDAD
        Button btn1 = (Button) findViewById(R.id.button1);
        Button btn2 = (Button) findViewById(R.id.button2);
        Button btn3 = (Button) findViewById(R.id.button3);
        Button btn4 = (Button) findViewById(R.id.button4);
        Button btn5 = (Button) findViewById(R.id.button5);
        Button btn6 = (Button) findViewById(R.id.button6);
        Button btn7 = (Button) findViewById(R.id.button7);
        Button btn8 = (Button) findViewById(R.id.button8);
        Button btn9 = (Button) findViewById(R.id.button9);

        if(btn1!=null && btn2!=null && btn3!=null && btn4!=null && btn5!=null && btn6!=null && btn7!=null && btn8!=null && btn9!=null) {
            btn1.setEnabled(true);
            btn2.setEnabled(true);
            btn3.setEnabled(true);
            btn4.setEnabled(true);
            btn5.setEnabled(true);
            btn6.setEnabled(true);
            btn7.setEnabled(true);
            btn8.setEnabled(true);
            btn9.setEnabled(true);
        }
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

    //DIALOG AL INTENTAR SALIR DE LA APP
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(R.drawable.logo).setTitle(getString(R.string.salir)).setMessage(getString(R.string.salirM)).setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}