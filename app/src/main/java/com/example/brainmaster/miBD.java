package com.example.brainmaster;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class miBD extends SQLiteOpenHelper {
    public miBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE Usuarios ('Codigo' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'nombre' VARCHAR(255), 'apellidos' VARCHAR(255), 'usuario' VARCHAR(255), 'password' VARCHAR(255), 'email' VARCHAR(255), 'fechaNac' DATE)");
        sqLiteDatabase.execSQL("CREATE TABLE Partidas ('CodigoPartida' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'usuario' VARCHAR(255), 'puntos' INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
