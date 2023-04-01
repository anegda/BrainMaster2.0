package com.example.brainmaster;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_MAGENTA;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

        //LLAMAMOS A LA BASE DE DATOS Y HACEMOS UN SELECT DE LAS PARTIDAS REALIZADAS ORDENADAS POR PUNTUACIÓN DESCENDENTEMENTE
        miBD GestorBD = new miBD(this, "BrainMaster", null, 1);
        SQLiteDatabase bd = GestorBD.getWritableDatabase();
        String[] campos = new String[] {"usuario","puntos","tipo","latitud","longitud"};
        Cursor c2 = bd.query("Partidas",campos,null,null, null,null,"puntos DESC");

        while (c2.moveToNext()){
            String usuario = c2.getString(0);
            int puntos = c2.getInt(1);
            String tipo = c2.getString(2);
            String latitud = c2.getString(3);
            String longitud = c2.getString(4);
            if(!latitud.equals("") && !longitud.equals("")){
                LatLng pos = new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud));
                if(tipo.equals("palabras")){
                    mMap.addMarker(new MarkerOptions().position(pos).title(usuario + "("+tipo+"): " + Integer.toString(puntos)).icon(BitmapDescriptorFactory.defaultMarker(HUE_MAGENTA)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                }
                else{
                    mMap.addMarker(new MarkerOptions().position(pos).title(usuario + "("+tipo+"): " + Integer.toString(puntos)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                }
            }
        }
    }

    //VOLVEMOS A MENU SI PULSAMOS ATRAS
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Menu.class));
        finish();
    }
}