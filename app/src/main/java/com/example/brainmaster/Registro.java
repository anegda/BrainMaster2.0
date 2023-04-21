package com.example.brainmaster;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;

public class Registro extends AppCompatActivity {
    Calendar calendario = Calendar.getInstance();

    //FOTO DE PERFIL
    static String fotoDePerfil;
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
        setContentView(R.layout.activity_registro);

        //QUITAMOS LA ACTION BAR
        getSupportActionBar().hide();

        //DIALOGO PARA LA FECHA
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int anyo, int mes, int dia) {
                //AL ELEGIR LA FECHA Y PULSAR "ok" LA ESTABLECEMOS COMO TEXTO DEL EDITTEXT
                EditText fechaN = (EditText) findViewById(R.id.fechaNacREdit);
                //HAY QUE +1 AL MES, VA DE (0-11)
                fechaN.setText(Integer.toString(anyo) + "-" + Integer.toString(mes + 1) + "-" + Integer.toString(dia));
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
        fechaN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Registro.this, date, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //BOTÓN
        Button btn_login = (Button) findViewById(R.id.btn_reg);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
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
                EditText emailE = (EditText) findViewById(R.id.emailREdit);
                String email = emailE.getText().toString();
                EditText fechaNacE = (EditText) findViewById(R.id.fechaNacREdit);
                String fechaNac = fechaNacE.getText().toString();

                //COMPROBAMOS QUE TODOS LOS DATOS HAN SIDO INTRODUCIDOS
                if (nombre.equals("") || apellidos.equals("") || usuario.equals("") || password.equals("") || email.equals("") || fechaNac.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.errorCampos), Toast.LENGTH_SHORT).show();
                } else {
                    //SELECT EN BASE DE DATOS REMOTA PARA COMPROBAR SI EL USUARIO ESTÁ DISPONIBLE
                    Data datos0 = new Data.Builder()
                            .putInt("funcion",2)
                            .putString("usuario", usuario).build();
                    OneTimeWorkRequest otwr0 = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos0).build();
                    WorkManager.getInstance(Registro.this).getWorkInfoByIdLiveData(otwr0.getId()).observe(Registro.this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if(workInfo!=null && workInfo.getState().isFinished()){
                                Data outputData = workInfo.getOutputData();
                                if(outputData!=null){
                                    boolean existe = outputData.getBoolean("existe", false);
                                    if (existe) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.errorRegistro), Toast.LENGTH_SHORT).show();
                                    } else {
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
                                        img.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                        byte[] b = baos.toByteArray();
                                        //PARA QUE NO EXISTAN PROBLEMAS CON EL TAMAÑO DE LA IMAGEN
                                        b = tratarImagen(b);
                                        fotoDePerfil = Base64.getEncoder().encodeToString(b);

                                        //INSERT EN BD REMOTA
                                        Data datos = new Data.Builder()
                                                .putInt("funcion", 1)
                                                .putString("nombre", nombre)
                                                .putString("apellidos", apellidos)
                                                .putString("usuario", usuario)
                                                .putString("password", password)
                                                .putString("email", email)
                                                .putString("fechaNac", fechaNac)
                                                .build();
                                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(datos).build();
                                        WorkManager.getInstance(Registro.this).getWorkInfoByIdLiveData(otwr.getId()).observe(Registro.this, new Observer<WorkInfo>() {
                                            @Override
                                            public void onChanged(WorkInfo workInfo) {
                                                if (workInfo != null && workInfo.getState().isFinished()) {
                                                    Intent i = new Intent(Registro.this, Menu.class);
                                                    i.putExtra("usuario", usuario);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }
                                        });
                                        WorkManager.getInstance(Registro.this).enqueue(otwr);
                                    }
                                }
                            }
                        }
                    });
                    WorkManager.getInstance(Registro.this).enqueue(otwr0);
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
                    ImageView fotoPerfil = (ImageView) findViewById(R.id.fotoDePerfil);
                    fotoPerfil.setImageBitmap(foto);
                }else {
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ImageView fotoPerfil = (ImageView) findViewById(R.id.fotoDePerfil);
                    fotoPerfil.setImageBitmap(selectedImage);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Registro.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(Registro.this,  getString(R.string.error),Toast.LENGTH_SHORT).show();
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

    //VOLVEMOS A MAINACTIVITY SI PULSAMOS ATRAS
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}