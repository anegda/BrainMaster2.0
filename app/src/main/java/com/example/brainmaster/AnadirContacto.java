package com.example.brainmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Locale;

public class AnadirContacto extends AppCompatActivity {

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

        //CREAR INTERFAZ
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_contacto);

        //QUITAMOS LA ACTION BAR
        getSupportActionBar().hide();

        Button btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nombreA = (EditText) findViewById(R.id.nombreAEdit);
                String nombre = nombreA.getText().toString();
                EditText apellidosA = (EditText) findViewById(R.id.apellidoAEdit);
                String apellidos = apellidosA.getText().toString();
                EditText emailA = (EditText) findViewById(R.id.emailAEdit);
                String email = emailA.getText().toString();

                /**
                 * Basado en el código extraído de Stack Overflow
                 * Pregunta: https://stackoverflow.com/questions/4459138/insert-contact-in-android-with-contactscontract
                 * Autor: https://stackoverflow.com/users/2692601/suresh-pareek
                 * Modificado por Ane García para traducir varios términos y adaptarlo a la aplicación
                 */

                //GUARDAMOS EN NUESTRA CUENTA
                ContentValues p=new ContentValues();
                p.put(ContactsContract.RawContacts.ACCOUNT_TYPE, "com.google");
                p.put(ContactsContract.RawContacts.ACCOUNT_NAME, "email");
                Uri rowcontect= getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, p);
                long rawcontectid= ContentUris.parseId(rowcontect);

                //CREAMOS EL CONTACTO
                ContentValues value = new ContentValues();
                value.put(ContactsContract.Data.RAW_CONTACT_ID,rawcontectid);
                value.put(android.provider.ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                value.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nombre + " " + apellidos);
                getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, value);

                //CREAMOS EL EMAIL
                ContentValues ppv=new ContentValues();
                ppv.put(android.provider.ContactsContract.Data.RAW_CONTACT_ID, rawcontectid);
                ppv.put(android.provider.ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                ppv.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email);
                ppv.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_MOBILE);
                getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, ppv);

                startActivity(new Intent(AnadirContacto.this, Contactos.class));
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        EditText nombreA = (EditText) findViewById(R.id.nombreAEdit);
        String nombre = nombreA.getText().toString();
        savedInstanceState.putString("nombre",nombre);
        EditText apellidosA = (EditText) findViewById(R.id.apellidoAEdit);
        String apellidos = apellidosA.getText().toString();
        savedInstanceState.putString("apellidos",apellidos);
        EditText emailA = (EditText) findViewById(R.id.emailAEdit);
        String email = emailA.getText().toString();
        savedInstanceState.putString("email",email);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        EditText nombreA = (EditText) findViewById(R.id.nombreAEdit);
        String nombre = savedInstanceState.getString("nombre");
        nombreA.setText(nombre);
        EditText apellidosA = (EditText) findViewById(R.id.apellidoAEdit);
        String apellidos = savedInstanceState.getString("apellidos");
        apellidosA.setText(apellidos);
        EditText emailA = (EditText) findViewById(R.id.emailAEdit);
        String email = savedInstanceState.getString("email");
        emailA.setText(email);
    }

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

    //VOLVEMOS A MAINACTIVITY SI PULSAMOS ATRAS
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Contactos.class));
        finish();
    }
}