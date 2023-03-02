package com.example.brainmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int[] logos={R.drawable.botones, R.drawable.palabras};
        String [] nombres={getString(R.string.botones), getString(R.string.palabras)};
        double [] dificultad={2, 1};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ListView juegos = (ListView) findViewById(R.id.lista);
        AdaptadorListView eladap = new AdaptadorListView(getApplicationContext(), nombres, logos, dificultad);
        juegos.setAdapter(eladap);

        //BOTÓN DE AJUSTES
        ImageButton btn_ajustes = (ImageButton) findViewById(R.id.ajustes);
        btn_ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Menu.this, Ajustes.class));
            }
        });
    }
}