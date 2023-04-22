package com.example.brainmaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
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

        //COMPROBAMOS SI EXISTE CONEXIÓN A INTERNET
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||  connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        if(connected) {
            Data datos0 = new Data.Builder()
                    .putInt("funcion", 6)
                    .putString("tipo", "palabras").build();
            OneTimeWorkRequest otwr0 = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos0).build();
            WorkManager.getInstance(Ranking.this).getWorkInfoByIdLiveData(otwr0.getId()).observe(Ranking.this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(WorkInfo workInfo) {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        Data outputData = workInfo.getOutputData();
                        if (outputData != null) {
                            String result = outputData.getString("result");
                            if(result==null){
                                //EN CASO DE QUE LA CONEXIÓN SEA LENTA O MALA
                                Toast.makeText(getApplicationContext(), getString(R.string.errorConexion), Toast.LENGTH_LONG).show();

                                //LISTA PALABRAS
                                ArrayList<String> usuariosPalabras1 = new ArrayList<String>();
                                ArrayList<String> puntosPalabras1 = new ArrayList<String>();
                                ArrayList<String> perfilPalabras1 = new ArrayList<String>();

                                ListView ranking = (ListView) findViewById(R.id.listaRankingPalabras);
                                AdaptadorListViewRanking eladap = new AdaptadorListViewRanking(getApplicationContext(), usuariosPalabras1.toArray(new String[0]), puntosPalabras1.toArray(new String[0]), perfilPalabras1.toArray(new String[0]));
                                ranking.setAdapter(eladap);
                            }else {
                                JSONParser parser = new JSONParser();
                                Log.d("DAS", result);
                                try {
                                    JSONArray jsonArray = (JSONArray) parser.parse(result);


                                    for (int i = 0; i < jsonArray.size() && i < 5; i++) {
                                        JSONObject obj = (JSONObject) jsonArray.get(i);
                                        String usuario = (String) obj.get("usuario");
                                        String puntos = (String) obj.get("puntos");
                                        usuariosPalabras.add(usuario);
                                        puntosPalabras.add(puntos);

                                        //CONSEGUIMOS LAS FOTOS DE PERFIL HACIENDO OTRA CONSULTA A LA BD REMOTA
                                        Data datos0 = new Data.Builder()
                                                .putInt("funcion", 2)
                                                .putString("usuario", usuario).build();
                                        OneTimeWorkRequest otwr0 = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos0).build();
                                        WorkManager.getInstance(Ranking.this).getWorkInfoByIdLiveData(otwr0.getId()).observe(Ranking.this, new Observer<WorkInfo>() {
                                            @Override
                                            public void onChanged(WorkInfo workInfo) {
                                                if (workInfo != null && workInfo.getState().isFinished()) {
                                                    for (int i = 0; i < usuariosPalabras.size(); i++) {
                                                        perfilPalabras.add(diccUsuarioPerfil.get(usuariosPalabras.get(i)));
                                                    }
                                                    if (perfilPalabras.size() != usuariosPalabras.size()) {
                                                        //EN CASO DE QUE LA CONEXIÓN SEA LENTA O MALA
                                                        Toast.makeText(getApplicationContext(), getString(R.string.errorConexion), Toast.LENGTH_LONG).show();

                                                        //LISTA PALABRAS VACÍA
                                                        ArrayList<String> usuariosPalabras1 = new ArrayList<String>();
                                                        ArrayList<String> puntosPalabras1 = new ArrayList<String>();
                                                        ArrayList<String> perfilPalabras1 = new ArrayList<String>();

                                                        ListView ranking = (ListView) findViewById(R.id.listaRankingPalabras);
                                                        AdaptadorListViewRanking eladap = new AdaptadorListViewRanking(getApplicationContext(), usuariosPalabras1.toArray(new String[0]), puntosPalabras1.toArray(new String[0]), perfilPalabras1.toArray(new String[0]));
                                                        ranking.setAdapter(eladap);
                                                    } else {
                                                        ListView ranking = (ListView) findViewById(R.id.listaRankingPalabras);
                                                        AdaptadorListViewRanking eladap = new AdaptadorListViewRanking(getApplicationContext(), usuariosPalabras.toArray(new String[0]), puntosPalabras.toArray(new String[0]), perfilPalabras.toArray(new String[0]));
                                                        ranking.setAdapter(eladap);
                                                    }
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
                }
            });
            WorkManager.getInstance(Ranking.this).enqueue(otwr0);

            //SELECT PARTIDAS BOTONES
            ArrayList<String> usuariosBotones = new ArrayList<String>();
            ArrayList<String> puntosBotones = new ArrayList<String>();
            ArrayList<String> perfilBotones = new ArrayList<String>();
            Data datos = new Data.Builder()
                    .putInt("funcion", 6)
                    .putString("tipo", "botones").build();
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos).build();
            WorkManager.getInstance(Ranking.this).getWorkInfoByIdLiveData(otwr.getId()).observe(Ranking.this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(WorkInfo workInfo) {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        Data outputData = workInfo.getOutputData();
                        if (outputData != null) {
                            String result = outputData.getString("result");
                            if (result==null){
                                //EN CASO DE QUE LA CONEXIÓN SEA LENTA O MALA
                                Toast.makeText(getApplicationContext(), getString(R.string.errorConexion), Toast.LENGTH_LONG).show();

                                //LISTA BOTONES VACÍA
                                ArrayList<String> usuariosBotones1 = new ArrayList<String>();
                                ArrayList<String> puntosBotones1 = new ArrayList<String>();
                                ArrayList<String> perfilBotones1 = new ArrayList<String>();

                                ListView ranking2 = (ListView) findViewById(R.id.listaRankingBotones);
                                AdaptadorListViewRanking eladap2 = new AdaptadorListViewRanking(getApplicationContext(), usuariosBotones1.toArray(new String[0]), puntosBotones1.toArray(new String[0]), perfilBotones1.toArray(new String[0]));
                                ranking2.setAdapter(eladap2);
                            }else{
                                JSONParser parser = new JSONParser();
                                try {
                                    JSONArray jsonArray = (JSONArray) parser.parse(result);

                                    for (int i = 0; i < jsonArray.size() && i < 5; i++) {
                                        JSONObject obj = (JSONObject) jsonArray.get(i);
                                        String usuario = (String) obj.get("usuario");
                                        String puntos = (String) obj.get("puntos");
                                        usuariosBotones.add(usuario);
                                        puntosBotones.add(puntos);

                                        //CONSEGUIMOS LAS FOTOS DE PERFIL HACIENDO OTRA CONSULTA A LA BD REMOTA
                                        Data datos0 = new Data.Builder()
                                                .putInt("funcion", 2)
                                                .putString("usuario", usuario).build();
                                        OneTimeWorkRequest otwr0 = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos0).build();
                                        WorkManager.getInstance(Ranking.this).getWorkInfoByIdLiveData(otwr0.getId()).observe(Ranking.this, new Observer<WorkInfo>() {
                                            @Override
                                            public void onChanged(WorkInfo workInfo) {
                                                if (workInfo != null && workInfo.getState().isFinished()) {
                                                    for (int i = 0; i < usuariosBotones.size(); i++) {
                                                        perfilBotones.add(diccUsuarioPerfil.get(usuariosBotones.get(i)));
                                                    }
                                                    if (perfilBotones.size()!=usuariosBotones.size()){
                                                        //EN CASO DE QUE LA CONEXIÓN SEA LENTA O MALA
                                                        Toast.makeText(getApplicationContext(), getString(R.string.errorConexion), Toast.LENGTH_LONG).show();

                                                        //LISTA BOTONES VACÍA
                                                        ArrayList<String> usuariosBotones1 = new ArrayList<String>();
                                                        ArrayList<String> puntosBotones1 = new ArrayList<String>();
                                                        ArrayList<String> perfilBotones1 = new ArrayList<String>();

                                                        ListView ranking2 = (ListView) findViewById(R.id.listaRankingBotones);
                                                        AdaptadorListViewRanking eladap2 = new AdaptadorListViewRanking(getApplicationContext(), usuariosBotones1.toArray(new String[0]), puntosBotones1.toArray(new String[0]), perfilBotones1.toArray(new String[0]));
                                                        ranking2.setAdapter(eladap2);
                                                    }else{
                                                        ListView ranking2 = (ListView) findViewById(R.id.listaRankingBotones);
                                                        AdaptadorListViewRanking eladap2 = new AdaptadorListViewRanking(getApplicationContext(), usuariosBotones.toArray(new String[0]), puntosBotones.toArray(new String[0]), perfilBotones.toArray(new String[0]));
                                                        ranking2.setAdapter(eladap2);
                                                    }
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
                }
            });
            WorkManager.getInstance(Ranking.this).enqueue(otwr);
        }else{
            //LO INDICAMOS MEDIANTE UN TOAST Y CREAMOS LAS LISTAS VACÍAS
            Toast.makeText(getApplicationContext(), getString(R.string.errorConexion), Toast.LENGTH_LONG).show();

            //LISTA PALABRAS
            ArrayList<String> usuariosPalabras1 = new ArrayList<String>();
            ArrayList<String> puntosPalabras1 = new ArrayList<String>();
            ArrayList<String> perfilPalabras1 = new ArrayList<String>();

            ListView ranking = (ListView) findViewById(R.id.listaRankingPalabras);
            AdaptadorListViewRanking eladap = new AdaptadorListViewRanking(getApplicationContext(), usuariosPalabras1.toArray(new String[0]), puntosPalabras1.toArray(new String[0]), perfilPalabras1.toArray(new String[0]));
            ranking.setAdapter(eladap);

            //LISTA BOTONES
            ArrayList<String> usuariosBotones1 = new ArrayList<String>();
            ArrayList<String> puntosBotones1 = new ArrayList<String>();
            ArrayList<String> perfilBotones1 = new ArrayList<String>();

            ListView ranking2 = (ListView) findViewById(R.id.listaRankingBotones);
            AdaptadorListViewRanking eladap2 = new AdaptadorListViewRanking(getApplicationContext(), usuariosBotones1.toArray(new String[0]), puntosBotones1.toArray(new String[0]), perfilBotones1.toArray(new String[0]));
            ranking2.setAdapter(eladap2);
        }
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