package com.example.brainmaster;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class ClasePalabrasJuego {
    /**
     * Clase java que incluye toda la información referente a la partida actual del juego de palabras.
     */
    private ArrayList<String> aparecidas; //PALABRAS QUE YA HAN APARECIDO
    private ArrayList<String> posibles; //PALABRAS QUE PUEDEN APARECER
    private ArrayList<String> todas; //TODAS LAS PALABRAS DE LOS FICHEROS
    private int puntuacion; //PUNTUACIÓN DE LA PARTIDA ACTUAL

    public ClasePalabrasJuego(Context context, String idioma){
        this.aparecidas = new ArrayList<String>();
        this.puntuacion = 0;
        this.todas = new ArrayList<String>();

        InputStream fich = context.getResources().openRawResource(R.raw.palabras);
        //LEO EL FICHERO INCLUIDO EN LA APLICACIÓN CON LAS PALABRAS
        if(idioma.equals("es")) {
            fich = context.getResources().openRawResource(R.raw.palabras);
        } else if (idioma.equals("en")) {
            fich = context.getResources().openRawResource(R.raw.words);
        } else if (idioma.equals("eu")) {
            fich = context.getResources().openRawResource(R.raw.hitzak);
        }

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

        //AL PRINCIPIO DE LA PARTIDA EL NÚMERO DE PALABRAS QUE PUEDEN SALIR ES 40 Y POSTERIORMENTE VA AUMENTANDO
        this.posibles = new ArrayList<>(this.todas.subList(0,39));
    }

    public String getPalabra(){
        //ELEGIR PALABRA ALEATORIA DE LA LISTA DE POSIBLES
        Collections.shuffle(this.posibles);
        String palabra = this.posibles.get(0);
        return palabra;
    }

    //COMPROBAMOS LA RESPUESTA DEL USUARIO
    public boolean comprobarRespuesta(boolean respuesta, String palabra){
        //SI ESTÁ EN APARECIDAS Y HEMOS CLICKADO VISTO => true
        if(this.aparecidas.contains(palabra) && respuesta){
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
        //SI NO ESTÁ EN APARECIDAS Y HEMOS CLICKADO NUEVO => true
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

    //OBTENEMOS LA PUNTUACIÓN DEL USUARIO
    public int getPuntos(){
        return this.puntuacion;
    }
}
