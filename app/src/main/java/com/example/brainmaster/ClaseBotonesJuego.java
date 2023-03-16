package com.example.brainmaster;

import java.util.ArrayList;

public class ClaseBotonesJuego {
    /**
     * Clase java que incluye toda la información referente a la partida actual.
     */
    private ArrayList<Integer> secuencia;   //GUARDAMOS LA SECUENCIA DE BOTONES
    private int puntuacion; //GUARDAMOS LA PUNTUACIÓN ACTUAL
    private int ronda; //GUARDAMOS LA RONDA EN LA QUE ESTAMOS (NECESARIO PARA AL CAMBIAR AL HORIZONTAL)

    //CONSTRUCTORA DEL JUEGO
    public ClaseBotonesJuego(){
        this.secuencia = new ArrayList<Integer>();
        this.puntuacion = 0;
        this.ronda=0;
    }

    //OBTENEMOS LA SECUENCIA PARA ILUMINAR LOS BOTONES CORRESPONDIENTES
    //DEVUELVE NÚMEROS DEL 1-9 QUE HACEN REFERENCIA A CADA BOTÓN
    public ArrayList<Integer> getSecuencia(){
        //SI LA PUNTUACIÓN NO COINCIDE CON LA RONDA IMPLICA QUE HEMOS CAMBIADO LA ORIENTACIÓN DEL MOVIL Y TENEMOS QUE REPETIR LA SECUENCIA
        if(puntuacion==ronda) {
            int numero = (int) (Math.random() * 9 + 1);
            secuencia.add(numero);
            this.ronda++;
        }
        return secuencia;
    }

    //COMPARA LA SECUENCIA REAL CON LA PROPUESTA POR EL USUARIO
    public boolean comparar(ArrayList<Integer> resultado){
        if(secuencia.equals(resultado)){
            puntuacion = puntuacion+1;
            return true;
        }
        else{
            return false;
        }
    }

    //DEVUELVE LA PUNTUACIÓN ACTUAL DE LA PARTIDA
    public int getPuntos(){
        return this.puntuacion;
    }
}
