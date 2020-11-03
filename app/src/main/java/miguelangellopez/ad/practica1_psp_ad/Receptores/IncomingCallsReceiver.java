package miguelangellopez.ad.practica1_psp_ad.Receptores;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import miguelangellopez.ad.practica1_psp_ad.Contactos;
import miguelangellopez.ad.practica1_psp_ad.Llamadas;
import miguelangellopez.ad.practica1_psp_ad.MainActivity;
import miguelangellopez.ad.practica1_psp_ad.R;
import miguelangellopez.ad.practica1_psp_ad.util.ContactComparator;

import static android.content.Context.*;
import static miguelangellopez.ad.practica1_psp_ad.MainActivity.contactList;
import static miguelangellopez.ad.practica1_psp_ad.MainActivity.tvLlamadas;

public class IncomingCallsReceiver extends BroadcastReceiver {

    MainActivity principal = new MainActivity();

    public static Llamadas call;

    public static ArrayList<Llamadas> LISTA_LLAMADAS = new ArrayList<>();

    String formato = "yyyy; MM; dd; HH; mm; ss";
    Calendar fecha = Calendar.getInstance ();
    SimpleDateFormat df = new SimpleDateFormat(formato);
    String rightNow = df.format(fecha.getTime());


    Context context;
    String incomingNumber;
    String name = "Desconocido";
    String memoriaInterna;
    String memoriaExterna;

    @Override
    public void onReceive(Context context, Intent intent) {
        ContentResolver resolver = context.getContentResolver();

        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
             incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);


            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(incomingNumber));

            Cursor cursor = resolver.query(lookupUri, new String[]{
                    ContactsContract.PhoneLookup.STARRED}, null, null, null);

            Thread hebra = new Thread() {
                @Override
                public void run() {
                    cogerNombre();
                    memoriaInterna = rightNow + "; " + incomingNumber + "; " + name;
                    memoriaExterna = name + "; " + rightNow + "; " + incomingNumber;
                    writeCallExternal(memoriaExterna, context);
                    writeCallInternal(memoriaInterna, context);

                }
            };

            hebra.start();

            Toast.makeText(context, "Llamada desde: "+incomingNumber + " " + rightNow, Toast.LENGTH_LONG).show();

        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE) ||
                intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            Toast.makeText(context, "Se ha colgado la llamada",Toast.LENGTH_LONG).show();
        }
    }

    private void cogerNombre(){
        for (int i = 0; i < contactList.size(); i++) {
            if(contactList.get(i).getNumber().equals(incomingNumber)){
                name = contactList.get(i).getName();
            }
        }
    }


    //ESCRIBIR EN UN ARCHIVO DE TEXTO, DE LA MEMORIA EXTERNA
    private boolean writeCallExternal(String calls, Context context){
        boolean result = true;
        File f = new File(context.getExternalFilesDir(null), "llamadas.csv");
        FileWriter fw = null;
        try{
            fw = new FileWriter(f, true);
            fw.write(calls + "\n");
            fw.flush();
            fw.close();
        }catch (IOException e){
            result = false;
        }
        return  result;
    }

    private void readCallExternal() {
        File f = new File(context.getExternalFilesDir(null),"llamadas.csv");
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

    //ESCRIBIR EN UN ARCHIVO DE TEXTO DE LA MEMORIA INTERNA
    private boolean writeCallInternal(String calls, Context context){
        boolean result = true;
        File f = new File(context.getFilesDir(),"historial.csv");
        FileWriter fw= null;
        try {
            fw = new FileWriter(f, true);
            fw.write(calls + "\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            result = false;
        }
        return  result;

    }

    private void readCallInternal() {
        File f = new File(context.getFilesDir(),"historial.csv");
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

}


