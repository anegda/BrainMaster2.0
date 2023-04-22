package com.example.brainmaster;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Locale;

public class JuegoPalabrasTablero extends AppCompatActivity {
    static ClasePalabrasJuego juego;
    static String latitud;
    static String longitud;
    static String nombreUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ESTABLECER IDIOMA USANDO PREFERENCIAS
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idiomapref","es");
        cambiarIdioma(idioma);

        //MANTENER ELEMENTOS EN HORIZONTAL
        if (savedInstanceState == null) {
            juego = new ClasePalabrasJuego(this, idioma);
        }

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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_palabras_tablero);

        //QUITAMOS LA ACTION BAR
        getSupportActionBar().hide();

        //ESTABLECEMOS LA PALABRA ACTUAL
        TextView palabraText = (TextView) findViewById(R.id.palabraText);
        String palabraAct = juego.getPalabra();
        palabraText.setText(palabraAct);

        //ESTABLECEMOS LA PUNTUACIÓN
        TextView puntosText = (TextView) findViewById(R.id.puntosPText);
        int puntuación = juego.getPuntos();
        puntosText.setText(getString(R.string.puntuacion)+" "+Integer.toString(puntuación));

        //OBTENER NOMBRE DE USUARIO
        if (getIntent().hasExtra("usuario")){
            nombreUsuario = getIntent().getStringExtra("usuario");
        }

        //COMPROBAMOS SI EXISTE CONEXIÓN A INTERNET
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||  connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        if(!connected){
            Toast.makeText(getApplicationContext(), getString(R.string.errorConexion), Toast.LENGTH_LONG).show();
        }

        //AÑADIMOS FUNCIONALIDAD AL BOTÓN NUEVO
        Button btn_nuevo = (Button) findViewById(R.id.btn_nuevo);
        btn_nuevo.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                TextView palabraText = (TextView) findViewById(R.id.palabraText);
                String palabraAct = (String) palabraText.getText();

                boolean seguir = juego.comprobarRespuesta(false,palabraAct);
                //SI PERDEMOS
                if(!seguir){
                    //OBTENEMOS LA PUNTUACIÓN
                    int puntos =juego.getPuntos();

                    //OBTENER UBICACIÓN ACTUAL (POSTERIORMENTE HACER UN MAPA DE REGISTROS)
                    /**
                     * Codigo basado en los apuntes de egela: Tema 13 - Geolocalización
                     **/
                    FusedLocationProviderClient proveedordelocalizacion = LocationServices.getFusedLocationProviderClient(JuegoPalabrasTablero.this);
                    proveedordelocalizacion.getLastLocation()
                            .addOnSuccessListener(JuegoPalabrasTablero.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        latitud = String.valueOf(location.getLatitude());
                                        longitud = String.valueOf(location.getLongitude());

                                        //INSERT EN BD REMOTA
                                        Data datos = new Data.Builder()
                                                .putInt("funcion", 5)
                                                .putString("usuario", nombreUsuario)
                                                .putInt("puntos", puntos)
                                                .putString("tipo", "palabras")
                                                .putString("latitud", latitud)
                                                .putString("longitud", longitud)
                                                .build();
                                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos).build();
                                        WorkManager.getInstance(JuegoPalabrasTablero.this).getWorkInfoByIdLiveData(otwr.getId()).observe(JuegoPalabrasTablero.this, new Observer<WorkInfo>() {
                                            @Override
                                            public void onChanged(WorkInfo workInfo) {
                                                if (workInfo != null && workInfo.getState().isFinished()) {
                                                    Log.d("DAS", "INTRODUCIDO");
                                                }
                                            }
                                        });
                                        WorkManager.getInstance(JuegoPalabrasTablero.this).enqueue(otwr);
                                    }else{
                                        //INTRODUCIMOS LA PUNTUACIÓN EN LA BD REMOTA (SIN UBICACIÓN)
                                        Data datos = new Data.Builder()
                                                .putInt("funcion", 5)
                                                .putString("usuario", nombreUsuario)
                                                .putInt("puntos", puntos)
                                                .putString("tipo", "palabras")
                                                .putString("latitud", "")
                                                .putString("longitud", "")
                                                .build();
                                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos).build();
                                        WorkManager.getInstance(JuegoPalabrasTablero.this).getWorkInfoByIdLiveData(otwr.getId()).observe(JuegoPalabrasTablero.this, new Observer<WorkInfo>() {
                                            @Override
                                            public void onChanged(WorkInfo workInfo) {
                                                if (workInfo != null && workInfo.getState().isFinished()) {
                                                    Log.d("DAS", "INTRODUCIDO");
                                                }
                                            }
                                        });
                                        WorkManager.getInstance(JuegoPalabrasTablero.this).enqueue(otwr);
                                    }
                                }
                            })
                            .addOnFailureListener(JuegoPalabrasTablero.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("DAS", "No se puede acceder a la ubicación");
                                    //INTRODUCIMOS LA PUNTUACIÓN EN LA BD REMOTA (SIN UBICACIÓN)
                                    Data datos = new Data.Builder()
                                            .putInt("funcion", 5)
                                            .putString("usuario", nombreUsuario)
                                            .putInt("puntos", puntos)
                                            .putString("tipo", "palabras")
                                            .putString("latitud", "")
                                            .putString("longitud", "")
                                            .build();
                                    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos).build();
                                    WorkManager.getInstance(JuegoPalabrasTablero.this).getWorkInfoByIdLiveData(otwr.getId()).observe(JuegoPalabrasTablero.this, new Observer<WorkInfo>() {
                                        @Override
                                        public void onChanged(WorkInfo workInfo) {
                                            if (workInfo != null && workInfo.getState().isFinished()) {
                                                Log.d("DAS", "INTRODUCIDO");
                                            }
                                        }
                                    });
                                    WorkManager.getInstance(JuegoPalabrasTablero.this).enqueue(otwr);
                                }
                            });

                    //REINICIAMOS JUEGO
                    String idioma = prefs.getString("idiomapref","es");
                    juego = new ClasePalabrasJuego(JuegoPalabrasTablero.this,idioma);

                    //DIÁLOGO DICIENDO QUE HAS PERDIDO
                    new AlertDialog.Builder(JuegoPalabrasTablero.this).setCancelable(false).setIcon(R.drawable.logo).setTitle(getString(R.string.perder)).setMessage(getString(R.string.puntuacion)+" "+Integer.toString(puntos)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(JuegoPalabrasTablero.this, Menu.class));
                            finish();
                        }
                    }).show();
                }
                //NUEVA PALABRA
                String nuevaPalabra = juego.getPalabra();
                palabraText.setText(nuevaPalabra);

                //ESTABLECEMOS LA NUEVA PUNTUACIÓN
                TextView puntosText = (TextView) findViewById(R.id.puntosPText);
                int puntuación = juego.getPuntos();
                puntosText.setText(getString(R.string.puntuacion)+" "+Integer.toString(puntuación));
            }
        });

        //AÑADIMOS FUNCIONALIDAD AL BOTÓN VISTO
        Button btn_visto = (Button) findViewById(R.id.btn_visto);
        btn_visto.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                TextView palabraText = (TextView) findViewById(R.id.palabraText);
                String palabraAct = (String) palabraText.getText();

                boolean seguir = juego.comprobarRespuesta(true,palabraAct);
                //SI PERDEMOS
                if(!seguir){
                    //OBTENEMOS LA PUNTUACIÓN
                    int puntos =juego.getPuntos();

                    //OBTENER UBICACIÓN ACTUAL (POSTERIORMENTE HACER UN MAPA DE REGISTROS)
                    /**
                     * Codigo basado en los apuntes de egela: Tema 13 - Geolocalización
                     **/
                    FusedLocationProviderClient proveedordelocalizacion = LocationServices.getFusedLocationProviderClient(JuegoPalabrasTablero.this);
                    proveedordelocalizacion.getLastLocation()
                            .addOnSuccessListener(JuegoPalabrasTablero.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        latitud = String.valueOf(location.getLatitude());
                                        longitud = String.valueOf(location.getLongitude());

                                        //INSERT EN BD REMOTA
                                        Data datos = new Data.Builder()
                                                .putInt("funcion", 5)
                                                .putString("usuario", nombreUsuario)
                                                .putInt("puntos", puntos)
                                                .putString("tipo", "palabras")
                                                .putString("latitud", latitud)
                                                .putString("longitud", longitud)
                                                .build();
                                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos).build();
                                        WorkManager.getInstance(JuegoPalabrasTablero.this).getWorkInfoByIdLiveData(otwr.getId()).observe(JuegoPalabrasTablero.this, new Observer<WorkInfo>() {
                                            @Override
                                            public void onChanged(WorkInfo workInfo) {
                                                if (workInfo != null && workInfo.getState().isFinished()) {
                                                    Log.d("DAS", "INTRODUCIDO");
                                                }
                                            }
                                        });
                                        WorkManager.getInstance(JuegoPalabrasTablero.this).enqueue(otwr);
                                    }else{
                                        //INTRODUCIMOS LA PUNTUACIÓN EN LA BD REMOTA (SIN UBICACIÓN)
                                        Data datos = new Data.Builder()
                                                .putInt("funcion", 5)
                                                .putString("usuario", nombreUsuario)
                                                .putInt("puntos", puntos)
                                                .putString("tipo", "palabras")
                                                .putString("latitud", "")
                                                .putString("longitud", "")
                                                .build();
                                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos).build();
                                        WorkManager.getInstance(JuegoPalabrasTablero.this).getWorkInfoByIdLiveData(otwr.getId()).observe(JuegoPalabrasTablero.this, new Observer<WorkInfo>() {
                                            @Override
                                            public void onChanged(WorkInfo workInfo) {
                                                if (workInfo != null && workInfo.getState().isFinished()) {
                                                    Log.d("DAS", "INTRODUCIDO");
                                                }
                                            }
                                        });
                                        WorkManager.getInstance(JuegoPalabrasTablero.this).enqueue(otwr);
                                    }
                                }
                            })
                            .addOnFailureListener(JuegoPalabrasTablero.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("DAS", "No se puede acceder a la ubicación");
                                    //INTRODUCIMOS LA PUNTUACIÓN EN LA BD REMOTA (SIN UBICACIÓN)
                                    Data datos = new Data.Builder()
                                            .putInt("funcion", 5)
                                            .putString("usuario", nombreUsuario)
                                            .putInt("puntos", puntos)
                                            .putString("tipo", "palabras")
                                            .putString("latitud", "")
                                            .putString("longitud", "")
                                            .build();
                                    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos).build();
                                    WorkManager.getInstance(JuegoPalabrasTablero.this).getWorkInfoByIdLiveData(otwr.getId()).observe(JuegoPalabrasTablero.this, new Observer<WorkInfo>() {
                                        @Override
                                        public void onChanged(WorkInfo workInfo) {
                                            if (workInfo != null && workInfo.getState().isFinished()) {
                                                Log.d("DAS", "INTRODUCIDO");
                                            }
                                        }
                                    });
                                    WorkManager.getInstance(JuegoPalabrasTablero.this).enqueue(otwr);
                                }
                            });

                    //REINICIAMOS JUEGO
                    String idioma = prefs.getString("idiomapref","es");
                    juego = new ClasePalabrasJuego(JuegoPalabrasTablero.this,idioma);

                    //DIÁLOGO DICIENDO QUE HAS PERDIDO
                    new AlertDialog.Builder(JuegoPalabrasTablero.this).setCancelable(false).setIcon(R.drawable.logo).setTitle(getString(R.string.perder)).setMessage(getString(R.string.puntuacion)+" "+Integer.toString(puntos)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(JuegoPalabrasTablero.this, Menu.class));
                            finish();
                        }
                    }).show();
                }
                //NUEVA PALABRA
                String nuevaPalabra = juego.getPalabra();
                palabraText.setText(nuevaPalabra);

                //ESTABLECEMOS LA NUEVA PUNTUACIÓN
                TextView puntosText = (TextView) findViewById(R.id.puntosPText);
                int puntuación = juego.getPuntos();
                puntosText.setText(getString(R.string.puntuacion)+" "+Integer.toString(puntuación));
            }
        });

        //BOTÓN QUE EXPLICA LAS REGLAS
        ImageButton btn_reglasP = findViewById(R.id.btn_reglasP);
        btn_reglasP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(JuegoPalabrasTablero.this).setIcon(R.drawable.logo).setTitle(getString(R.string.reglas)).setMessage(getString(R.string.reglasMP)).show();
            }
        });
    }

    //GUARDAMOS LA INFORMACIÓN SI PASAMOS A HORIZONTAL / LLAMADAS
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //GUARDAR PALABRA ACTUAL
        TextView palabraText = (TextView) findViewById(R.id.palabraText);
        String palabraAct = (String) palabraText.getText();
        savedInstanceState.putString("palabraAct", palabraAct);
    }

    //RECUPERAMOS LA INFORMACIÓN GUARDADA
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //PONGO LA PALABRA
        String palabraAct = savedInstanceState.getString("palabraAct");
        TextView palabraText = (TextView) findViewById(R.id.palabraText);
        palabraText.setText(palabraAct);
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

    //DIALOG AL INTENTAR SALIR DE LA APP
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(R.drawable.logo).setTitle(getString(R.string.salir)).setMessage(getString(R.string.salirM)).setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(JuegoPalabrasTablero.this, Menu.class));
                        finish();
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}