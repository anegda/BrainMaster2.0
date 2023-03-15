package com.example.brainmaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdaptadorListViewRanking extends BaseAdapter {
    private Context contexto;
    private LayoutInflater inflater;
    private String[] datos;
    private Integer[] imagenes;

    public AdaptadorListViewRanking(Context pcontext, String[] pdatos, Integer[] pimagenes){
        contexto = pcontext;
        datos = pdatos;
        imagenes = pimagenes;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.fila_ranking, null);

        TextView nombre= (TextView) view.findViewById(R.id.etiquetaRanking);
        ImageView img=(ImageView) view.findViewById(R.id.imagenRanking);
        nombre.setText(datos[i]);
        img.setImageResource(imagenes[i]);
        return view;
    }
}
