package com.example.brainmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;

public class Perfil extends AppCompatActivity {
    Calendar calendario = Calendar.getInstance();

    static String fotoDePerfil;
    static String nombreUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ESTABLECER IDIOMA USANDO PREFERENCIAS
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idiomapref", "es");
        cambiarIdioma(idioma);

        //ESTABLECER TEMA UTILIZANDO PREFERENCIAS
        String tema = prefs.getString("temapref", "1");
        if (tema.equals("1")) {
            Log.d("DAS", tema + " 1");
            setTheme(R.style.Theme_BrainMaster);
        } else if (tema.equals("2")) {
            Log.d("DAS", tema + " 2");
            setTheme(R.style.Theme_BrainMasterSummer);
        } else if (tema.equals("3")) {
            Log.d("DAS", tema + " 3");
            setTheme(R.style.Theme_BrainMasterPunk);
        } else {
            Log.d("DAS", tema + " 4");
            setTheme(R.style.Theme_BrainMaster);
        }

        //INTERFAZ
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //RELLENAMOS LOS CAMPOS
        if (getIntent().hasExtra("usuario")){
            nombreUsuario = getIntent().getStringExtra("usuario");
        }
        Data datos0 = new Data.Builder()
                .putInt("funcion",2)
                .putString("usuario", nombreUsuario).build();
        OneTimeWorkRequest otwr0 = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos0).build();
        WorkManager.getInstance(Perfil.this).getWorkInfoByIdLiveData(otwr0.getId()).observe(Perfil.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if(workInfo!=null && workInfo.getState().isFinished()){
                    Data outputData = workInfo.getOutputData();
                    if(outputData!=null){
                        String nombre = outputData.getString("nombre");
                        EditText nombreE = (EditText) findViewById(R.id.nombreEEdit);
                        nombreE.setText(nombre);
                        nombreE.setEnabled(false);

                        String apellidos = outputData.getString("apellidos");
                        EditText apellidosE = (EditText) findViewById(R.id.apellidoEEdit);
                        apellidosE.setText(apellidos);
                        apellidosE.setEnabled(false);

                        String usuario = outputData.getString("usuario");
                        EditText usuarioE = (EditText) findViewById(R.id.usuarioEEdit);
                        usuarioE.setText(usuario);
                        usuarioE.setEnabled(false);

                        String email = outputData.getString("email");
                        EditText emailE = (EditText)  findViewById(R.id.emailEEdit);
                        emailE.setText(email);
                        emailE.setEnabled(false);

                        /**
                         * Basado en el código extraído de Stack Overflow
                         * Pregunta: https://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa
                         * Autor: https://stackoverflow.com/users/1191766/sachin10
                         * Modificado por Ane García para traducir varios términos y adaptarlo a la aplicación
                         */
                        byte [] encodeByte = Base64.getDecoder().decode(fotoDePerfil);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                        ImageView imgView=(ImageView) findViewById(R.id.fotoDePerfilE);
                        imgView.setImageBitmap(bitmap);
                    }
                }
            }
        });
        WorkManager.getInstance(Perfil.this).enqueue(otwr0);

        //QUITAMOS LA ACTION BAR
        getSupportActionBar().hide();

        //BOTÓN
        Button btn_editar = (Button) findViewById(R.id.btn_editar);
        btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OBTENEMOS LOS DATOS DE LOS EDITTEXT

                EditText passwordE = (EditText) findViewById(R.id.contraEEdit);
                String password = passwordE.getText().toString();

                EditText usuarioE = (EditText) findViewById(R.id.usuarioEEdit);
                String usuario = usuarioE.getText().toString();

                //COMPROBAMOS QUE TODOS LOS DATOS HAN SIDO INTRODUCIDOS
                if (password.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.errorCampos), Toast.LENGTH_SHORT).show();
                } else {
                    //OBTENER STRING DEL BITMAP PARA ALMACENARLO EN LA BD
                    /**
                     * Basado en el código extraído de Stack Overflow
                     * Pregunta: https://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa
                     * Autor: https://stackoverflow.com/users/1191766/sachin10
                     * Modificado por Ane García para traducir varios términos y adaptarlo a la aplicación
                     */
                    ImageView fotoPerfil = (ImageView) findViewById(R.id.fotoDePerfilE);
                    Bitmap img = ((BitmapDrawable) fotoPerfil.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    //PARA QUE NO EXISTAN PROBLEMAS CON EL TAMAÑO DE LA IMAGEN
                    b = tratarImagen(b);
                    fotoDePerfil = Base64.getEncoder().encodeToString(b);

                    //ACTUALIZAMOS EL USUARIO EN LA BD
                    Data datos0 = new Data.Builder()
                            .putInt("funcion",4)
                            .putString("usuario", usuario)
                            .putString("password", password).build();
                    OneTimeWorkRequest otwr0 = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos0).build();
                    WorkManager.getInstance(Perfil.this).getWorkInfoByIdLiveData(otwr0.getId()).observe(Perfil.this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if(workInfo!=null && workInfo.getState().isFinished()){
                                Data outputData = workInfo.getOutputData();
                                if(outputData!=null){
                                    //ABRIMOS EL MENU
                                    Intent i = new Intent(Perfil.this, Menu.class);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }
                    });
                    WorkManager.getInstance(Perfil.this).enqueue(otwr0);

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
        ImageView fotoPerfil = (ImageView) findViewById(R.id.fotoDePerfilE);
        fotoPerfil.setClickable(true);
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent i1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    Intent i2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                    chooser.putExtra(Intent.EXTRA_INTENT, i1);

                    Intent[] intentArray = { i2 };
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooser, 1); //ESTA DEPRECATED PERO FUNCIONA
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
                if(imageUri==null){
                    Bitmap foto = (Bitmap) data.getExtras().get("data");
                    ImageView fotoPerfil = (ImageView) findViewById(R.id.fotoDePerfilE);
                    fotoPerfil.setImageBitmap(foto);
                }else {
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ImageView fotoPerfil = (ImageView) findViewById(R.id.fotoDePerfilE);
                    fotoPerfil.setImageBitmap(selectedImage);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Perfil.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(Perfil.this,  getString(R.string.error),Toast.LENGTH_SHORT).show();
        }
    }

    //COMPACTAR IMAGEN
    protected byte[] tratarImagen(byte[] img){
        /**
         * Basado en el código extraído de Stack Overflow
         * Pregunta: https://stackoverflow.com/questions/57107489/sqliteblobtoobigexception-row-too-big-to-fit-into-cursorwindow-while-writing-to
         * Autor: https://stackoverflow.com/users/3694451/leo-vitor
         * Modificado por Ane García para traducir varios términos y adaptarlo a la aplicación
         */
        while(img.length > 50000){
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            Bitmap compacto = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.8), (int)(bitmap.getHeight()*0.8), true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            compacto.compress(Bitmap.CompressFormat.PNG, 100, stream);
            img = stream.toByteArray();
        }
        return img;
    }

    //MANTENER DATOS EN HORIZONTAL
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        EditText nombreE = (EditText) findViewById(R.id.nombreEEdit);
        String nombre = nombreE.getText().toString();
        savedInstanceState.putString("nombre", nombre);

        EditText apellidosE = (EditText) findViewById(R.id.apellidoEEdit);
        String apellidos = apellidosE.getText().toString();
        savedInstanceState.putString("apellidos", apellidos);

        EditText usuarioE = (EditText) findViewById(R.id.usuarioEEdit);
        String usuario = usuarioE.getText().toString();
        savedInstanceState.putString("usuario", usuario);

        EditText passwordE = (EditText) findViewById(R.id.contraEEdit);
        String password = passwordE.getText().toString();
        savedInstanceState.putString("password", password);

        EditText emailE = (EditText)  findViewById(R.id.emailEEdit);
        String email = emailE.getText().toString();
        savedInstanceState.putString("email", email);

        ImageView fotoPerfil = (ImageView) findViewById(R.id.fotoDePerfilE);
        Bitmap img = ((BitmapDrawable) fotoPerfil.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        //PARA QUE NO EXISTAN PROBLEMAS CON EL TAMAÑO DE LA IMAGEN
        b = tratarImagen(b);
        String temp = Base64.getEncoder().encodeToString(b);
        savedInstanceState.putString("img", temp);

    }

    //RECUPERAR LOS DATOS GUARDADOS
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String nombre = savedInstanceState.getString("nombre");
        EditText nombreE = (EditText) findViewById(R.id.nombreEEdit);
        nombreE.setText(nombre);

        String apellidos = savedInstanceState.getString("apellidos");
        EditText apellidosE = (EditText) findViewById(R.id.apellidoEEdit);
        apellidosE.setText(apellidos);

        String usuario = savedInstanceState.getString("usuario");
        EditText usuarioE = (EditText) findViewById(R.id.usuarioEEdit);
        usuarioE.setText(usuario);

        String password = savedInstanceState.getString("password");
        EditText passwordE = (EditText) findViewById(R.id.contraEEdit);
        passwordE.setText(password);

        String email = savedInstanceState.getString("email");
        EditText emailE = (EditText)  findViewById(R.id.emailEEdit);
        emailE.setText(email);

        String img = savedInstanceState.getString("img");
        byte [] encodeByte = Base64.getDecoder().decode(img);
        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        ImageView imgView=(ImageView) findViewById(R.id.fotoDePerfilE);
        imgView.setImageBitmap(bitmap);
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