package com.example.brainmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Registro extends AppCompatActivity {
    Calendar calendario = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int anyo, int mes, int dia) {

                EditText fechaN = (EditText) findViewById(R.id.fechaNacREdit);
                fechaN.setText(Integer.toString(anyo)+"/"+Integer.toString(mes)+"/"+Integer.toString(dia));
            }
        };

        EditText fechaN = (EditText) findViewById(R.id.fechaNacREdit);
        fechaN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Registro.this,date,calendario.get(Calendar.YEAR),calendario.get(Calendar.MONTH),calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}