package miguelangellopez.ad.practica1_psp_ad;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import miguelangellopez.ad.practica1_psp_ad.Receptores.IncomingCallsReceiver;
import miguelangellopez.ad.practica1_psp_ad.Settings.SettingsActivity;

import static android.Manifest.permission.PROCESS_OUTGOING_CALLS;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static miguelangellopez.ad.practica1_psp_ad.Receptores.IncomingCallsReceiver.LISTA_LLAMADAS;
import static miguelangellopez.ad.practica1_psp_ad.Receptores.IncomingCallsReceiver.call;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener  {


    private static final int PERMISO_CONTACTOS = 1;
    private static final String TAG = MainActivity.class.getName() + "xyzyx";
    private static final int PERMISO = 1;

    private static final int PERMISSION_CODE = 123;

    private SharedPreferences sharedPreferences;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public static TextView tvLlamadas;

    public static ArrayList<Contactos> contactList = new ArrayList<>();

    String[] data = new String[]{ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
    String order = ContactsContract.Data.DISPLAY_NAME + " ASC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        tvLlamadas = findViewById(R.id.tvLlamadas);

        if(checkPermission()){
            getContacts();
        }else {
            requestPermission();
        }

        listener = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        onSharedPreferenceChanged(sharedPreferences, "Memoria Interna");

    }


    //----- PEDIR PERMISOS -----
    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        READ_CALL_LOG,
                        READ_PHONE_STATE,
                        READ_CONTACTS
                }, PERMISSION_CODE);
    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_CODE:
                if (grantResults.length > 0) {

                    boolean RealCallLogPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadPhoneStatePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadContacts = grantResults[2] == PackageManager.PERMISSION_GRANTED;



                    if (RealCallLogPermission && ReadPhoneStatePermission && ReadContacts) {
                        Toast.makeText(this, "Los permisos estan concedidos", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }

    //Me comprueba que tengo todos los permisos, a la hora de realizar una acción
    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CALL_LOG);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int thirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                thirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    //----- INICIALIZAR PANTALLA DE AJUSTES -----
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menú se integra en el ActionBar
        //ciclo de vida, ejecutar cuando quiera abrir el menú
        //inflator especial, los menús se inflan de forma diferente
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menu, menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //ciclo de vida de la actividad
        int id = item.getItemId();
        switch(id) {
            case R.id.mnSettings:
                return viewSettingsActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean viewSettingsActivity() {
        //intención
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(sharedPreferences.getString("memoriaInterna",s).equals("Memoria Interna")){
            readCallInternal();
        }else if(sharedPreferences.getString("memoriaExterna",s).equals("Memoria Externa")){
            readCallExternal();
        }


    }

    //----- LEER LOS DATOS DE LOS ARCHIVOS -----
    private void readCallInternal() {
        File f = new File(getFilesDir(),"historial.csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea;
            StringBuilder texto = new StringBuilder();
            while ((linea = br.readLine()) != null) {
                texto.append(linea);
                texto.append('\n');
            }
            tvLlamadas.setText(texto);
            br.close();
        } catch(IOException e) {
        }
    }

    private void readCallExternal() {
        File f = new File(getExternalFilesDir(null),"llamadas.csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea;
            StringBuilder texto = new StringBuilder();
            while ((linea = br.readLine()) != null) {
                texto.append(linea);
                texto.append("\n");
            }
            tvLlamadas.setText(texto);
            br.close();
        } catch(IOException e) {
        }
    }

    //----- OBTENER LISTA DE CONTACTOS DE LA AGENDA -----
    public void getContacts(){
        //ContentResolver contentResolver = getContentResolver();
        Cursor cursor =  getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                data,
                null,
                null,
                order);

        while(cursor.moveToNext()){
            String name = cursor.getString(0);
            String number = cursor.getString(1);

            Contactos contact = new Contactos(name, number);
            contactList.add(contact);
            Log.v("xyzyx", contact.toString());

        }
        cursor.close();
    }


}