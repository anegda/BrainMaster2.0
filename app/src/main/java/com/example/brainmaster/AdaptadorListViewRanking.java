package com.example.brainmaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdaptadorListViewRanking extends BaseAdapter {
    /**
     * Basado en el código de Egela: Tema 04 - Listas
     **/
    private Context contexto;
    private LayoutInflater inflater;
    private String[] datos;
    private Integer[] imagenes;

    //CREAMOS LA CONSTRUCTURA CON TODA LA INFORMACIÓN NECESARIA
    public AdaptadorListViewRanking(Context pcontext, String[] pdatos, Integer[] pimagenes){
        contexto = pcontext;
        datos = pdatos;
        imagenes = pimagenes;
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
        view = inflater.inflate(R.layout.fila_ranking, null);
        //ESTABLECEMOS EL NOMBRE Y LA IMAGEN
        TextView nombre= (TextView) view.findViewById(R.id.etiquetaRanking);
        ImageView img=(ImageView) view.findViewById(R.id.imagenRanking);
        nombre.setText(datos[i]);
        img.setImageResource(imagenes[i]);
        return view;
    }
}
