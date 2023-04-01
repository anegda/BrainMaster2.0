package com.example.brainmaster;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.brainmaster.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //AÑADIMOS EL ESTILO AL MAPA: https://developers.google.com/maps/documentation/android-sdk/styling?hl=es-419#string-resource
        googleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_strings)));

        //LLAMAMOS A LA BASE DE DATOS Y HACEMOS UN SELECT DE LAS PARTIDAS REALIZADAS ORDENADAS POR PUNTUACIÓN DESCENDENTEMENTE
        miBD GestorBD = new miBD(this, "BrainMaster", null, 1);
        SQLiteDatabase bd = GestorBD.getWritableDatabase();
        String[] campos = new String[] {"usuario","puntos","latitud","longitud"};
        Cursor c2 = bd.query("Partidas",campos,null,null, null,null,"puntos DESC");

        while (c2.moveToNext()){
            String usuario = c2.getString(0);
            int puntos = c2.getInt(1);
            String latitud = c2.getString(2);
            String longitud = c2.getString(3);
            if(!latitud.equals("") && !longitud.equals("")){
                LatLng pos = new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud));
                mMap.addMarker(new MarkerOptions().position(pos).title(usuario + ": " + Integer.toString(puntos)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            }
        }
    }

    //VOLVEMOS A MENU SI PULSAMOS ATRAS
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Menu.class));
    }
}