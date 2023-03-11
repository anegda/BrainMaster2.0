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
            juego = new ClasePalabrasJuego(this, pIdioma);
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
                    //INTRODUCIMOS LA PUNTUACIÓN EN LA BD
                    miBD GestorBD = new miBD(JuegoPalabrasTablero.this, "BrainMaster", null, 1);
                    SQLiteDatabase bd = GestorBD.getWritableDatabase();
                    String nombre = "anegda";
                    int puntos =juego.getPuntos();
                    bd.execSQL("INSERT INTO Partidas ('usuario', 'puntos','tipo') VALUES ('" + nombre + "'," + puntos + ",'palabras')");

                    //DIÁLOGO DICIENDO QUE HAS PERDIDO
                    new AlertDialog.Builder(getApplicationContext()).setIcon(R.drawable.logo).setTitle(getString(R.string.perder)).setMessage(getString(R.string.puntuacion)+" "+Integer.toString(puntos)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(JuegoPalabrasTablero.this, Menu.class));
                            finish();
                        }
                    }).show();
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
                    //INTRODUCIMOS LA PUNTUACIÓN EN LA BD
                    miBD GestorBD = new miBD(JuegoPalabrasTablero.this, "BrainMaster", null, 1);
                    SQLiteDatabase bd = GestorBD.getWritableDatabase();
                    String nombre = "anegda";
                    int puntos =juego.getPuntos();
                    bd.execSQL("INSERT INTO Partidas ('usuario', 'puntos','tipo') VALUES ('" + nombre + "'," + puntos + ",'palabras')");

                    //DIÁLOGO DICIENDO QUE HAS PERDIDO
                    new AlertDialog.Builder(getApplicationContext()).setIcon(R.drawable.logo).setTitle(getString(R.string.perder)).setMessage(getString(R.string.puntuacion)+" "+Integer.toString(puntos)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(JuegoPalabrasTablero.this, Menu.class));
                            finish();
                        }
                    }).show();
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

    //DIALOG AL INTENTAR SALIR DE LA APP
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(R.drawable.logo).setTitle(getString(R.string.salir)).setMessage(getString(R.string.salirM)).setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}