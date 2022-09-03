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
 * Clase principal de la aplicacion Android desarrollada para el TFG
 *
 * Esta aplicacion se encarga de establecer una comunicacion con un modulo bluetooth HC-06, el cual estara conectado
 * a un Arduino Leonardo. Tras establecerse la conexion con el modulo se carga el layout principal (main_activity_layout.xml)
 * el cual contiene ciertos botones los cuales al pulsarse envian un mensaje al modulo HC-06, y el arduino los
 * interpreta como una orden, cambiando el estado de ciertos pines del mismo.
 *
 * En caso de haber un fallo durante el proceso de conexion con el modulo HC-06 se cargara un layout de error determinado
 * y se presentara un pequeño mensaje al usuario de fallo de inicio de esta conexion.
 *
 * @author Juan Jose Ropero Cerro (i82rocej)
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    //Variables para poder usar bluetooth
    BluetoothAdapter bluetoothAdapter;//Variable para identificar el modulo bluetooth del dispositivo android
    BluetoothSocket blueSocket;//Socket para la comunicacion por bluetooth
    BluetoothDevice blueDevice;//Variable para el dispisitivo bluetooth
    ConnectedThread btt = null;//Hilo de comunicaciones de bluetooth
    //Constantes necesarias
    private static final UUID blueUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//UUID del Modulo bluetooth en android
    public static final String blueMac = "00:20:04:BD:D4:DE";//Identificador MAC del modulo HC-06 usado
    //Codigos de identificacion de permisos de bluetooth
    //private static final int BLUETOOTH_CONNECT_CODE = 100;
    //Botones usados en las vistas
    public Button bF, bB, bR, bL, bS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);
        // Adaptador bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
                //MEnsaje informativo para el usuario
                Toast.makeText(MainActivity.this,"Iniciando Comunicacion.Espere un poco",Toast.LENGTH_SHORT).show();
                //Iniciamos la conexion con el dispositivo bluetooth
                if (iniciarComBlue()) {//Conexion exitosa
                    //Pequelo mensaje de confirmacion de conexion
                    Toast.makeText(MainActivity.this, "Conexion establecida con el modulo bluetooth", Toast.LENGTH_SHORT) .show();

                    //Cargamos la vista principal de la app
                    setContentView(R.layout.main_activity_layout);

                    //Sincronizamos los botones del layout con los definidos en MainActivity
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
                    Toast.makeText(MainActivity.this, "Error de conexion con el modulo bluetooth HC-06", Toast.LENGTH_SHORT) .show();
                }
            }
        }
    }

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
            //Establecemos la UUID y la direccion MAC de nuestro modulo
            blueDevice = bluetoothAdapter.getRemoteDevice(blueMac);
            blueSocket = blueDevice.createRfcommSocketToServiceRecord(blueUUID);

            //Establecemos conexion por el puerto de bluetooth
            blueSocket.connect();

            //Creamos e iniciamos el therad usado para la comunicacion bluetooth
            btt=new ConnectedThread(blueSocket);
            btt.start();
            //Retornamos exito de comunicacion
            return true;
        }catch (Exception e){//En caso de surgir un fallo inesperado durante la comunicacion
            return false;
        }
    }

    /**
     * Funcion encargada de resetear los colores de los botones del main_activity_layout.xml al color inicial definicdo
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
     * Funcion que se encarga de reinicar la aplicacion.
     */
    private void restartApp()  {
        //Verificamos si tenemos los permisos necesarios para habilitar el modulo bluetooth
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
            //Solicitamos los permisos al sistema
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.BLUETOOTH_CONNECT},0);
        }
        finConexion();//Aseguramos que cerramos el socket
        bluetoothAdapter.enable();//Activamos el bluetooth

        //Detenemos la app por 0.5segundos para garantizar el correcto inicio del bluetooth del movil
        try {
            //Mensaje informativo para el usuario
            Toast.makeText(MainActivity.this,"Reiniciando espere un poco",Toast.LENGTH_SHORT).show();
            //Delay aplicado a la app
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Creamos el Intent necesario para reinicar la app
        Intent a=new Intent(this,MainActivity.class);
        //Reiniciamos la app
        startActivity(a);
        //Teminamos con la previa activa
        finish();
    }

    /**
     * Funcion encargada de enviar mensajes al vehiculo con el que se comunica
     * @param data: Informacion a enviar al receptor
     */
    private void sendMessage(String data){

        //Verificamos que aun estamos conectados al modulo bluetooth
        if (blueSocket.isConnected() && btt != null) {
            //Enviamos el comando de funcionamiento al modulo bluetooth
            btt.write(data.getBytes());
            //Actualizamos la UI para reflejar que fuel el ultimo que se pulso
            updateUI(data);
        } else {
            //Pequeño mensaje de error, en caso de haber un fallo de envio
            Toast.makeText(MainActivity.this, "Message could not be sent", Toast.LENGTH_SHORT).show();
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
}