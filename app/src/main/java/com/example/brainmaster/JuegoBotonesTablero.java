package com.example.brainmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class JuegoBotonesTablero extends AppCompatActivity implements FragmentBotonesTablero.listenerDelFragment{
    static boolean espera;
    static ArrayList<Integer> solucion;
    static ClaseBotonesJuego juego;
    Runnable ronda = new Runnable() {
        @Override
        public void run() {
            ArrayList<Integer> ronda = juego.getSecuencia();
            long step = 2000;
            long numRonda = 0;
            for (Integer i : ronda) {
                Handler handler = new Handler();
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
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_botones_tablero);

        juego = new ClaseBotonesJuego();
        solucion = new ArrayList<Integer>();

        Handler handler = new Handler();
        handler.postDelayed(ronda,1000);

        Button btn_enter = findViewById(R.id.btn_enter);
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(juego.comparar(solucion)){
                    solucion = new ArrayList<Integer>();
                    Handler handler = new Handler();
                    handler.postDelayed(ronda,3000);
                }
                else{
                    miBD GestorBD = new miBD(JuegoBotonesTablero.this, "BrainMaster", null, 1);
                    SQLiteDatabase bd = GestorBD.getWritableDatabase();
                    String nombre = "anegda";
                    int puntos =juego.getPuntos();
                    bd.execSQL("INSERT INTO Partidas ('usuario', 'puntos') VALUES ('" + nombre + "'," + puntos + ")");
                    finish();
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
}