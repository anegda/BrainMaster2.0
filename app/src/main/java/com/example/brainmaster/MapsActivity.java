package com.example.brainmaster;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_MAGENTA;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.brainmaster.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
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

        super.onCreate(savedInstanceState);

        //OBTENER NOMBRE DE USUARIO
        if (getIntent().hasExtra("usuario")){
            nombreUsuario = getIntent().getStringExtra("usuario");
        }

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //LLAMAMOS A LA BD REMOTA Y HACEMOS UN SELECT DE LAS PARTIDAS REALIZADAS
        Data datos = new Data.Builder()
                .putInt("funcion",7)
                .putString("usuario", nombreUsuario).build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos).build();
        WorkManager.getInstance(MapsActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(MapsActivity.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if(workInfo!=null && workInfo.getState().isFinished()){
                    Data outputData = workInfo.getOutputData();
                    if(outputData!=null){
                        String result = outputData.getString("result");
                        JSONParser parser = new JSONParser();
                        try {
                            JSONArray jsonArray = (JSONArray) parser.parse(result);
                            for(int i=0; i < jsonArray.size(); i++){
                                JSONObject obj = (JSONObject) jsonArray.get(i);
                                String usuario = (String) obj.get("usuario");
                                String puntos = (String) obj.get("puntos");
                                String tipo = (String) obj.get("tipo");
                                String latitud = (String) obj.get("latitud");
                                String longitud = (String) obj.get("longitud");

                                if(!latitud.equals("") && !longitud.equals("")){
                                    LatLng pos = new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud));
                                    if(tipo.equals("palabras")){
                                        mMap.addMarker(new MarkerOptions().position(pos).title(usuario + ": " + puntos).icon(BitmapDescriptorFactory.defaultMarker(HUE_MAGENTA)));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                                    }
                                    else{
                                        mMap.addMarker(new MarkerOptions().position(pos).title(usuario + ": " + puntos).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                                    }
                                }

                            }
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        WorkManager.getInstance(MapsActivity.this).enqueue(otwr);
    }

    //CAMBIAR IDIOMA
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

    //VOLVEMOS A MENU SI PULSAMOS ATRAS
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Menu.class));
        finish();
    }
}