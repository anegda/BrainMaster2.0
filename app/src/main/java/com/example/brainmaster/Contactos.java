package com.example.brainmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class Contactos extends AppCompatActivity {
    ArrayList<String> arraydedatos;
    ArrayList<String> arraypertenecia;
    ArrayList<String> arrayKeys;

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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        //APUNTES DE EGELA TEMA: 11 - CONTENT PROVIDERS
        //BUSCAMOS ENTRE LOS CONTACTOS DE CORREO ELECTRÓNICO DEL DISPOSITIVO
        Uri uridinamica = Uri.parse(String.valueOf(ContactsContract.CommonDataKinds.Email.CONTENT_URI));
        String[] columnas = new String[] {ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Email._ID};
        Cursor cursor = getContentResolver().query(uridinamica, columnas, null, null, null);
        arraydedatos = new ArrayList<String>();
        arraypertenecia = new ArrayList<String>();
        arrayKeys = new ArrayList<String>();
        while(cursor.moveToNext()){
            arraydedatos.add(cursor.getString(0));
            arrayKeys.add(cursor.getString(1));

            //MIRO SI EL EMAIL ESTÁ REGISTRADO A LA APP
            miBD GestorBD = new miBD(Contactos.this, "BrainMaster", null, 1);
            SQLiteDatabase bd = GestorBD.getWritableDatabase();
            String[] campos = new String[] {"Codigo"};
            String [] argumentos = new String[] {cursor.getString(0)};
            Cursor c2 = bd.query("Usuarios",campos,"email=?",argumentos, null,null,null);
            //SI EXISTE EL EMAIL
            if(c2.getCount()>0) {
                arraypertenecia.add(getString(R.string.registrado));
            }else{
                arraypertenecia.add(getString(R.string.noRegistrado));
            }
        }
        String[] a = arraydedatos.toArray(new String[0]);
        String[] a2 = arraypertenecia.toArray(new String[0]);

        //CREAMOS UN LISTVIEW QUE EXPRESA SI NUESTROS CONTACTOS ESTÁN O NO REGISTRADOS EN LA APP
        ArrayAdapter eladaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2,android.R.id.text1,arraydedatos){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View vista= super.getView(position, convertView, parent);
                TextView lineaprincipal=(TextView) vista.findViewById(android.R.id.text1);
                TextView lineasecundaria=(TextView) vista.findViewById(android.R.id.text2);
                lineaprincipal.setText(a[position]);
                lineasecundaria.setText(a2[position].toString());
                return vista;
            }
        };
        ListView lista = (ListView) findViewById(R.id.listaContactos);
        lista.setAdapter(eladaptador);

        //AÑADIMOS LISTENERS PARA ELIMINAR O INVITAR CONTACTOS
        //PULSACIÓN CORTA ES INVITAR
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String email = ((TextView)view.findViewById(android.R.id.text1)).getText().toString();
                String[] emails = {email};
                String asunto = getString(R.string.app_name);
                String texto = getString(R.string.compartirContenido);
                Intent enviarEmail = new Intent(Intent.ACTION_SEND);
                enviarEmail.setType("message/rfc822");
                Log.d("DAS",email);
                enviarEmail.putExtra(Intent.EXTRA_EMAIL, emails);
                enviarEmail.putExtra(Intent.EXTRA_SUBJECT, asunto);
                enviarEmail.putExtra(Intent.EXTRA_TEXT, texto);
                startActivity(Intent.createChooser(enviarEmail, getString(R.string.compartir)));
            }
        });

        //PULSACIÓN LARGA ES ELIMINAR
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String key = arrayKeys.get(i);
                Log.d("DAS",key);
                Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_URI, key);
                getContentResolver().delete(uri, null, null);
                startActivity(getIntent());
                finish();
                return true;
            }
        });

        //BOTÓN DE AÑADIR CONTACTO
        Button btn_anadir = (Button) findViewById(R.id.btn_anadir);
        btn_anadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Contactos.this, AnadirContacto.class));
                finish();
            }
        });
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
        startActivity(new Intent(this, Menu.class));
        finish();
    }
}