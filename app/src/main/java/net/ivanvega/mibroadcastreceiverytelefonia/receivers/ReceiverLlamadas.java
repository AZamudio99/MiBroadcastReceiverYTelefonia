package net.ivanvega.mibroadcastreceiverytelefonia.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import net.ivanvega.mibroadcastreceiverytelefonia.Archivo;

public class ReceiverLlamadas extends BroadcastReceiver {
    private static final String TAG="Tel";
    String numero;
    String mensaje;
    String entrada;
    static boolean entradallamada = false;
    static String numeroEntrante;
    @Override
    public void onReceive(Context context, Intent intent) {
        numero="";
        mensaje="";
        TelephonyManager tm=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int estado, String NumeroLlamada){
                super.onCallStateChanged(estado, NumeroLlamada);
                if(estado==TelephonyManager.CALL_STATE_RINGING){
                    numeroEntrante = NumeroLlamada.substring(3);
                    entradallamada = true;
                }
                if(estado==TelephonyManager.CALL_STATE_IDLE){
                    if(entradallamada){
                        entradallamada = false;
                        entrada = Archivo.readFromFile(context).replace("\n","").replace("\r","");
                        if(entrada.contains("%!%")){
                            numero = entrada.split("%!%")[0];
                            mensaje = entrada.split("%!%")[1];
                        }
                        numero = numero.replace(" ","");
                        if(numero.equals(NumeroLlamada)){
                            Toast.makeText(context, "Son iguales", Toast.LENGTH_SHORT).show();
                            enviarSMS(NumeroLlamada, mensaje,context);
                        }
                    }
                }
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
    }
    private void enviarSMS(String tel, String msj,Context context) {
        SmsManager smsManager =  SmsManager.getDefault();
        smsManager.sendTextMessage(tel,null, msj, null, null);
        Toast.makeText(context, "Mensaje enviado", Toast.LENGTH_SHORT).show();
    }
}
