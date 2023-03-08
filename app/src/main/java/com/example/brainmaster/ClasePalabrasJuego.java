package com.example.brainmaster;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class ClasePalabrasJuego {
    private ArrayList<String> aparecidas;
    private ArrayList<String> posibles;
    private ArrayList<String> todas;
    private int puntuacion;

    public ClasePalabrasJuego(Context context){
        this.aparecidas = new ArrayList<String>();
        this.puntuacion = 0;
        this.todas = new ArrayList<String>();

        //LEO EL FICHERO INCLUIDO EN LA APLICACIÓN CON LAS PALABRAS
        InputStream fich = context.getResources().openRawResource(R.raw.palabras);
        BufferedReader buff = new BufferedReader(new InputStreamReader(fich));
        try {
            String linea = buff.readLine();
            while( linea != null) {
                this.todas.add(linea);
                linea = buff.readLine();
            }
            buff.close();
            fich.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //PARA DISTINTAS PALABRAS EN CADA PARTIDA
        Collections.shuffle(this.todas);
        this.posibles = new ArrayList<>(this.todas.subList(0,39));
    }

    public String getPalabra(){
        //ELEGIR PALABRA ALEATORIA DE LA LISTA DE POSIBLES
        Collections.shuffle(this.posibles);
        String palabra = this.posibles.get(0);
        return palabra;
    }

    public boolean comprobarRespuesta(boolean respuesta, String palabra){
        if(this.aparecidas.contains(palabra) && respuesta){
            Log.d("DAS",String.valueOf(this.aparecidas));
            this.puntuacion++;
            //AUMENTAMOS EL NÚMERO DE PALABRAS POSIBLES
            if(this.puntuacion>40){
                ArrayList<String> extra = new ArrayList<>(this.todas.subList(40,89));
                this.posibles.addAll(extra);
            }
            else if(this.puntuacion>80){
                this.posibles = (ArrayList<String>) this.todas.clone();
            }
            return true;
        }
        else if(!this.aparecidas.contains(palabra) && !respuesta){
            this.aparecidas.add(palabra);
            this.puntuacion++;
            //AUMENTAMOS EL NÚMERO DE PALABRAS POSIBLES
            if(this.puntuacion>40){
                ArrayList<String> extra = new ArrayList<>(this.todas.subList(40,89));
                this.posibles.addAll(extra);
            }
            else if(this.puntuacion>80){
                this.posibles = (ArrayList<String>) this.todas.clone();
            }
            return true;
        }
        else{
            return false;
        }
    }

    public int getPuntos(){
        return this.puntuacion;
    }
}
