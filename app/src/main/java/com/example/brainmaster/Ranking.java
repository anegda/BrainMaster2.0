package com.example.brainmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Ranking extends AppCompatActivity {
    static Map<String, String> diccUsuarioPerfil = new HashMap<String, String>();
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
        //SELECT PARTIDAS PALABRAS EN BD REMOTA
        ArrayList<String> usuariosPalabras = new ArrayList<String>();
        ArrayList<String> puntosPalabras = new ArrayList<String>();
        ArrayList<String> perfilPalabras = new ArrayList<String>();

        Data datos0 = new Data.Builder()
                .putInt("funcion",6)
                .putString("tipo", "palabras").build();
        OneTimeWorkRequest otwr0 = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos0).build();
        WorkManager.getInstance(Ranking.this).getWorkInfoByIdLiveData(otwr0.getId()).observe(Ranking.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if(workInfo!=null && workInfo.getState().isFinished()){
                    Data outputData = workInfo.getOutputData();
                    if(outputData!=null){
                        String result = outputData.getString("result");
                        JSONParser parser = new JSONParser();
                        try {
                            JSONArray jsonArray = (JSONArray) parser.parse(result);


                            for(int i=0; i < jsonArray.size() && i < 5; i++){
                                JSONObject obj = (JSONObject) jsonArray.get(i);
                                String usuario = (String) obj.get("usuario");
                                String puntos = (String) obj.get("puntos");
                                usuariosPalabras.add(usuario);
                                puntosPalabras.add(puntos);

                                //CONSEGUIMOS LAS FOTOS DE PERFIL HACIENDO OTRA CONSULTA A LA BD REMOTA
                                Data datos0 = new Data.Builder()
                                        .putInt("funcion",2)
                                        .putString("usuario", usuario).build();
                                OneTimeWorkRequest otwr0 = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos0).build();
                                WorkManager.getInstance(Ranking.this).getWorkInfoByIdLiveData(otwr0.getId()).observe(Ranking.this, new Observer<WorkInfo>() {
                                    @Override
                                    public void onChanged(WorkInfo workInfo) {
                                        if(workInfo!=null && workInfo.getState().isFinished()){
                                            for (int i = 0; i < usuariosPalabras.size(); i++){
                                                perfilPalabras.add(diccUsuarioPerfil.get(usuariosPalabras.get(i)));
                                            }
                                            ListView ranking = (ListView) findViewById(R.id.listaRankingPalabras);
                                            AdaptadorListViewRanking eladap = new AdaptadorListViewRanking(getApplicationContext(), usuariosPalabras.toArray(new String[0]), puntosPalabras.toArray(new String[0]), perfilPalabras.toArray(new String[0]));
                                            ranking.setAdapter(eladap);
                                        }
                                    }
                                });
                                WorkManager.getInstance(Ranking.this).enqueue(otwr0);
                            }
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        WorkManager.getInstance(Ranking.this).enqueue(otwr0);

        //SELECT PARTIDAS BOTONES
        ArrayList<String> usuariosBotones = new ArrayList<String>();
        ArrayList<String> puntosBotones = new ArrayList<String>();
        ArrayList<String> perfilBotones = new ArrayList<String>();
        Data datos = new Data.Builder()
                .putInt("funcion",6)
                .putString("tipo", "botones").build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos).build();
        WorkManager.getInstance(Ranking.this).getWorkInfoByIdLiveData(otwr.getId()).observe(Ranking.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if(workInfo!=null && workInfo.getState().isFinished()){
                    Data outputData = workInfo.getOutputData();
                    if(outputData!=null){
                        String result = outputData.getString("result");
                        JSONParser parser = new JSONParser();
                        try {
                            JSONArray jsonArray = (JSONArray) parser.parse(result);

                            for(int i=0; i < jsonArray.size() && i < 5; i++){
                                JSONObject obj = (JSONObject) jsonArray.get(i);
                                String usuario = (String) obj.get("usuario");
                                String puntos = (String) obj.get("puntos");
                                usuariosBotones.add(usuario);
                                puntosBotones.add(puntos);

                                //CONSEGUIMOS LAS FOTOS DE PERFIL HACIENDO OTRA CONSULTA A LA BD REMOTA
                                Data datos0 = new Data.Builder()
                                        .putInt("funcion",2)
                                        .putString("usuario", usuario).build();
                                OneTimeWorkRequest otwr0 = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos0).build();
                                WorkManager.getInstance(Ranking.this).getWorkInfoByIdLiveData(otwr0.getId()).observe(Ranking.this, new Observer<WorkInfo>() {
                                    @Override
                                    public void onChanged(WorkInfo workInfo) {
                                        if(workInfo!=null && workInfo.getState().isFinished()){
                                            for (int i = 0; i < usuariosBotones.size(); i++){
                                                perfilBotones.add(diccUsuarioPerfil.get(usuariosBotones.get(i)));
                                            }
                                            ListView ranking2 = (ListView) findViewById(R.id.listaRankingBotones);
                                            AdaptadorListViewRanking eladap2 = new AdaptadorListViewRanking(getApplicationContext(), usuariosBotones.toArray(new String[0]), puntosBotones.toArray(new String[0]), perfilBotones.toArray(new String[0]));
                                            ranking2.setAdapter(eladap2);
                                        }
                                    }
                                });
                                WorkManager.getInstance(Ranking.this).enqueue(otwr0);
                            }
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        WorkManager.getInstance(Ranking.this).enqueue(otwr);
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