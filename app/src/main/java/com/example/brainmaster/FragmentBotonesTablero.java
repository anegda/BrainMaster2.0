package com.example.brainmaster;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentBotonesTablero#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentBotonesTablero extends Fragment {

    public interface  listenerDelFragment{

    }
    private listenerDelFragment elListener;

    public FragmentBotonesTablero() {
        // Required empty public constructor
    }

    public static FragmentBotonesTablero newInstance(String param1, String param2) {
        FragmentBotonesTablero fragment = new FragmentBotonesTablero();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_botones_tablero, container, false);
    }
}