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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    private static final UUID blueUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//UUID del Modulo bluetooth en android
    public static final String blueMac = "00:20:04:BD:D4:DE";//Identificador MAC del modulo HC-06 usado

    //Codigos de identificacion de permisos de bluetooth
    //private static final int BLUETOOTH_CONNECT_CODE = 100;
    //Botones usados en las vistas
    public Button bF, bB, bR, bL, bS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Adaptador bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Verificamos que el dispositivo bluetooth es capaz de usar bluetooth
        if (bluetoothAdapter == null) {
            //cargamos vista para indicar que no posee BT compatible
            //toDo: Vista para cuando no se tiene bluetooth
            setContentView(R.layout.bt_non_existent_layout);
        } else {
            //en caso de disponer de bluetooth, verificamos si esta activado o no
            if (!bluetoothAdapter.isEnabled()) {//caso de no estar activado el bluetooth
                setContentView(R.layout.bt_disable_layout);//Cargamos una vista de error
            } else {//Si el bluetooth esta activado

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

                            //Verificamos que aun estamos conectados al modulo bluetooth
                            if (blueSocket.isConnected() && btt != null) {
                                //Enviamos el comando de funcionamiento al modulo bluetooth
                                btt.write("F".getBytes());
                                //establecemos los botones a color original
                                resetColor();
                                //Cambiamos el color para reflejar que fuel el ultimo que se pulso
                                bF.setBackgroundColor(Color.GREEN);
                            } else {
                                //Pequeño mensaje de error, en caso de haber un fallo de envio
                                Toast.makeText(MainActivity.this, "Message could not be sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //Boton de accion Retroceso(Backwards) del vehiculo arduino
                    bB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Verificamos que aun estamos conectados al modulo bluetooth
                            if (blueSocket.isConnected() && btt != null) {

                                //Enviamos el comando de funcionamiento al modulo bluetooth
                                btt.write("B".getBytes());

                                    //establecemos los botones a color original
                                    resetColor();
                                    //Cambiamos el color para reflejar que fuel el ultimo que se pulso
                                    bB.setBackgroundColor(Color.GREEN);
                            } else {
                                //Pequeño mensaje de error, en caso de haber un fallo de envio
                                Toast.makeText(MainActivity.this, "Message could not be sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //Boton de accion giro Derecha(Right) del vehiculo arduino
                    bR.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Verificamos que aun estamos conectados al modulo bluetooth
                            if (blueSocket.isConnected() && btt != null) {

                                //Enviamos el comando de funcionamiento al modulo bluetooth
                                btt.write("R".getBytes());
                                //establecemos los botones a color original
                                resetColor();
                                //Cambiamos el color para reflejar que fuel el ultimo que se pulso
                                bR.setBackgroundColor(Color.GREEN);
                            } else {
                                //Pequeño mensaje de error, en caso de haber un fallo de envio
                                Toast.makeText(MainActivity.this, "Message could not be sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //Boton de accion giro Izquierda(Left) del vehiculo arduino
                    bL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Verificamos que aun estamos conectados al modulo bluetooth
                            if (blueSocket.isConnected() && btt != null) {

                                //Enviamos el comando de funcionamiento al modulo bluetooth
                                btt.write("L".getBytes());

                                //establecemos los botones a color original
                                resetColor();
                                //Cambiamos el color para reflejar que fuel el ultimo que se pulso
                                bL.setBackgroundColor(Color.GREEN);

                            } else {
                                //Pequeño mensaje de error, en caso de haber un fallo de envio
                                Toast.makeText(MainActivity.this, "Message could not be sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //Boton de accion Detenerse(Stop) del vehiculo arduino
                    bS.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Verificamos que aun estamos conectados al modulo bluetooth
                            if (blueSocket.isConnected() && btt != null) {

                                //Enviamos el comando de funcionamiento al modulo bluetooth
                                btt.write("S".getBytes());
                                //establecemos los botones a color original
                                resetColor();
                                //Cambiamos el color para reflejar que fuel el ultimo que se pulso
                                bS.setBackgroundColor(Color.GREEN);
                            } else {
                                //Pequeño mensaje de error, en caso de haber un fallo de envio
                                Toast.makeText(MainActivity.this, "Message could not be sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
    public void resetColor(){
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
}