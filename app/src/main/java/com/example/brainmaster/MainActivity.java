package com.example.brainmaster;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_reg = (Button) findViewById(R.id.btn_registrar);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Registro.class));
            }
        });

        Button btn_log = (Button) findViewById(R.id.btn_entrar);
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        ImageButton btn_idiomas = (ImageButton) findViewById(R.id.idiomas);
        /*btn_idiomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.idiomas));
                final CharSequence[] opciones = {"Español", "English", "Euskera"};
                builder.setSingleChoiceItems(opciones, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            Locale nuevaloc = new Locale("en");
                            Locale.setDefault(nuevaloc);
                            Configuration configuration = getBaseContext().getResources().getConfiguration();
                            configuration.setLocale(nuevaloc);
                            configuration.setLayoutDirection(nuevaloc);

                            Context context = getBaseContext().createConfigurationContext(configuration);
                            getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
                        }
                        else if(i==1){
                            Locale nuevaloc = new Locale("es");
                            Locale.setDefault(nuevaloc);
                            Configuration configuration = getBaseContext().getResources().getConfiguration();
                            configuration.setLocale(nuevaloc);
                            configuration.setLayoutDirection(nuevaloc);

                            Context context = getBaseContext().createConfigurationContext(configuration);
                            getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
                        }
                        else{
                            Locale nuevaloc = new Locale("eu");
                            Locale.setDefault(nuevaloc);
                            Configuration configuration = getBaseContext().getResources().getConfiguration();
                            configuration.setLocale(nuevaloc);
                            configuration.setLayoutDirection(nuevaloc);

                            Context context = getBaseContext().createConfigurationContext(configuration);
                            getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
                        }
                    }
                });
            }
        });*/

    }
}