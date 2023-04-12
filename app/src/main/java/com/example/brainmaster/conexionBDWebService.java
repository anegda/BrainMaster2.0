package com.example.brainmaster;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
                return Result.success();
            case 2:
                outputData = selectNombreUsuario();
                if(outputData==null) {
                    outputData = new Data.Builder().putBoolean("existe", false).build();
                }
                return Result.success(outputData);
            case 3:
                resultado = selectNombreUsuarioContraseña();
                outputData = new Data.Builder().putBoolean("correcto",resultado).build();
                return Result.success(outputData);
            case 4:
                actualizarUsuario();
                return Result.success();
            case 5:
                insertarPartida();
                return Result.success();
            case 6:
                outputData = selectPartidaTipo();
                if(outputData==null){
                    outputData = new Data.Builder().build();
                }
                return Result.success(outputData);
            case 7:
                outputData = selectPartidaUsuario();
                if(outputData==null){
                    outputData = new Data.Builder().build();
                }
                return Result.success(outputData);
            default:
                break;
        }
        return Result.failure();
    }

    public boolean insertarUsuarios(){
        //INTRODUCIMOS EL USUARIO A LA BD REMOTA
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
            Log.d("DAS","ERROR INSERT");
            throw new RuntimeException(e);
        }
    }

    public Data selectNombreUsuario(){
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agarcia794/WEB/selectNombreUsuario.php";
        HttpURLConnection urlConnection = null;
        Data datos = this.getInputData();
        String usuario = datos.getString("usuario");
        try {
            Data outputData = null;

            String parametros = "?usuario="+usuario;
            URL destino = new URL(direccion+parametros);
            urlConnection = (HttpURLConnection) destino.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if(statusCode==200){
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(inputStream, "UTF-8"));
                String line, result="";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                inputStream.close();
                if(!result.equals("No records matching your query were found.")) {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(result);
                    String nom = (String) json.get("nombre");
                    String ap = (String) json.get("apellidos");
                    String u = (String) json.get("usuario");
                    String p = (String) json.get("password");
                    String em = (String) json.get("email");
                    String fN = (String) json.get("fechaNac");
                    String i = (String) json.get("img");

                    if(nom!=null){
                        outputData = new Data.Builder()
                                .putBoolean("existe", true)
                                .putString("nombre",nom)
                                .putString("apellidos",ap)
                                .putString("usuario",u)
                                .putString("password",p)
                                .putString("email",em)
                                .putString("fechaNac",fN)
                                .putString("img",i)
                                .build();
                    }
                    return outputData;
                }
                return outputData;
            }else{
                return outputData;
            }

        } catch (IOException | ParseException e) {
            Log.d("DAS","ERROR SELECT");
            throw new RuntimeException(e);
        }
    }

    public boolean selectNombreUsuarioContraseña(){
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agarcia794/WEB/selectNombreUsuarioContraseña.php";
        HttpURLConnection urlConnection = null;
        Data datos = this.getInputData();
        String usuario = datos.getString("usuario");
        String password = datos.getString("password");
        try {
            String parametros = "?usuario="+usuario+"&password="+password;
            URL destino = new URL(direccion+parametros);
            urlConnection = (HttpURLConnection) destino.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if(statusCode==200){
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(inputStream, "UTF-8"));
                String line, result="";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                inputStream.close();
                if(!result.equals("No records matching your query were found.")) {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(result);
                    String nom = (String) json.get("nombre");
                    if (nom != null) {
                        return true;
                    }
                }
            }
            return false;
        } catch (ParseException | IOException e) {
            Log.d("DAS","ERROR SELECT*");
            throw new RuntimeException(e);
        }
    }

    public boolean actualizarUsuario(){
        //ACTUALIZAMOS EL USUARIO EN LA BD REMOTA
        String direccion2 = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agarcia794/WEB/actualizarUsuario.php";
        HttpURLConnection urlConnection2 = null;
        Data datos = this.getInputData();
        String usuario = datos.getString("usuario");
        String password = datos.getString("password");
        String temp = datos.getString("img");
        try {
            String parametros2 = "?usuario="+usuario+"&password="+password+"&img="+temp;
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
            Log.d("DAS","ERROR UPDATE");
            throw new RuntimeException(e);
        }
    }

    public boolean insertarPartida(){
        //INTRODUCIMOS LA PARTIDA EN LA BD REMOTA
        String direccion2 = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agarcia794/WEB/insertarPartidas.php";
        HttpURLConnection urlConnection2 = null;
        Data datos = this.getInputData();
        String usuario = datos.getString("usuario");
        int puntos = datos.getInt("puntos",0);
        String tipo = datos.getString("tipo");
        String latitud = datos.getString("latitud");
        String longitud = datos.getString("longitud");
        try {
            String parametros2 = "?usuario="+usuario+"&puntos="+puntos+"&tipo="+tipo+"&latitud="+latitud+"&longitud="+longitud;
            URL destino2 = new URL(direccion2+parametros2);
            urlConnection2 = (HttpURLConnection) destino2.openConnection();

            int statusCode = urlConnection2.getResponseCode();
            Log.d("DAS", String.valueOf(statusCode));
            if(statusCode==200 || statusCode==500){
                return true;
            }else{
                return false;
            }
        } catch (IOException e) {
            Log.d("DAS","ERROR INSERT PARTIDAS");
            throw new RuntimeException(e);
        }
    }

    public Data selectPartidaTipo(){
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agarcia794/WEB/selectPartidasTipo.php";
        HttpURLConnection urlConnection = null;
        Data datos = this.getInputData();
        String tipo = datos.getString("tipo");
        try {
            Data outputData = null;

            String parametros = "?tipo="+tipo;
            URL destino = new URL(direccion+parametros);
            urlConnection = (HttpURLConnection) destino.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if(statusCode==200){
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(inputStream, "UTF-8"));
                String line, result="";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                inputStream.close();
                if(!result.equals("No records matching your query were found.")) {
                    outputData = new Data.Builder().putString("result", result).build();
                }
            }
            return outputData;
        } catch (IOException e) {
            Log.d("DAS","ERROR SELECT PARTIDAS TIPO");
            throw new RuntimeException(e);
        }
    }

    public Data selectPartidaUsuario(){
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/agarcia794/WEB/selectPartidasUsuario.php";
        HttpURLConnection urlConnection = null;
        Data datos = this.getInputData();
        String usuario = datos.getString("usuario");
        try {
            Data outputData = null;

            String parametros = "?usuario="+usuario;
            URL destino = new URL(direccion+parametros);
            urlConnection = (HttpURLConnection) destino.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if(statusCode==200){
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(inputStream, "UTF-8"));
                String line, result="";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                inputStream.close();
                if(!result.equals("No records matching your query were found.")) {
                    outputData = new Data.Builder().putString("result", result).build();
                }
            }
            return outputData;
        } catch (IOException e) {
            Log.d("DAS","ERROR SELECT PARTIDAS USUARIO");
            throw new RuntimeException(e);
        }
    }
}
