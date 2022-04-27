package com.example.mosquittomqttbrokerandroidclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements MqttCallback, IMqttActionListener {

    private Button btnSendOn, btnSendOff;
    private TextView txtStatus;


    private static final String SERVER_URI = "tcp://192.168.0.11:1883";
    private static final String TOPIC = "/casa/foco";
    private static final int QOS = 1;
    private static final String TAG = "MainActivity";

    private MqttAndroidClient mqttAndroidClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSendOn = findViewById(R.id.btnSendOn);
        btnSendOff = findViewById(R.id.btnSendOff);
        txtStatus = findViewById(R.id.txtStatus);


        String clientId = UUID.randomUUID().toString();
        Log.d(TAG, "onCreate: clientId: " + clientId);

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), SERVER_URI, clientId);
        mqttAndroidClient.setCallback(this);

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);

        try {
            Log.d(TAG, "onCreate: Connecting to " + SERVER_URI);
            mqttAndroidClient.connect(mqttConnectOptions, null, this);
            txtStatus.setText("Conectado");
        } catch (MqttException ex){
            Log.e(TAG, "onCreate: ", ex);
            txtStatus.setText("No se pudo conectar.");
        }

        btnSendOn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Charset utf8 = Charset.forName("UTF-8");
                MqttMessage message = new MqttMessage( "ON".getBytes(utf8));
                message.setQos(QOS);
                try {
                    mqttAndroidClient.publish(TOPIC, message);

                    //mqttAndroidClient.disconnect();
                    Toast.makeText(v.getContext(), "Enviando ON...", Toast.LENGTH_SHORT).show();
                } catch (MqttException e) {
                    Log.e(TAG, "Error al publicar mensaje", e);
                    Toast.makeText(v.getContext(), "Error al publicar mensaje", Toast.LENGTH_LONG).show();
                }

            }
        });


        btnSendOff.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Charset utf8 = Charset.forName("UTF-8");
                MqttMessage message = new MqttMessage( "OFF".getBytes(utf8));
                message.setQos(QOS);
                try {
                    mqttAndroidClient.publish(TOPIC, message);

                    //mqttAndroidClient.disconnect();

                    Toast.makeText(v.getContext(), "Enviando OFF...", Toast.LENGTH_SHORT).show();
                } catch (MqttException e) {
                    Log.e(TAG, "Error al publicar mensaje", e);
                    Toast.makeText(v.getContext(), "Error al publicar mensaje", Toast.LENGTH_LONG).show();
                }

            }
        });



    }


        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.d(TAG, "onSuccess: ");
            try {
                mqttAndroidClient.subscribe(TOPIC, QOS);
                Toast.makeText(this, "Suscrito a "+ TOPIC + " correctamente.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(TAG, "Error subscribing to topic", e);
                Toast.makeText(this, "No se pudo suscribir a "+ TOPIC, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.e(TAG, "Failed to connect to: " + SERVER_URI, exception);
            txtStatus.setText("No se pudo conectar.");
        }

        @Override
        public void connectionLost(Throwable cause) {
            Log.d(TAG, "connectionLost: ", cause);
            txtStatus.setText("Conexión perdida.");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            Log.d(TAG, "Incoming message: " + new String(message.getPayload()));
            Toast.makeText(this, "Llego un mensaje: "+message.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.d(TAG, "deliveryComplete: ");
            Toast.makeText(this, "Mensaje Enviado.", Toast.LENGTH_LONG).show();
        }
}