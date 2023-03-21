package com.example.tfg_app;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.UUID;

public class ConectionThread extends Thread{
    private BluetoothSocket bSocket = null;//Socket para la comunicacion por bluetooth
    private BluetoothDevice bDevice = null;
    private BluetoothAdapter bAdapter = null;
    private OutputStream bOutput=null;
    private InputStream bInput=null;
    private AppCompatActivity actividadOrigen=null;
    private ImageButton bC=null;

    //Constantes necesarias
    private static final UUID bUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//UUID del Modulo bluetooth en android
    public static final String bMAC = "00:20:04:BD:D4:DE";//Identificador MAC del modulo HC-06 usado

    public ConectionThread(){
         BluetoothSocket bSocket = null;//Socket para la comunicacion por bluetooth
         BluetoothDevice bDevice = null;
         BluetoothAdapter bAdapter = null;
         OutputStream bOutput=null;
         InputStream bInput=null;
         AppCompatActivity actividadOrigen=null;
         ImageButton bC=null;
    }


    public void setBluetoothAdapter(BluetoothAdapter adapter) {
        if (adapter == null) {
            bAdapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            this.bAdapter = adapter;
        }
    }
    public void setBluetoothDevice(BluetoothDevice device) {
        this.bDevice = device;
    }
    public void setBluetoothSocket(BluetoothSocket socket) {
        this.bSocket=socket;
    }
    public void setOutputStream(OutputStream stream) {
        this.bOutput=stream;
    }
    public void setInputStream(InputStream stream){
        this.bInput=stream;
    }
    public void setImageButton(ImageButton button){
        this.bC=button;
    }
    public void setAppCompatActivity(AppCompatActivity actividad) { this.actividadOrigen=actividad; }

    public BluetoothAdapter getBluetoothAdapter(){
        return this.bAdapter;
    }
    public BluetoothSocket getBluetoothSocket(){
        return this.bSocket;
    }
    public BluetoothDevice getBluetoothDevice(){
        return this.bDevice;
    }
    public OutputStream getOutputStream() {
        return this.bOutput;
    }
    public InputStream getInputStream(){
        return this.bInput;
    }

    public AppCompatActivity getActividadOrigen() {return this.actividadOrigen;}

    /**
     * Funcion encargada para iniciar la comunicacion del dispositivo con el modulo bluetooth
     * Esta primero verifica que tenga los permisos para la comunicacion necesarios (BLUETOOTH_CONNECT)
     * En caso de no poseer estos se llama a la funcion requestPermission de ActivityCompat para que no los otorgue
     * Tras tener los permisos necesarios, la app establece la conexion con el modulo bluetooth especifico usado
     * La comunicacion sera exitosa si el led del modulo HC-06 deja de parpadear
     *
     * NOTA: Para que esta se lleve acabo correctamente antes hay que haber pareado el modulo con el movil a usar
     *
     */
    @Override
    public void run(){

        //Verificamos que tengamos los permisos necesarios autorizado
        //Para dispositivos con API>=31(Android 12 o superior) solicitamos permisos BLUETOOTH_CONNECT
        //Para dispositivos con API<31(Android 11 o previos) Solicitanos permisos BLUETOOTH
        if (Build.VERSION.SDK_INT >=31) {//API>=31
            //Verificamos que tengamos o no los permisos necesarios
            if (ActivityCompat.checkSelfPermission(actividadOrigen.getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                //Solicitamos el permiso de BLUETOOTH_CONNECT al no tenerlo
                ActivityCompat.requestPermissions(getActividadOrigen(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
            }
        }else{//API<31
            //Verificamos que tengamos o no los permisos necesarios
            if (ActivityCompat.checkSelfPermission(actividadOrigen.getApplicationContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                //Solicitamos el permiso de BLUETOOTH al no tenerlo
                ActivityCompat.requestPermissions(getActividadOrigen(), new String[]{Manifest.permission.BLUETOOTH}, 0);
            }
        }

        //Tras tener los permissos necesarios iniciamos la conexion con el modulo
        try {
            //Accededemos al Bluetooth del nuestro dispositivo
            setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());

            //Cargamos el dispositivo remoto al que deseamos conectarnos con bMAC
            setBluetoothDevice(this.bAdapter.getRemoteDevice(bMAC));

            //Establecemos el puerto de comunicacion necesario con bUUID
            setBluetoothSocket(this.bDevice.createRfcommSocketToServiceRecord(bUUID));

            //Abrimos la conexion por el puerto con el dispositivo esclavo de bluetooth
            bSocket.connect();

            //Enlazamos la via de conexion de datos entre los 2 dispositivos
            setOutputStream(getBluetoothSocket().getOutputStream());
            setInputStream(getBluetoothSocket().getInputStream());

            //Enviamos mensaje al arduino para que este empieze a funcionar y actualizamos la variable de estado de conexion a On
            bC.setBackgroundColor(Color.GREEN);
            enviarComando("0");
            //state="On";

            //Funcionalidad añadida para indicar al usuario que la conexion esta activa
            bC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActividadOrigen(),"La conexion esta activa y funcionando",Toast.LENGTH_SHORT).show();
                }
            });

//            //INPUTSTREAM
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while(state=="On"){
//                        try {
//                            txt.setText(leerdatos());
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }
//            }).start();

        }catch (Exception e){//En caso de surgir un fallo inesperado durante la comunicacion

            //Actualizamos el color del Boton y la variable de estado de control
            bC.setBackgroundColor(Color.RED);
            //state="Off";

           // Funcionalidad de reintento de conexion con el modulo bluetooth en caso de fallos
            bC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //MEnsaje de informacion para el usuario
                    Toast.makeText(getActividadOrigen(),"Reintentando conexion",Toast.LENGTH_SHORT).show();

                    //Indicamos que al reintentar la conexion lo haga en un nuevo Thread,
                    //en caso de que esta vuelva a fallar
//                    new Thread(new Runnable() {
//
//                    }).start();
                }
            });
        }
    }

    /**
     * Funcion encargada de enviar mensajes al vehiculo con el que se comunica
     * @param data: Informacion a enviar al receptor
     */
    public int enviarComando(String data){

        //Verificamos que aun estamos conectados al modulo bluetooth
        if (getBluetoothSocket().isConnected()) {
            //Conevrtimos el comando a tipo byte[]
            byte[] comando=data.getBytes();

            //Enviamos el comando de funcionamiento al modulo bluetooth
            try {
                this.bOutput.write(comando);
                return 0;
            } catch (IOException e) {
                //Hubo algun error al enviar un mensaje
                e.printStackTrace();
                return 1;
            }
        } else {
            //Pequeño mensaje de error, en caso de estar la conexion cerrada
            return -1;
        }
    }

    public void destroyClass(){
        if(getBluetoothSocket().isConnected()) {
            //state="Off";
            //Enviamos un comando al arduino para que comprenda que se ha acabado la conexion
            enviarComando("1");
            try {
                getOutputStream().close();
                getInputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Cerramos la conexion con el arduino
            try {
                bSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Ponemos bSocket, bInput y bOutput a null para acabar con el cerrado de conexion
            this.bSocket=null;
            this.bInput=null;
            this.bOutput=null;
        }
    }

}
