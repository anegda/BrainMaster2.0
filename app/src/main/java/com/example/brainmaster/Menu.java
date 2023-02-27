package com.example.brainmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int[] logos={R.drawable.botones, R.drawable.palabras};
        String [] nombres={"Botones", "Palabras"};
        double [] dificultad={2.0, 1.5};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ListView juegos = (ListView) findViewById(R.id.lista);
        AdaptadorListView eladap = new AdaptadorListView(getApplicationContext(), nombres, logos, dificultad);
        juegos.setAdapter(eladap);
    }
}