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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView puntosText = (TextView) view.findViewById(R.id.puntuaciónText);
        puntosText.setText(getString(R.string.puntuacion) +" 0");
    }

    public void actualizarPuntuacion(int puntos){
        TextView puntosText = (TextView) getView().findViewById(R.id.puntuaciónText);
        puntosText.setText(getString(R.string.puntuacion) +" "+Integer.toString(puntos));
    }
}