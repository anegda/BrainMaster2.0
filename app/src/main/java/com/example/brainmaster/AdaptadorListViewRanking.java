package com.example.brainmaster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Base64;

public class AdaptadorListViewRanking extends BaseAdapter {
    /**
     * Basado en el código de Egela: Tema 04 - Listas
     **/
    private Context contexto;
    private LayoutInflater inflater;
    private String[] datos;
    private String[] puntos;
    private String[] imagenes;

    //CREAMOS LA CONSTRUCTURA CON TODA LA INFORMACIÓN NECESARIA
    public AdaptadorListViewRanking(Context pcontext, String[] pdatos, String[] ppuntos,String[] pimagenes){
        contexto = pcontext;
        datos = pdatos;
        puntos = ppuntos;
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
        nombre.setText(datos[i]+": "+puntos[i]);

        /**
         * Basado en el código extraído de Stack Overflow
         * Pregunta: https://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa
         * Autor: https://stackoverflow.com/users/1191766/sachin10
         * Modificado por Ane García para traducir varios términos y adaptarlo a la aplicación
         */
        if (imagenes[i]==null){
            ImageView img=(ImageView) view.findViewById(R.id.imagenRanking);
            img.setBackgroundResource(R.drawable.ranking);
        }else {
            byte[] encodeByte = Base64.getDecoder().decode(imagenes[i]);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            ImageView img = (ImageView) view.findViewById(R.id.imagenRanking);
            img.setImageBitmap(bitmap);
        }
        return view;
    }
}
