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
import android.util.Log;
import android.widget.ListView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
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
        //SELECT PARTIDAS PALABRAS EN BD REMOTA
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
                            ArrayList<String> usuarios_puntos = new ArrayList<String>();
                            ArrayList<String> perfil = new ArrayList<String>();

                            for(int i=0; i < jsonArray.size() && i < 5; i++){
                                JSONObject obj = (JSONObject) jsonArray.get(i);
                                String usuario = (String) obj.get("usuario");
                                String puntos = (String) obj.get("puntos");
                                usuarios_puntos.add(usuario+": "+puntos);

                                Bitmap img =  BitmapFactory.decodeResource(getResources(), R.drawable.ranking);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                img.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                byte[] b = baos.toByteArray();
                                //PARA QUE NO EXISTAN PROBLEMAS CON EL TAMAÑO DE LA IMAGEN
                                b = tratarImagen(b);
                                String temp = Base64.getEncoder().encodeToString(b);

                                perfil.add(temp);
                            }

                            ListView ranking = (ListView) findViewById(R.id.listaRankingPalabras);
                            AdaptadorListViewRanking eladap = new AdaptadorListViewRanking(getApplicationContext(), usuarios_puntos.toArray(new String[0]), perfil.toArray(new String[0]));
                            ranking.setAdapter(eladap);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        WorkManager.getInstance(Ranking.this).enqueue(otwr0);

        //SELECT PARTIDAS BOTONES
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
                            ArrayList<String> usuarios_puntos = new ArrayList<String>();
                            ArrayList<String> perfil = new ArrayList<String>();

                            for(int i=0; i < jsonArray.size() && i < 5; i++){
                                JSONObject obj = (JSONObject) jsonArray.get(i);
                                String usuario = (String) obj.get("usuario");
                                String puntos = (String) obj.get("puntos");
                                usuarios_puntos.add(usuario+": "+puntos);

                                Bitmap img =  BitmapFactory.decodeResource(getResources(), R.drawable.ranking);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                img.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                byte[] b = baos.toByteArray();
                                //PARA QUE NO EXISTAN PROBLEMAS CON EL TAMAÑO DE LA IMAGEN
                                b = tratarImagen(b);
                                String temp = Base64.getEncoder().encodeToString(b);

                                perfil.add(temp);
                            }

                            ListView ranking = (ListView) findViewById(R.id.listaRankingBotones);
                            AdaptadorListViewRanking eladap = new AdaptadorListViewRanking(getApplicationContext(), usuarios_puntos.toArray(new String[0]), perfil.toArray(new String[0]));
                            ranking.setAdapter(eladap);
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

    //HASTA SOLUCIONAR LO DE LAS FOTOS
    protected byte[] tratarImagen(byte[] img){
        /**
         * Basado en el código extraído de Stack Overflow
         * Pregunta: https://stackoverflow.com/questions/57107489/sqliteblobtoobigexception-row-too-big-to-fit-into-cursorwindow-while-writing-to
         * Autor: https://stackoverflow.com/users/3694451/leo-vitor
         * Modificado por Ane García para traducir varios términos y adaptarlo a la aplicación
         */
        while(img.length > 50000){
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            Bitmap compacto = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.8), (int)(bitmap.getHeight()*0.8), true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            compacto.compress(Bitmap.CompressFormat.PNG, 100, stream);
            img = stream.toByteArray();
        }
        return img;
    }
}