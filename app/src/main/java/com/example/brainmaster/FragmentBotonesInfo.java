package com.example.brainmaster;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentBotonesInfo extends Fragment {
    /**
     * La gran mayoría del código se genera solo.
     * Basado en el código de Egela: Tema 07 - Fragments
     */
    public FragmentBotonesInfo() {
        // Required empty public constructor
    }

    public static FragmentBotonesInfo newInstance() {
        FragmentBotonesInfo fragment = new FragmentBotonesInfo();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_botones_info, container, false);
    }

    //SE ENCARGA DE AÑADIR LA PUNTUACIÓN ACTUAL AL TEXTVIEW AL GENERAR EL FRAGMENT
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView puntosText = (TextView) view.findViewById(R.id.puntuaciónText);
        int puntuacion = getArguments().getInt("puntuacion");
        puntosText.setText(getString(R.string.puntuacion) +" "+Integer.toString(puntuacion));
    }

    //SE ENCARGA DE ACTUALIZAR LA PUNTUACIÓN ACTUAL
    public void actualizarPuntuacion(int puntos){
        TextView puntosText = (TextView) getView().findViewById(R.id.puntuaciónText);
        puntosText.setText(getString(R.string.puntuacion) +" "+Integer.toString(puntos));
    }
}