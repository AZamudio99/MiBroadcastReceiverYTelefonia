package net.ivanvega.mibroadcastreceiverytelefonia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.ivanvega.mibroadcastreceiverytelefonia.receivers.MiReceiverTelefonia;
import net.ivanvega.mibroadcastreceiverytelefonia.receivers.MyBroadcastReceiver;

public class MainActivity extends AppCompatActivity {

    MyBroadcastReceiver myBroadcastReceiver=
            new MyBroadcastReceiver();
    MiReceiverTelefonia miReceiverTelefonia = new MiReceiverTelefonia();
    Button btnEnviar,btnConfigurar;
    TextView lbl;
    EditText txtTel, txtMessage;
    static String telefono="",mensaje="";
    TextView lslMensaje;
    TextView lslNumero;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lslMensaje=findViewById(R.id.lblMensaje);
        mensaje="";
        telefono="";
        btnEnviar = findViewById(R.id.btnSend);

        txtTel = findViewById(R.id.txtPhone);
        txtMessage = findViewById(R.id.txtTexto);
        lslNumero=findViewById(R.id.lblNum);
        btnConfigurar=findViewById(R.id.btnConfigurar);
        String archivo= Archivo.readFromFile(getApplicationContext());
        if(archivo.contains("%!%")){
            String resultado[]=
                    archivo.replace("\n","").replace("\r","").split("%!%");
            telefono=resultado[0];
            mensaje=resultado[1];
        }
        lslNumero.setText(telefono);
        lslMensaje.setText(mensaje);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent broadcast = new Intent();
                broadcast.setAction(getString(R.string.action_broadcast));
                broadcast.putExtra("key1", "parametro de la difusion");
                sendBroadcast(broadcast);
            }
        });
        btnConfigurar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String cadAgregar = txtTel.getText().toString() + "%!%" + txtMessage.getText().toString();
                Archivo.writeToFile(cadAgregar,getApplicationContext());
                lslNumero.setText(txtTel.getText());
                lslMensaje.setText(txtMessage.getText());
            }
        });
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(getString(R.string.action_broadcast));
        this.registerReceiver(myBroadcastReceiver, filter);
        IntentFilter intentFilterTel = new IntentFilter(Telephony.Sms .Intents.SMS_RECEIVED_ACTION);
        getApplicationContext().registerReceiver(miReceiverTelefonia,
                intentFilterTel
        );
    }
    private void enviarSMS(String tel, String msj) {
         SmsManager smsManager =  SmsManager.getDefault();
         smsManager.sendTextMessage(tel,null, msj,
         null, null);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(myBroadcastReceiver);
    }

    public void btnSMS_onclick(View v){
        enviarSMS(txtTel.getText().toString(), txtMessage.getText().toString());
    }

}