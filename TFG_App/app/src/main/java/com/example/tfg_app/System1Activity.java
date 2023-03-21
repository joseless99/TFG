package com.example.tfg_app;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

//TODO: Incluir una seccion de UI de la actividad donde se pueda recibir datos enviados al
// InputStream de la comunicacion bluetooth. Este principalmente recibirá la distancia del vehiculo
// con un posible obstaculo que tenga en frente

//TODO: Crear un metodo que permita obtener el mensaje que haya sido enviado a InputStream

//TODO: revisar documentacion del codigo para actualizar con respecto a los combios

/**
 * Actividad de ejemplo de la aplicacion Android desarrollada para el TFG
 *
 * Esta Actividad se encarga de establecer una comunicacion con un modulo bluetooth HC-06, el cual
 * estara conectado a un Arduino Leonardo. El sistema que este representa es un coche electronico
 * montado sobre una placa arduino como unidad de control.
 *
 * Cuando esta se inicia, se establece una comunicacion con el vehiculo, con el fin de poder
 * mandarle comandos para controlarlo de forma remota.
 *
 * En caso de haber un fallo durante el proceso de conexion con el modulo HC-06 se presentara un pequeño
 * mensaje al usuario de fallo de inicio de esta conexion.
 *
 * @author Juan Jose Ropero Cerro (i82rocej)
 * @version 1.1
 */
public class System1Activity extends AppCompatActivity {
    //Variables para poder usar bluetooth
    private BluetoothSocket bSocket = null;//Socket para la comunicacion por bluetooth
    private BluetoothDevice bDevice = null;
    private BluetoothAdapter bAdapter = null;
    private OutputStream bOutput=null;
    private InputStream bInput=null;
    private ConectionThread C;
    private String state="Off";//Variable para controlar el InputStream del Bluetooth
    //Constantes necesarias
    private static final UUID bUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//UUID del Modulo bluetooth en android
    public static final String bMAC = "00:20:04:BD:D4:DE";//Identificador MAC del modulo HC-06 usado

    //Botones usados en las vistas
    public Button bF, bB, bR, bL, bS;
    public TextView txt;
    public ImageButton bC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Carga del Layout de la actividad
        setContentView(R.layout.system_1_layout);


        //MEnsaje informativo para el usuario
        Toast.makeText(System1Activity.this, "Iniciando Comunicacion.Espere un poco", Toast.LENGTH_SHORT).show();


        //Sincronizamos los elementos del layout con los definidos en System1Activity
        bF = findViewById(R.id.bForward);
        bB = findViewById(R.id.bBack);
        bR = findViewById(R.id.bRight);
        bL = findViewById(R.id.bLeft);
        bS = findViewById(R.id.bStop);
        bC=findViewById(R.id.bConexion);
        txt=findViewById(R.id.data);

        //Tratamos de iniciar la comunicacion con el modulo bluetooth esclavo, asignado a
        //esta actividad. Este cargará de forma paralela a la carga de la interfaz principal

        C=new ConectionThread();
        C.setAppCompatActivity(this);
        C.setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
        C.setImageButton(bC);
        C.start();

        //Añadimos funcionalidades para cada boton del layout, para cuando se pulsen
        //Boton de accion Avance(Forward) del vehiculo arduino
        bF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res=C.enviarComando("F");
                if(res==0){
                    updateUI("F");
                }else if(res==1){
                    Toast.makeText(System1Activity.this, "El mensaje no pudo ser enviado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(System1Activity.this, "La conexion esta cerrada,y no se pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton de accion Retroceso(Backwards) del vehiculo arduino
        bB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res=C.enviarComando("B");
                if(res==0){
                    updateUI("B");
                }else if(res==1){
                    Toast.makeText(System1Activity.this, "El mensaje no pudo ser enviado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(System1Activity.this, "La conexion esta cerrada,y no se pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton de accion giro Derecha(Right) del vehiculo arduino
        bR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res=C.enviarComando("R");
                if(res==0){
                    updateUI("R");
                }else if(res==1){
                    Toast.makeText(System1Activity.this, "El mensaje no pudo ser enviado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(System1Activity.this, "La conexion esta cerrada,y no se pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton de accion giro Izquierda(Left) del vehiculo arduino
        bL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res=C.enviarComando("L");
                if(res==0){
                    updateUI("L");
                }else if(res==1){
                    Toast.makeText(System1Activity.this, "El mensaje no pudo ser enviado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(System1Activity.this, "La conexion esta cerrada,y no se pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton de accion Detenerse(Stop) del vehiculo arduino
        bS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res=C.enviarComando("S");
                if(res==0){
                    updateUI("S");
                }else if(res==1){
                    Toast.makeText(System1Activity.this, "El mensaje no pudo ser enviado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(System1Activity.this, "La conexion esta cerrada,y no se pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * Funcion encargada de resetear los colores de los botones del system_1_layout.xml al color inicial definicdo
     */
    private void resetColor(){
        //Variable que almacena el color por defecto de los botones decidido
        //Cambiar su valor en caso de cambiar el color por defecto de los botones
        int defaultButtonColor= Color.parseColor("#0000FF");

        //Cambio del color de cada boton al valor por defecto.
        //Aqui no se incluye el icono que refleja el estado de conexion bluetooth
        bF.setBackgroundColor(defaultButtonColor);
        bB.setBackgroundColor(defaultButtonColor);
        bS.setBackgroundColor(defaultButtonColor);
        bR.setBackgroundColor(defaultButtonColor);
        bL.setBackgroundColor(defaultButtonColor);

    }
//
//    public String leerdatos() throws IOException {
//
//        BufferedReader r = new BufferedReader(new InputStreamReader(getInputStream()));
//        String data=r.readLine();
//        return data;
//    }
    /**
     * Funcion que actualiza los colores de los botones en funcion de aquel que se haya pulsado.
     *
     *  Esta primero llama a resetColor() para reestablecer los botones al color original(Azul)
     *  Despues cambia el ultimo boton pulsado a color verde para relfejar cual fue activado
     *
     * @param data Valor para saber que boton hay que cambiar a verde
     */
    private void updateUI(String data){
        //LLamada a la funcion resetClor()
        resetColor();

        //Decidimos que boton hemos de actualizar a verde
        if(data.equals("F")){
            bF.setBackgroundColor(Color.GREEN);
        }else if(data.equals("B")){
            bB.setBackgroundColor(Color.GREEN);
        }else if(data.equals("R")){
            bR.setBackgroundColor(Color.GREEN);
        }else if(data.equals("L")){
            bL.setBackgroundColor(Color.GREEN);
        }else if(data.equals("S")){
            bS.setBackgroundColor(Color.GREEN);
        }
    }


    /**
     * Metodo que se activa cuando se cierra esta actividad. Finaliza la comunicaciones abiertos y
     * elimina el Thread usado
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        C.destroyClass();
    }
}