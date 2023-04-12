package com.example.brainmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class Login extends AppCompatActivity {
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

        //INTERFAZ Y LOGIN
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //QUITAMOS LA ACTION BAR
        getSupportActionBar().hide();

        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OBTENEMOS LO ESCRITO POR EL USUARIO
                EditText usuarioE = (EditText) findViewById(R.id.usuarioEdit);
                String usuario = usuarioE.getText().toString();
                EditText passwordE = (EditText) findViewById(R.id.passwordEdit);
                String password = passwordE.getText().toString();

                //LLAMAMOS A LA BD REMOTA
                final boolean[] correcto = {false};
                Data datos0 = new Data.Builder()
                        .putInt("funcion",3)
                        .putString("usuario", usuario)
                        .putString("password", password).build();
                OneTimeWorkRequest otwr0 = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos0).build();
                WorkManager.getInstance(Login.this).getWorkInfoByIdLiveData(otwr0.getId()).observe(Login.this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo!=null && workInfo.getState().isFinished()){
                            Data outputData = workInfo.getOutputData();
                            correcto[0] = outputData.getBoolean("correcto", false);
                        }
                    }
                });
                WorkManager.getInstance(Login.this).enqueue(otwr0);

                //ESPERAMOS UN POCO PARA RECIBIR LA RESPUESTA
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if(correcto[0]) {
                            //CREAMOS TOAST INDICANDO QUE EL LOGIN ES CORRECTO
                            Toast.makeText(getApplicationContext(), getString(R.string.okLogin), Toast.LENGTH_LONG).show();

                            //VAMOS AL MENÚ
                            Intent i = new Intent(Login.this, Menu.class);
                            i.putExtra("usuario",usuario);
                            startActivity(i);
                            finish();
                        }
                        else{
                            //TOAST DICIENDO QUE HA OCURRIDO UN ERROR
                            Toast.makeText(getApplicationContext(), getString(R.string.errorLogin), Toast.LENGTH_LONG).show();
                        }
                    }
                },1000);
            }
        });
    }

    //MANTENER DATOS EN HORIZONTAL
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        EditText usuarioE = (EditText) findViewById(R.id.usuarioEdit);
        String usuario = usuarioE.getText().toString();
        savedInstanceState.putString("usuario", usuario);

        EditText passwordE = (EditText) findViewById(R.id.passwordEdit);
        String password = passwordE.getText().toString();
        savedInstanceState.putString("password", password);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String usuario = savedInstanceState.getString("usuario");
        EditText usuarioE = (EditText) findViewById(R.id.usuarioEdit);
        usuarioE.setText(usuario);

        String password = savedInstanceState.getString("password");
        EditText passwordE = (EditText) findViewById(R.id.passwordEdit);
        passwordE.setText(password);
    }

    //CAMBIAR IDIOMA
    protected void cambiarIdioma(String idioma){
        Locale nuevaloc = new Locale(idioma);
        Locale.setDefault(nuevaloc);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    //VOLVEMOS A MAINACTIVITY SI PULSAMOS ATRAS
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}