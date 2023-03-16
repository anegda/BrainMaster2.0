package com.example.brainmaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class AdaptadorListView extends BaseAdapter {
    /**
     * Basado en el código de Egela: Tema 04 - Listas
    **/
    private Context contexto;
    private LayoutInflater inflater;
    private String[] datos;
    private int[] imagenes;
    private double[] dificultad;

    //CREAMOS LA CONSTRUCTURA CON TODA LA INFORMACIÓN NECESARIA
    public AdaptadorListView(Context pcontext, String[] pdatos, int[] pimagenes, double[] pdificultad){
        contexto = pcontext;
        datos = pdatos;
        imagenes = pimagenes;
        dificultad = pdificultad;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //SOBREESCRIBIMOS LOS MÉTODOS
    @Override
    public int getCount() {
        return datos.length;
    }

    @Override
    public Object getItem(int i) {
        return datos[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //CREAMOS LA VISTA UTILIZANDO EL LAYOUT fila.xml
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.fila, null);

        //ESTABLECEMOS LA IMAGEN, EL NOMBRE Y LA RATINGBAR
        TextView nombre= (TextView) view.findViewById(R.id.etiqueta);
        ImageView img=(ImageView) view.findViewById(R.id.imagen);
        RatingBar barra= (RatingBar) view.findViewById(R.id.barra);
        nombre.setText(datos[i]);
        img.setImageResource(imagenes[i]);
        barra.setRating((float)dificultad[i]);
        return view;
    }
}
