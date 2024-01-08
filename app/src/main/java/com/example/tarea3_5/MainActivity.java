package com.example.tarea3_5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 123;
    private Button botonAnadir,botonMostrar,botonEliminar;
    private TextView tvSalida;
    private Uri uri = CallLog.Calls.CONTENT_URI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonAnadir = findViewById(R.id.btAnadir);
        botonMostrar = findViewById(R.id.btMostrar);
        botonEliminar = findViewById(R.id.btBorrar);
        tvSalida = findViewById(R.id.tvSalida);

        botonAnadir.setOnClickListener(view -> {
            metodoAnadir();
        });

        botonMostrar.setOnClickListener(view -> {
            metodoMostrar();
        });

        botonEliminar.setOnClickListener(view -> {
            metodoEliminar();
        });


    }

    private void metodoAnadir(){
        Date ahora = new Date();
        Random r;
        r = new Random();
        int dia = 24 * 60 * 60 * 1000; //milisegundos de un día
        ContentValues valores = new ContentValues();
        valores.put(CallLog.Calls.DATE, ahora.getTime() - r.nextInt(dia)); //Valor de tiempo aleatorio. Desde ahora hasta hace un día.
        valores.put(CallLog.Calls.NUMBER, r.nextInt(1000000) + 555000000); //Número de teléfono aleatorio con pref. 555
        valores.put(CallLog.Calls.DURATION, r.nextInt(300)); //Duración aleatoria hasta 5 minutos
        valores.put(CallLog.Calls.TYPE, r.nextInt(3) + 1); //Tipo aleatorio: entrante, saliente o perdida
        getContentResolver().insert(uri, valores);
        Toast.makeText(this, "Se ha agregado un nuevo registro de llamada", Toast.LENGTH_SHORT).show();
    }

    private void metodoMostrar(){
        //Preparamos el cursor
        String[] proyeccion = new String[]{CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.NUMBER, CallLog.Calls.TYPE};
        String seleccion = "type in (?, ?, ?)";
        String [] seleccionArgs = new String[]{"1", "2", "3"}; //Seleccionamos llamadas entrantes, salientes y perdidas.
        String ordenado = CallLog.Calls.DATE + " DESC";
        Cursor cursor = getContentResolver().query(uri,proyeccion,seleccion,seleccionArgs,ordenado);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Obtén datos de la llamada desde el cursor
                @SuppressLint("Range") long fechaC = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                Date fecha = new Date(fechaC);
                @SuppressLint("Range") String numero = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                @SuppressLint("Range") int duracion = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
                @SuppressLint("Range") String tipo = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                // Formatea los datos y añádelos al TextView
                String detalleLlamada = fecha + " " + numero + " " + duracion + " " + tipo + "\n";
                tvSalida.append(detalleLlamada);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }


    private void metodoEliminar(){
        String seleccion = "type in (?, ?, ?)";
        String [] seleccionArgs = new String[]{"1", "2", "3"}; //Seleccionamos llamadas entrantes, salientes y perdidas.
        getContentResolver().delete(uri,seleccion,seleccionArgs);
        Toast.makeText(this, "Se ha eliminador un registro", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

            if (checkPermissions()) {
                // Los permisos ya están concedidos, puedes realizar acciones que requieren permisos.
            } else {
                requestPermissions();
            }

    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG},
                REQUEST_CODE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // El usuario no concedió los permisos, muestra un mensaje y finaliza la aplicación.
                showToastAndFinish();
            }
        }
    }

    private void showToastAndFinish() {
        Toast.makeText(this, "Hasta que no se den permisos, no se podrá leer/escribir en el proveedor de contenidos", Toast.LENGTH_SHORT).show();
        finish();
    }


}