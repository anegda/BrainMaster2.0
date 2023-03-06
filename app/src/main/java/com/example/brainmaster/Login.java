package com.example.brainmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class Login extends AppCompatActivity {
    int pTema;
    String pIdioma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //MANTENER IDIOMA EN HORIZONTAL
        if (savedInstanceState != null) {
            pIdioma = savedInstanceState.getString("idiomaAct");
            cambiarIdioma(pIdioma);
        }
        else{
            Locale locale = getResources().getConfiguration().getLocales().get(0);
            pIdioma = locale.getLanguage();
            getIntent().putExtra("idiomaAct",pIdioma);
        }

        //INTERFAZ Y LOGIN
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText usuarioE = (EditText) findViewById(R.id.usuarioEdit);
                String usuario = usuarioE.getText().toString();
                EditText passwordE = (EditText) findViewById(R.id.passwordEdit);
                String password = passwordE.getText().toString();

                miBD GestorBD = new miBD(Login.this, "BrainMaster", null, 1);
                SQLiteDatabase bd = GestorBD.getWritableDatabase();
                String[] campos = new String[] {"Codigo"};
                String [] argumentos = new String[] {usuario,password};
                Cursor c2 = bd.query("Usuarios",campos,"usuario=? AND password=?",argumentos, null,null,null);
                if(c2.getCount()>0) {
                    c2.close();
                    bd.close();
                    Toast.makeText(getApplicationContext(), "Login correcto.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Login.this, Menu.class));
                }
                else{
                    Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //MANTENER DATOS EN HORIZONTAL
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        String idiomaAct = getIntent().getStringExtra("idiomaAct");
        savedInstanceState.putString("idiomaAct", idiomaAct);

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
}