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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;

public class Registro extends AppCompatActivity {
    Calendar calendario = Calendar.getInstance();

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

        //INTERFAZ
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //DIALOGO PARA LA FECHA
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int anyo, int mes, int dia) {
                //AL ELEGIR LA FECHA Y PULSAR "ok" LA ESTABLECEMOS COMO TEXTO DEL EDITTEXT
                EditText fechaN = (EditText) findViewById(R.id.fechaNacREdit);
                //HAY QUE +1 AL MES, VA DE (0-11)
                fechaN.setText(Integer.toString(anyo)+"-"+Integer.toString(mes+1)+"-"+Integer.toString(dia));
                //APROVECHAMOS PARA ESTABLECER EL FORMATO DE DATE EN SQL
            }
        };

        /**
         * Basado en el código extraído de Stack Overflow
         * Pregunta: https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
         * Autor: https://stackoverflow.com/users/7874047/ronak-thakkar
         * Modificado por Ane García para traducir varios términos y adaptarlo a la aplicación
         */
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
                //OBTENEMOS TODOS LOS DATOS DE LOS EDITTEXT
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

                //COMPROBAMOS QUE TODOS LOS DATOS HAN SIDO INTRODUCIDOS
                if(nombre.equals("")||apellidos.equals("")||usuario.equals("")||password.equals("")||email.equals("")||fechaNac.equals("")){
                    Toast.makeText(getApplicationContext(), getString(R.string.errorCampos), Toast.LENGTH_SHORT).show();
                }
                else {
                    //LLAMAMOS A LA BD
                    miBD GestorBD = new miBD(Registro.this, "BrainMaster", null, 1);
                    SQLiteDatabase bd = GestorBD.getWritableDatabase();

                    //COMPROBAR QUE EL USUARIO ES ÚNICO
                    String[] campos = new String[] {"Codigo"};
                    String [] argumentos = new String[] {usuario};
                    Cursor c2 = bd.query("Usuarios",campos,"usuario=?",argumentos, null,null,null);
                    if(c2.getCount()>0) {
                        Toast.makeText(getApplicationContext(), getString(R.string.errorRegistro), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //OBTENER STRING DEL BITMAP PARA ALMACENARLO EN LA BD
                        /**
                         * Basado en el código extraído de Stack Overflow
                         * Pregunta: https://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa
                         * Autor: https://stackoverflow.com/users/1191766/sachin10
                         * Modificado por Ane García para traducir varios términos y adaptarlo a la aplicación
                         */
                        ImageView fotoPerfil = (ImageView) findViewById(R.id.fotoDePerfil);
                        Bitmap img = ((BitmapDrawable) fotoPerfil.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        img.compress(Bitmap.CompressFormat.PNG,100,baos);
                        byte [] b = baos.toByteArray();
                        String temp = Base64.getEncoder().encodeToString(b);
                        fotoPerfil.setContentDescription(temp);

                        //INTRODUCIMOS EL USUARIO A LA BD
                        bd.execSQL("INSERT INTO Usuarios ('nombre', 'apellidos', 'usuario', 'password','email','fechaNac','img') VALUES ('" + nombre + "','" + apellidos + "','" + usuario + "','" + password + "','" + email + "','" + fechaNac + "','" + temp + "')");
                        Toast.makeText(getApplicationContext(), getString(R.string.okRegistro), Toast.LENGTH_LONG).show();
                        Cursor c = bd.rawQuery("SELECT * FROM Usuarios", null);
                        Log.d("DAS", Integer.toString(c.getCount()));
                        bd.close();

                        //ESTABLECEMOS NUESTRO NOMBRE DE USUARIO COMO NOMBRE DE RANKING EN LAS PREFERENCIAS (SE PODRÁ CAMBIAR)
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Registro.this);
                        SharedPreferences.Editor editor= prefs.edit();
                        editor.putString("nombre", usuario);
                        editor.apply();

                        //ABRIMOS EL MENU
                        startActivity(new Intent(Registro.this, Menu.class));
                        finish();
                    }
                }
            }
        });

        //FOTO DE PERFIL UTILIZANDO CONTENT PROVIDERS
        /**
         * Basado en el código extraído de Stack Overflow
         * Pregunta: https://stackoverflow.com/questions/38352148/get-image-from-the-gallery-and-show-in-imageview
         * Autor: https://stackoverflow.com/users/6339485/android-nerd
         * Modificado por Ane García para traducir varios términos y adaptarlo a la aplicación
         */
        ImageView fotoPerfil = (ImageView) findViewById(R.id.fotoDePerfil);
        fotoPerfil.setClickable(true);
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 1);   //ESTA DEPRECATED PERO FUNCIONA
                    //ALTERNATIVA A ESTE MÉTODO: https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
                }catch (Exception e){
                    Log.d("DAS","Error la imagen no se carga correctamente");
                }
            }
        });
    }

    //PARA ESTABLECER IMAGEN
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                ImageView fotoPerfil = (ImageView) findViewById(R.id.fotoDePerfil);
                fotoPerfil.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Registro.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(Registro.this,  getString(R.string.error),Toast.LENGTH_SHORT).show();
        }
    }

    //MANTENER DATOS EN HORIZONTAL
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

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

    //RECUPERAR LOS DATOS GUARDADOS
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