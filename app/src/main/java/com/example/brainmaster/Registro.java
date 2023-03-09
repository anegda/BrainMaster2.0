package com.example.brainmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Registro extends AppCompatActivity {
    Calendar calendario = Calendar.getInstance();
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

        //ESTABLECER TEMA UTILIZANDO PREFERENCIAS
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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

        //INTERFAZ
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //DIALOGO PARA LA FECHA
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int anyo, int mes, int dia) {

                EditText fechaN = (EditText) findViewById(R.id.fechaNacREdit);
                fechaN.setText(Integer.toString(anyo)+"-"+Integer.toString(mes+1)+"-"+Integer.toString(dia));
            }
        };

        EditText fechaN = (EditText) findViewById(R.id.fechaNacREdit);
        fechaN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Registro.this,date,calendario.get(Calendar.YEAR),calendario.get(Calendar.MONTH),calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //BOTÓN
        Button btn_login = (Button) findViewById(R.id.btn_reg);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nombreE = (EditText) findViewById(R.id.nombreREdit);
                String nombre = nombreE.getText().toString();
                EditText apellidosE = (EditText) findViewById(R.id.apellidoREdit);
                String apellidos = apellidosE.getText().toString();
                EditText usuarioE = (EditText) findViewById(R.id.usuarioREdit);
                String usuario = usuarioE.getText().toString();
                EditText passwordE = (EditText) findViewById(R.id.contraREdit);
                String password = passwordE.getText().toString();
                EditText emailE = (EditText)  findViewById(R.id.emailREdit);
                String email = emailE.getText().toString();
                EditText fechaNacE = (EditText) findViewById(R.id.fechaNacREdit);
                String fechaNac = fechaNacE.getText().toString();

                miBD GestorBD = new miBD(Registro.this, "BrainMaster", null, 1);
                SQLiteDatabase bd = GestorBD.getWritableDatabase();
                bd.execSQL("INSERT INTO Usuarios ('nombre', 'apellidos', 'usuario', 'password','email','fechaNac') VALUES ('" + nombre + "','" + apellidos + "','" + usuario + "','" + password + "','" + email + "','" + fechaNac +"')");
                Toast.makeText(getApplicationContext(),"Usuario registrado", Toast.LENGTH_LONG).show();
                Cursor c = bd.rawQuery("SELECT * FROM Usuarios",null);
                Log.d("DAS",Integer.toString(c.getCount()));

                bd.close();

                //ABRIMOS EL MENU
                startActivity(new Intent(Registro.this, Menu.class));
            }
        });
    }

    //MANTENER DATOS EN HORIZONTAL
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        String idiomaAct = getIntent().getStringExtra("idiomaAct");
        savedInstanceState.putString("idiomaAct", idiomaAct);

        EditText nombreE = (EditText) findViewById(R.id.nombreREdit);
        String nombre = nombreE.getText().toString();
        savedInstanceState.putString("nombre", nombre);

        EditText apellidosE = (EditText) findViewById(R.id.apellidoREdit);
        String apellidos = apellidosE.getText().toString();
        savedInstanceState.putString("apellidos", apellidos);

        EditText usuarioE = (EditText) findViewById(R.id.usuarioREdit);
        String usuario = usuarioE.getText().toString();
        savedInstanceState.putString("usuario", usuario);

        EditText passwordE = (EditText) findViewById(R.id.contraREdit);
        String password = passwordE.getText().toString();
        savedInstanceState.putString("password", password);

        EditText emailE = (EditText)  findViewById(R.id.emailREdit);
        String email = emailE.getText().toString();
        savedInstanceState.putString("email", email);

        EditText fechaNacE = (EditText) findViewById(R.id.fechaNacREdit);
        String fechaNac = fechaNacE.getText().toString();
        savedInstanceState.putString("fechaNac", fechaNac);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String nombre = savedInstanceState.getString("nombre");
        EditText nombreE = (EditText) findViewById(R.id.nombreREdit);
        nombreE.setText(nombre);

        String apellidos = savedInstanceState.getString("apellidos");
        EditText apellidosE = (EditText) findViewById(R.id.apellidoREdit);
        apellidosE.setText(apellidos);

        String usuario = savedInstanceState.getString("usuario");
        EditText usuarioE = (EditText) findViewById(R.id.usuarioREdit);
        usuarioE.setText(usuario);

        String password = savedInstanceState.getString("password");
        EditText passwordE = (EditText) findViewById(R.id.contraREdit);
        passwordE.setText(password);

        String email = savedInstanceState.getString("email");
        EditText emailE = (EditText)  findViewById(R.id.emailREdit);
        emailE.setText(email);

        String fechaNac = savedInstanceState.getString("fechaNac");
        EditText fechaNacE = (EditText) findViewById(R.id.fechaNacREdit);
        fechaNacE.setText(fechaNac);
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