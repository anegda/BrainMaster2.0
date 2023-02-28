package com.example.brainmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}