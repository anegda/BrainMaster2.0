package com.example.brainmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Locale;


public class Ranking extends AppCompatActivity {

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

        //QUITAMOS LA ACTION BAR
        getSupportActionBar().hide();

        //OBTENEMOS LAS 5 MEJORES PARTIDAS DE CADA TIPO DE JUEGO Y CREAMOS LOS LISTVIEWS PERSONALIZADOS
        //SELECT PARTIDAS PALABRAS
        miBD GestorBD = new miBD(this, "BrainMaster", null, 1);
        SQLiteDatabase bd = GestorBD.getWritableDatabase();
        String[] campos = new String[] {"usuario","puntos"};
        String [] argumentos = new String[] {"palabras"};
        Cursor c = bd.query("Partidas",campos,"tipo=?",argumentos, null,null,"puntos DESC");

        ArrayList<String> usuarios_puntos = new ArrayList<String>();
        ArrayList<String> perfil = new ArrayList<String>();
        int i = 0;
        while (c.moveToNext() && i<5){
            i++;
            String usuario = c.getString(0);
            int puntos = c.getInt(1);
            String info = usuario + ": " + Integer.toString(puntos);
            usuarios_puntos.add(info);

            //LLAMAMOS A LA BASE DE DATOS Y OBTENEMOS LAS FOTOS DE PERFIL DE LOS USUARIOS CON LAS 10 MEJORES PARTIDAS
            String[] campos2 = new String[] {"img"};
            String [] argumentos2 = new String[] {usuario};
            Cursor c2 = bd.query("Usuarios",campos2,"usuario=?",argumentos2, null,null,null);
            c2.moveToFirst();
            perfil.add(c2.getString(0));
            c2.close();
        }
        c.close();

        ListView ranking = (ListView) findViewById(R.id.listaRankingPalabras);
        AdaptadorListViewRanking eladap = new AdaptadorListViewRanking(getApplicationContext(), usuarios_puntos.toArray(new String[0]), perfil.toArray(new String[0]));
        ranking.setAdapter(eladap);

        //SELECT PARTIDAS BOTONES
        String[] campos3 = new String[] {"usuario","puntos"};
        String [] argumentos3 = new String[] {"botones"};
        Cursor c3 = bd.query("Partidas",campos3,"tipo=?",argumentos3, null,null,"puntos DESC");

        ArrayList<String> usuarios_puntos2 = new ArrayList<String>();
        ArrayList<String> perfil2 = new ArrayList<String>();
        int i2 = 0;
        while (c3.moveToNext() && i2<5){
            i2++;
            String usuario2 = c3.getString(0);
            int puntos2 = c3.getInt(1);
            String info = usuario2 + ": " + Integer.toString(puntos2);
            usuarios_puntos2.add(info);

            //LLAMAMOS A LA BASE DE DATOS Y OBTENEMOS LAS FOTOS DE PERFIL DE LOS USUARIOS CON LAS 10 MEJORES PARTIDAS
            String[] campos4 = new String[] {"img"};
            String [] argumentos4 = new String[] {usuario2};
            Cursor c4 = bd.query("Usuarios",campos4,"usuario=?",argumentos4, null,null,null);
            c4.moveToFirst();
            perfil2.add(c4.getString(0));
            c4.close();
        }
        c3.close();
        bd.close();

        ListView ranking2 = (ListView) findViewById(R.id.listaRankingBotones);
        AdaptadorListViewRanking eladap2 = new AdaptadorListViewRanking(getApplicationContext(), usuarios_puntos2.toArray(new String[0]), perfil2.toArray(new String[0]));
        ranking2.setAdapter(eladap2);
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