package com.example.brainmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class Menu extends AppCompatActivity {
    static String nombreUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ESTABLECER IDIOMA USANDO PREFERENCIAS
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idiomapref","es");
        cambiarIdioma(idioma);

        //ESTABLECER TEMA UTILIZANDO PREFERENCIAS
        String tema = prefs.getString("temapref","1");
        if(tema.equals("1")) {
            Log.d("DAS",tema+" 1");
            setTheme(R.style.Theme_BrainMaster);
        }
        else if(tema.equals("2")){
            Log.d("DAS",tema+" 2");
            setTheme(R.style.Theme_BrainMasterSummer);
        }
        else if(tema.equals("3")){
            Log.d("DAS",tema+" 3");
            setTheme(R.style.Theme_BrainMasterPunk);
        }
        else{
            Log.d("DAS",tema+" 4");
            setTheme(R.style.Theme_BrainMaster);
        }

        //CREAR INTERFAZ
        int[] logos={R.drawable.botones, R.drawable.palabras, R.drawable.trofeo,R.drawable.mapa};
        String [] nombres={getString(R.string.botones), getString(R.string.palabras), getString(R.string.ranking),getString(R.string.mapa)};
        double [] dificultad={2, 1, 0, 0};

        //CREAMOS EL LISTVIEW PERSONALIZADO
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ListView juegos = (ListView) findViewById(R.id.listaMenu);
        AdaptadorListView eladap = new AdaptadorListView(getApplicationContext(), nombres, logos, dificultad);
        juegos.setAdapter(eladap);

        //PONEMOS EL NOMBRE DE USUARIO (STATIC) PARA SER ACCEDIDO DESDE TODA LA APP
        Intent i = getIntent();
        if(i.hasExtra("usuario")) {
            nombreUsuario = i.getStringExtra("usuario");
        }

        //NAVIGATION DRAWER
        DrawerLayout elmenudesplegable = findViewById(R.id.drawer_layout);
        NavigationView elnavigation = findViewById(R.id.elnavigationview);
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        elnavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.preferencias:
                        startActivity(new Intent(Menu.this, Ajustes.class));
                        finish();
                        break;
                    case R.id.perfil:
                        startActivity(new Intent(Menu.this, Perfil.class));
                        finish();
                        break;
                    case R.id.compartir:
                        /**
                         * Código basado en: https://www.programaenlinea.net/crear-boton-compartir-android/
                         * Autor: NGuerrero
                         * Modificado por Ane García para adaptar los textos mostrados.
                         */

                        Intent compartir = new Intent(android.content.Intent.ACTION_SEND);
                        compartir.setType("text/plain");
                        compartir.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        compartir.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.compartirContenido));
                        startActivity(Intent.createChooser(compartir, "Compartir vía"));
                        break;
                    case R.id.contactos:
                        startActivity(new Intent(Menu.this, Contactos.class));
                        finish();
                        break;
                }
                elmenudesplegable.closeDrawers();
                return false;
            }
        });

        //ONCLICK DE LA LISTA => ABRIMOS LA ACTIVIDAD CORRESPONDIENTE Y CERRAMOS ESTA
        juegos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    startActivity(new Intent(Menu.this, JuegoBotonesTablero.class));
                    finish();
                }
                else if(position==1){
                    startActivity(new Intent(Menu.this, JuegoPalabrasTablero.class));
                    finish();
                }
                else if(position==2){
                    startActivity(new Intent(Menu.this, Ranking.class));
                    finish();
                }
                else if(position==3){
                    startActivity(new Intent(Menu.this, MapsActivity.class));
                    finish();
                }
            }
        });
    }

    protected void cambiarIdioma(String idioma){
        Log.d("DAS",idioma);
        Locale nuevaloc = new Locale(idioma);
        Locale.setDefault(nuevaloc);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    //NAVIGATION DRAWER
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                DrawerLayout elmenudesplegable = findViewById(R.id.drawer_layout);
                elmenudesplegable.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //SI PULSAMOS LA FLECHA ATRÁS
    @Override
    public void onBackPressed() {
        DrawerLayout elmenudesplegable = findViewById(R.id.drawer_layout);
        if (elmenudesplegable.isDrawerOpen(GravityCompat.START)) {
            elmenudesplegable.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}