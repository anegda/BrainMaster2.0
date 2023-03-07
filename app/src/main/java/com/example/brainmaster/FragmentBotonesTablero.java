package com.example.brainmaster;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FragmentBotonesTablero extends Fragment {

    public interface  listenerDelFragment{

    }
    private listenerDelFragment elListener;
    static ArrayList<Integer> resultado;

    public FragmentBotonesTablero() {
        // Required empty public constructor
    }

    public static FragmentBotonesTablero newInstance() {
        FragmentBotonesTablero fragment = new FragmentBotonesTablero();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_botones_tablero, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View.OnClickListener clicker = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) getView().findViewById(view.getId());
                int num = Integer.parseInt((String) btn.getText());
                resultado.add(num);
                Log.d("DAS",Integer.toString(num));
            }
        };

        view.findViewById(R.id.button1).setOnClickListener(clicker);
        view.findViewById(R.id.button2).setOnClickListener(clicker);
        view.findViewById(R.id.button3).setOnClickListener(clicker);
        view.findViewById(R.id.button4).setOnClickListener(clicker);
        view.findViewById(R.id.button5).setOnClickListener(clicker);
        view.findViewById(R.id.button6).setOnClickListener(clicker);
        view.findViewById(R.id.button7).setOnClickListener(clicker);
        view.findViewById(R.id.button8).setOnClickListener(clicker);
        view.findViewById(R.id.button9).setOnClickListener(clicker);
    }

}