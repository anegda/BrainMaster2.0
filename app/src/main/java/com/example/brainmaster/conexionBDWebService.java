package com.example.brainmaster;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class conexionBDWebService extends Worker {
    public conexionBDWebService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data datos = this.getInputData();
        int opcion = datos.getInt("funcion",0);
        boolean resultado = true;
        Data outputData = null;

        //DEPENDIENDO LA OPCIÓN HACEMOS UNA U OTRA
        switch (opcion){
            case 1:
                resultado = insertarUsuarios();
            case 2:
                resultado = selectNombreUsuario();
                Log.d("DAS", "RESULTADO: "+String.valueOf(resultado));
                outputData = new Data.Builder().putBoolean("existe", resultado).build();
        }
        if(resultado){
            return Result.success(outputData);
        }
        else{
            return Result.failure();
        }

    }

    public boolean insertarUsuarios(){
        //INTRODUCIMOS EL USUARIO A LA BD REMOTA
        Log.d("DAS", "INSERTANDO USUARIO");
        String direccion2 = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agarcia794/WEB/insertarUsuarios.php";
        HttpURLConnection urlConnection2 = null;
        Data datos = this.getInputData();
        String nombre = datos.getString("nombre");
        String apellidos = datos.getString("apellidos");
        String usuario = datos.getString("usuario");
        String password = datos.getString("password");
        String email = datos.getString("email");
        String fechaNac = datos.getString("fechaNac");
        String temp = datos.getString("img");
        try {
            String parametros2 = "?nombre="+nombre+"&apellidos="+apellidos+"&usuario="+usuario+"&password="+password+"&email="+email+"&fechaNac="+fechaNac+"&img="+temp;
            URL destino2 = new URL(direccion2+parametros2);
            urlConnection2 = (HttpURLConnection) destino2.openConnection();

            int statusCode = urlConnection2.getResponseCode();
            if(statusCode==200 || statusCode==500){
                //ABRIMOS EL MENU
                return true;
            }else{
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean selectNombreUsuario(){
        Log.d("DAS", "ENTRA EN SELECT");
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agarcia794/WEB/selectNombreUsuario.php";
        HttpURLConnection urlConnection = null;
        Data datos = this.getInputData();
        Log.d("DAS", String.valueOf(datos));
        String usuario = datos.getString("usuario");
        try {
            String parametros = "?usuario="+usuario;
            Log.d("DAS", direccion+parametros);
            URL destino = new URL(direccion+parametros);
            urlConnection = (HttpURLConnection) destino.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if(statusCode==200 || statusCode==500){
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(inputStream, "UTF-8"));
                String line, result="";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                inputStream.close();
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(result);
                String nom = (String) json.get("nombre");
                if (nom!=null){
                    return true;
                }
                return false;
            }else{
                return false;
            }

        } catch (ParseException | IOException e) {
            Log.d("DAS","ERROR");
            throw new RuntimeException(e);
        }
    }
}
