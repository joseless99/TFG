package com.example.tfg_app;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

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
 * @version 1.0
 */
public class System1Activity extends AppCompatActivity {
    //Variables para poder usar bluetooth
    BluetoothSocket blueSocket;//Socket para la comunicacion por bluetooth
    ConnectedThread coms = null;//Hilo de comunicaciones de bluetooth
    //Constantes necesarias
    private static final UUID bUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//UUID del Modulo bluetooth en android
    public static final String bMAC = "00:20:04:BD:D4:DE";//Identificador MAC del modulo HC-06 usado
    //Codigos de identificacion de permisos de bluetooth
    //private static final int BLUETOOTH_CONNECT_CODE = 100;
    //Botones usados en las vistas
    public Button bF, bB, bR, bL, bS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Carga del Layout de la actividad
        setContentView(R.layout.system_1_layout);

        // Adaptador bluetooth
        //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        //////////////////////////////////
        // Bloque de codigo a borrar
        // Desactivado ya que las pruebas necesarias se hacen antes en MainActivity
        //////////////////////////////

    /*
        //Verificamos que el dispositivo bluetooth es capaz de usar bluetooth
        if (bluetoothAdapter == null) {
            setContentView(R.layout.bt_non_existent_layout);
        } else {
            //en caso de disponer de bluetooth, verificamos si esta activado o no
            if (!bluetoothAdapter.isEnabled()) {//caso de no estar activado el bluetooth
                setContentView(R.layout.bt_disable_layout);//Cargamos una vista de error
                Button en = (Button) findViewById(R.id.actBlue);
                en.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Cambiamos el layout para evitar multiples activaciones de la accion
                        setContentView(R.layout.blank_layout);
                        restartApp();
                    }
                });
            } else {//Si el bluetooth esta activado

        */
           /////////////////////////
           ///FIn de Bloque de codigo a Borrar
           /////////////////////////




        //MEnsaje informativo para el usuario
        Toast.makeText(System1Activity.this,"Iniciando Comunicacion.Espere un poco",Toast.LENGTH_SHORT).show();

        //Iniciamos la conexion con el dispositivo bluetooth
        if (iniciarComBlue()) {//Conexion exitosa
            //Pequelo mensaje de confirmacion de conexion
            Toast.makeText(System1Activity.this, "Conexion establecida con el modulo bluetooth", Toast.LENGTH_SHORT) .show();

            //Sincronizamos los botones del layout con los definidos en System1Activity
            bF = findViewById(R.id.bForward);
            bB = findViewById(R.id.bBack);
            bR = findViewById(R.id.bRight);
            bL = findViewById(R.id.bLeft);
            bS = findViewById(R.id.bStop);
            //Añadimos funcionalidades para cada boton del layout, para cuando se pulsen

            //Boton de accion Avance(Forward) del vehiculo arduino
            bF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage("F");
                }
            });

            //Boton de accion Retroceso(Backwards) del vehiculo arduino
            bB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage("B");
                }
            });

            //Boton de accion giro Derecha(Right) del vehiculo arduino
            bR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage("R");
                }
            });

            //Boton de accion giro Izquierda(Left) del vehiculo arduino
            bL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage("L");
                }
            });

            //Boton de accion Detenerse(Stop) del vehiculo arduino
            bS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage("S");
                }
            });

            sendMessage("0");

        }else {//Comunicacion fallida
            Toast.makeText(System1Activity.this, "Error de conexion con el modulo bluetooth HC-06", Toast.LENGTH_SHORT) .show();
        }
    }




    ///////////////////
    /// Finales de linea del bloque de codigo comentado. Pendiente de borrar
    //////////////////
