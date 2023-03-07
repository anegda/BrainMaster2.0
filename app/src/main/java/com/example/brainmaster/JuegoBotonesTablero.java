package com.example.brainmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class JuegoBotonesTablero extends AppCompatActivity{
    static ArrayList<Integer> solucion;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ClaseBotonesJuego juego = new ClaseBotonesJuego();
            boolean finalizar = false;
            while(!finalizar){
                ArrayList<Integer> ronda = juego.getSecuencia();
                for(Integer i:ronda){
                    int btn_id = getResources().getIdentifier("button"+Integer.toString(i),"id",getPackageName());
                    Button btn_act = (Button) findViewById(btn_id);

                    btn_act.setBackgroundColor(Color.RED);
                    //ESPERAMOS UN SEGUNDO ANTES DE APAGARLO
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            int btn_id = getResources().getIdentifier("button"+Integer.toString(i),"id",getPackageName());
                            Button btn_act = (Button) findViewById(btn_id);
                            btn_act.setBackgroundColor(getColor(R.color.purple_500));
                        }
                    }, 1000);
                }
                finalizar=true;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_botones_tablero);

        Handler handler = new Handler();
        handler.postDelayed(runnable,4000);
    }

}