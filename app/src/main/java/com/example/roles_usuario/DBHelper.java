package com.example.roles_usuario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    // Constructor
    public DBHelper(@Nullable Context context) {
        super(context, "listar.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Tabla roles
        db.execSQL("CREATE TABLE roles(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT UNIQUE NOT NULL," +
                "descripcion TEXT)");

        db.execSQL("CREATE TABLE usuarios(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "apellido TEXT," +
                "correo TEXT UNIQUE NOT NULL," +
                "contrasena TEXT NOT NULL," +
                "telefono TEXT," +
                "fecha_nac TEXT," +
                "verificado INTEGER DEFAULT 0," +
                "id_rol INTEGER," +
                "FOREIGN KEY (id_rol) REFERENCES roles(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS roles");
        onCreate(db);
    }

    // Metodo para insertar un rol de forma directa
    public boolean insertarRol(String nombre, String descripcion){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("descripcion", descripcion);

        long resultado = db.insert("roles", null, values);
        return resultado != -1;
    }

    //Metodo para insertar un rol usando un objeto Rol
    public boolean InsertarRoles(Rol rol){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nombre", rol.getNombre());
        values.put("descripcion", rol.getDescripcion());

        long resultado = db.insert("roles", null, values);
        return resultado != -1;
    }

    //Metodo para obtener todos los roles con manejo de cursores
    public ArrayList<Rol> obtenerRoles(){
        ArrayList<Rol> listaRoles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM roles", null);

        if(cursor.moveToFirst()){
            do{
                Rol rol = new Rol();
                rol.setId(cursor.getInt(0));
                rol.setNombre(cursor.getString(1));
                rol.setDescripcion(cursor.getString(2));

                listaRoles.add(rol);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return listaRoles;
    }

    //Metodo para eliminar un rol por su ID
     public boolean eliminarRol(int id) {
         SQLiteDatabase db = this.getWritableDatabase();
         int filasEliminadas = db.delete("roles", "id = ?", new String[]{String.valueOf(id)});
         db.close();
         return filasEliminadas > 0;
     }

     //Metodo para actualizar un rol
    public boolean actualizarRol(Rol rol){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", rol.getNombre());
        values.put("descripcion", rol.getDescripcion());

        int filaActualizadas = db.update("roles", values, "id = ?", new String[]{String.valueOf(rol.getId())});
        db.close();
        return filaActualizadas > 0;
    }


}
