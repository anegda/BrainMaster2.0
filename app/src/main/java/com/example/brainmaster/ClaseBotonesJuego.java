package com.example.brainmaster;

import java.util.ArrayList;

public class ClaseBotonesJuego {
    private ArrayList<Integer> secuencia;
    private long puntuacion;

    public ClaseBotonesJuego(){
        this.secuencia = new ArrayList<Integer>();
        this.puntuacion = 0;
    }

    public ArrayList<Integer> getSecuencia(){
        int numero = (int)(Math.random()*9+1);
        secuencia.add(numero);
        return secuencia;
    }

    public boolean comparar(ArrayList<Integer> resultado){
        if(secuencia.equals(resultado)){
            puntuacion = puntuacion+100;
            return true;
        }
        else{
            return false;
        }
    }
}