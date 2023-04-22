package com.example.brainmaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

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

        //COMPROBAMOS SI EXISTE CONEXIÓN A INTERNET
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||  connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        if(!connected){
            Toast.makeText(getApplicationContext(), getString(R.string.errorConexion), Toast.LENGTH_LONG).show();
        }

        //APUNTES DE EGELA TEMA: 11 - CONTENT PROVIDERS
        //BUSCAMOS ENTRE LOS CONTACTOS DE CORREO ELECTRÓNICO DEL DISPOSITIVO
        Uri uridinamica = Uri.parse(String.valueOf(ContactsContract.CommonDataKinds.Email.CONTENT_URI));
        String[] columnas = new String[] {ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Email._ID};
        Cursor cursor = getContentResolver().query(uridinamica, columnas, null, null, null);
        arraydedatos = new ArrayList<String>();
        arraypertenecia = new ArrayList<String>();
        arrayKeys = new ArrayList<String>();
        while(cursor.moveToNext()){
            //MIRO SI EL EMAIL ESTÁ REGISTRADO A LA APP
            String email = cursor.getString(0);
            String key = cursor.getString(1);
            Data datos0 = new Data.Builder()
                    .putInt("funcion",9)
                    .putString("email", cursor.getString(0)).build();
            OneTimeWorkRequest otwr0 = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos0).build();
            WorkManager.getInstance(Contactos.this).getWorkInfoByIdLiveData(otwr0.getId()).observe(Contactos.this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(WorkInfo workInfo) {
                    if(workInfo!=null && workInfo.getState().isFinished()){
                        arraydedatos.add(email);
                        arrayKeys.add(key);

                        Data outputData = workInfo.getOutputData();
                        boolean registrado = outputData.getBoolean("registrado", false);
                        if(registrado) {
                            arraypertenecia.add(getString(R.string.registrado));
                        }
                        else{
                            arraypertenecia.add(getString(R.string.noRegistrado));
                        }
                        String[] a = arraydedatos.toArray(new String[0]);
                        String[] a2 = arraypertenecia.toArray(new String[0]);

                        Log.d("DAS", String.valueOf(a.length));
                        Log.d("DAS", String.valueOf(a2.length));

                        //CREAMOS UN LISTVIEW QUE EXPRESA SI NUESTROS CONTACTOS ESTÁN O NO REGISTRADOS EN LA APP
                        ArrayAdapter eladaptador = new ArrayAdapter<String>(Contactos.this, android.R.layout.simple_list_item_2,android.R.id.text1,arraydedatos){
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
                    }
                }
            });
            WorkManager.getInstance(Contactos.this).enqueue(otwr0);
        }

        //AÑADIMOS LISTENERS PARA ELIMINAR O INVITAR CONTACTOS
        //PULSACIÓN CORTA ES INVITAR
        ListView lista = (ListView) findViewById(R.id.listaContactos);
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