//        }
//    }




    /**
     * Funcion encargada para iniciar la comunicacion del dispositivo con el modulo bluetooth
     * Esta primero verifica que tenga los permisos para la comunicacion necesarios (BLUETOOTH_CONNECT)
     * En caso de no poseer estos se llama a la funcion requestPermission de ActivityCompat para que no los otorgue
     * Tras tener los permisos necesarios, la app establece la conexion con el modulo bluetooth especifico usado
     * La comunicacion sera exitosa si el led del modulo HC-06 deja de parpadear
     *
     * NOTA: Para que esta se lleve acabo correctamente antes hay que haber pareado el modulo con el movil a usar
     *
     * @return True si se ha llevado a cabo con exito la conexion con el modulo HC-06;
     * False si la conexion con el modulo HC-06 falla por alguna razon.
     */
    public boolean iniciarComBlue(){

        //Verificamos que tengamos los permisos necesarios autorizado
        //Para dispositivos con API>=31(Android 12 o superior) solicitamos permisos BLUETOOTH_CONNECT
        //Para dispositivos con API<31(Android 11 o previos) Solicitanos permisos BLUETOOTH
        if (Build.VERSION.SDK_INT >=31) {//API>=31
            //Verificamos que tengamos o no los permisos necesarios
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                //Solicitamos el permiso de BLUETOOTH_CONNECT al no tenerlo
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
            }
        }else{//API<31
            //Verificamos que tengamos o no los permisos necesarios
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                //Solicitamos el permiso de BLUETOOTH al no tenerlo
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 0);
            }
        }

        //Tras tener los permissos necesarios iniciamos la conexion con el modulo
        try {
            //Accededemos al Bluetooth del nuestro dispositivo
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            //Cargamos el dispositivo remoto al que deseamos conectarnos con bMAC
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bMAC);

            //Arbimos el puerto de comunicacion necesario con bUUID
            blueSocket = device.createRfcommSocketToServiceRecord(bUUID);

            //Establecemos conexion por el puerto de bluetooth
            blueSocket.connect();

            //Creamos e iniciamos el therad usado para la comunicacion bluetooth
            coms=new ConnectedThread(blueSocket);
            coms.start();
            //Retornamos exito de comunicacion
            return true;
        }catch (Exception e){//En caso de surgir un fallo inesperado durante la comunicacion
            return false;
        }
    }

    /**
     * Funcion encargada de resetear los colores de los botones del system_1_layout.xml al color inicial definicdo
     */
    private void resetColor(){
        //Variable que almacena el color defaul de los botones
        //Cambiar su valor en caso de cambiar el color default de los botones
        int defaultButtonColor= Color.parseColor("#0000FF");

        //Cambio del color de cada boton al definido previamente
        bF.setBackgroundColor(defaultButtonColor);
        bB.setBackgroundColor(defaultButtonColor);
        bS.setBackgroundColor(defaultButtonColor);
        bR.setBackgroundColor(defaultButtonColor);
        bL.setBackgroundColor(defaultButtonColor);

    }


    /**
     * Funcion encargada de enviar mensajes al vehiculo con el que se comunica
     * @param data: Informacion a enviar al receptor
     */
    private void sendMessage(String data){

        //Verificamos que aun estamos conectados al modulo bluetooth
        if (blueSocket.isConnected() && coms != null) {
            //Enviamos el comando de funcionamiento al modulo bluetooth
            coms.write(data.getBytes());
            //Actualizamos la UI para reflejar que fuel el ultimo que se pulso
            updateUI(data);
        } else {
            //Pequeño mensaje de error, en caso de haber un fallo de envio
            Toast.makeText(System1Activity.this, "Message could not be sent", Toast.LENGTH_SHORT).show();
        }
    }

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
     * Funcion encargada de cerrar la conexion con el modulo bluetooth receptor
     */
    private void finConexion(){
        //Comprobamos si hay abierta una conexion (cuando blueSocket no es null)
        if(blueSocket!=null)
            try {
                //Cerramos conexion
                blueSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        //Ponemos la variable blueSocket a null para garantizar que se acaba la conexion EN cualquier caso posible
        blueSocket=null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        sendMessage("1");
        finConexion();
    }

    //////////////////////////////////////////
    // Funcion a Borrar.
    // Desactivada debido a que ya no es Util
    //////////////////////////////////////////
    /**
     * Funcion que se encarga de reinicar la aplicacion.
     */
/*
    private void restartApp()  {
        //Verificamos si tenemos los permisos necesarios para habilitar el modulo bluetooth
        if (ActivityCompat.checkSelfPermission(System1Activity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
            //Solicitamos los permisos al sistema
            ActivityCompat.requestPermissions(System1Activity.this,new String[]{Manifest.permission.BLUETOOTH_CONNECT},0);
        }
        finConexion();//Aseguramos que cerramos el socket
        bluetoothAdapter.enable();//Activamos el bluetooth

        //Detenemos la app por 0.5segundos para garantizar el correcto inicio del bluetooth del movil
        try {
            //Mensaje informativo para el usuario
            Toast.makeText(System1Activity.this,"Reiniciando espere un poco",Toast.LENGTH_SHORT).show();
            //Delay aplicado a la app
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Creamos el Intent necesario para reinicar la app
        Intent a=new Intent(this, System1Activity.class);
        //Reiniciamos la app
        startActivity(a);
        //Teminamos con la previa activa
        finish();
    }
*/

}