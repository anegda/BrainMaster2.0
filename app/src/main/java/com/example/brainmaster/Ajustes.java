package com.example.brainmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

public class Ajustes extends AppCompatActivity {
    int tema=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().hasExtra("bundle") && savedInstanceState==null){
            savedInstanceState = getIntent().getExtras().getBundle("bundle");
            tema = getIntent().getExtras().getInt("tema");
        }

        if(tema==1) {
            setTheme(R.style.Theme_BrainMaster);

        }
        else if(tema==2){
            setTheme(R.style.Theme_BrainMasterSummer);
        }
        else{
            setTheme(R.style.Theme_BrainMasterPunk);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        //https://code.tutsplus.com/es/tutorials/how-to-add-a-dropdown-menu-in-android-studio--cms-37860
        Spinner temas = findViewById(R.id.temasDes);
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this, R.array.temas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        temas.setAdapter(adapter);

        Button btn_guardar = (Button) findViewById(R.id.btn_guardar);
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Notificaciones
                NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(Ajustes.this,"11");
                elBuilder.setSmallIcon(R.drawable.logo)
                        .setContentTitle("Has actualizado la configuración")
                        .setContentText("Te informamos de que has cambiado la configuración y esta ha sido guardada.")
                        .setSubText("Aprovecha ahora para jugar")
                        .setVibrate(new long[] {0,500,100,1000})
                        .setAutoCancel(true);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    NotificationChannel elCanal = new NotificationChannel("11","NombreCanal",NotificationManager.IMPORTANCE_DEFAULT);
                    elCanal.setDescription("Descripción del canal");
                    elCanal.enableLights(true);
                    elCanal.setLightColor(Color.RED);
                    elCanal.setVibrationPattern(new long[]{0, 500, 100, 1000});
                    elCanal.enableVibration(true);
                    elManager.createNotificationChannel(elCanal);
                }
                elManager.notify(11, elBuilder.build());

                //Temas
                Spinner temas = findViewById(R.id.temasDes);
                String temaElegido = temas.getSelectedItem().toString();
                String[] temaElegidoS = temaElegido.split(" ");
                int valor = Integer.parseInt(temaElegidoS[0]);

                Bundle temp_bundle = new Bundle();
                onSaveInstanceState(temp_bundle);
                Intent intent = new Intent(Ajustes.this, Ajustes.class);
                intent.putExtra("bundle", temp_bundle);
                intent.putExtra("tema", valor);
                setResult(RESULT_OK, intent);

                startActivity(intent);
                finish();
            }
        });

    }
}