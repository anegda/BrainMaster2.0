package com.example.brainmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}