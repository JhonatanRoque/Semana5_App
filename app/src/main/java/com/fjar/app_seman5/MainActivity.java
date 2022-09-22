package com.fjar.app_seman5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private EditText edtcod, edtDesc, edtPrec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtcod = (EditText) findViewById(R.id.edtCod);
        edtDesc = (EditText) findViewById(R.id.edtDesc);
        edtPrec = (EditText) findViewById(R.id.edtPrecio);

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso para leer y escribir!");
        }

    }
    //Método para registrar datos a la base de datos
    public void alta (View v){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        String cod = edtcod.getText().toString();
        String descripcion = edtDesc.getText().toString();
        String precio = edtPrec.getText().toString();
        ContentValues registro = new ContentValues();
        registro.put("codigo", cod);
        registro.put("descripcion", descripcion);
        registro.put("precio", precio);
        db.insert("articulos", null, registro);
        db.close();
        edtcod.setText("");
        edtDesc.setText("");
        edtPrec.setText("");
        Toast.makeText(this, "Se cargarón los datos del artículo", Toast.LENGTH_SHORT).show();
    }
    //Método para consultar producto mendiante su código
    public void consultarProdCod (View v){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        String cod = edtcod.getText().toString();
        Cursor fila = db.rawQuery(
                "Select descripcion, precio from articulos where codigo == " + cod, null
        );
        if (fila.moveToFirst()){
            edtDesc.setText(fila.getString(0));
            edtPrec.setText(fila.getString(1));
        }else {
            Toast.makeText(this, "No existe un artículo con dicho código ", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
    //Método para consultar producto mediante su descripción
    public void consultarProdDesc (View v){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        String descripcion = edtDesc.getText().toString();
        Cursor fila = db.rawQuery(
                "Select codigo, precio from articulos where descripcion == '" + descripcion + "'" , null);
        if (fila.moveToFirst()){
            edtcod.setText(fila.getString(0));
            edtPrec.setText(fila.getString(1));
        }else{
            Toast.makeText(this, "No existe un artículo con dicha descripción", Toast.LENGTH_SHORT).show();
        }
        db.close();


    }
    //Método para eliminar por código
    public void eliminarCod (View v){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase db  = admin.getWritableDatabase();
        String cod = edtcod.getText().toString();
        int cant = db.delete("articulos", "codigo = " + cod, null);
        db.close();
        edtcod.setText("");
        edtDesc.setText("");
        edtPrec.setText("");
        if (cant == 1){
            Toast.makeText(this, "Se borró el articulo con dicho código", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "No existe un artículo con dicho código", Toast.LENGTH_SHORT).show();
        }
    }

    //Método para la modificación de un producto
    public void modificacion (View v){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        String cod = edtcod.getText().toString();
        String desc = edtDesc.getText().toString();
        String precio = edtPrec.getText().toString();
        ContentValues registro = new ContentValues();
        registro.put("codigo", cod);
        registro.put("descripcion", desc);
        registro.put("precio", precio);
        int cant = db.update("articulos", registro, "codigo = " + cod, null);
        if (cant == 1){
            Toast.makeText(this, "Se modificarón los datos", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "No existe un artículo con el código ingresado", Toast.LENGTH_SHORT).show();
        }
    }

    public void CopiaBD(View v){
        try{
            final String inFileName = "/data/data/com.fjar.app_seman5/databases/administracion";


            File dbFile = new File(inFileName);
            if(dbFile.exists()){
                Toast.makeText(this, "Existe la base de datos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No existe la base de datos", Toast.LENGTH_SHORT).show();
            }
            FileInputStream fis = new FileInputStream(dbFile);
            File directorio = new File(Environment.getExternalStorageDirectory() + "/CopiasDB");
            if(!directorio.exists()) {
                if (directorio.mkdirs()) {
                    Toast.makeText(this, "Se creo el directorio", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "No se creo el directorio", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Ya existe el direcotorio", Toast.LENGTH_SHORT).show();
            }
            String outFileName = Environment.getExternalStorageDirectory() + "/CopiasDB/administra_copy.db";
            File files = new File(outFileName);
            if(!files.exists()){
                files.createNewFile();
                if(files.exists()){
                    Toast.makeText(this, "El archivo copia existe", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(this, "Exste la base de datos ", Toast.LENGTH_SHORT).show();
            }
            // Open the empty db as the output stream
            FileOutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[(int) dbFile.length()];

             int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }
            // Close the streams
            output.flush();
            output.close();
            fis.close();


        }catch(Exception e){
            Log.e("error", e.toString());
        }

    }
}