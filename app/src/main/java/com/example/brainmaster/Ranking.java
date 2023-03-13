package com.example.brainmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Locale;


public class Ranking extends AppCompatActivity {
    ArrayList<String> arraydedatos = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        miBD GestorBD = new miBD(this, "BrainMaster", null, 1);
        SQLiteDatabase bd = GestorBD.getWritableDatabase();
        String[] campos = new String[] {"usuario","puntos"};
        Cursor c2 = bd.query("Partidas",campos,null,null, null,null,"puntos DESC");
        while (c2.moveToNext()){
            String usuario = c2.getString(0);
            int puntos = c2.getInt(1);
            String info = usuario + ": " + Integer.toString(puntos);
            arraydedatos.add(info);
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arraydedatos);
        ListView lalista = findViewById(R.id.listaRanking);
        lalista.setAdapter(adapter);

    }

    //PARA QUE NO HAYA PROBLEMAS AL ACTUALIZAR EL RANKING
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Menu.class));
        finish();
    }

    //MANTENER EL IDIOMA
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