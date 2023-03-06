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

        new Thread(new Runnable() {
            @Override
            public void run() {
                inicioConexionB();
            }
        }).start();


        //Añadimos funcionalidades para cada boton del layout, para cuando se pulsen
        //Boton de accion Avance(Forward) del vehiculo arduino
        bF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarComando("F");
            }
        });

        //Boton de accion Retroceso(Backwards) del vehiculo arduino
        bB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarComando("B");
            }
        });

        //Boton de accion giro Derecha(Right) del vehiculo arduino
        bR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarComando("R");
            }
        });

        //Boton de accion giro Izquierda(Left) del vehiculo arduino
        bL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarComando("L");
            }
        });

        //Boton de accion Detenerse(Stop) del vehiculo arduino
        bS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarComando("S");
            }
        });

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
    public void inicioConexionB(){

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
            setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());

            //Cargamos el dispositivo remoto al que deseamos conectarnos con bMAC
            setBluetoothDevice(getBluetoothAdapter().getRemoteDevice(bMAC));

            //Establecemos el puerto de comunicacion necesario con bUUID
            setBluetoothSocket(getBluetoothDevice().createRfcommSocketToServiceRecord(bUUID));

            //Abrimos la conexion por el puerto con el dispositivo esclavo de bluetooth
            bSocket.connect();

            //Enlazamos la via de conexion de datos entre los 2 dispositivos
            setOutputStream(getBluetoothSocket().getOutputStream());
            setInputStream(getBluetoothSocket().getInputStream());

            //Cambaimos el colo del Boton a verde para relfejar que el dispositivo se ha conectado
            //correctamente con el arduino receptor
            bC.setBackgroundColor(Color.GREEN);

            //Enviamos mensaje al arduino para que este empieze a funcionar
            enviarComando("0");
            state="On";
            //Funcionalidad añadida para indicar al usuario que la conexion esta activa
            bC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(System1Activity.this,"La conexion esta activa y funcionando",Toast.LENGTH_SHORT).show();
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(state=="On"){
                        try {
                            txt.setText(leerdatos());
                        } catch (Exception e) {

                        }
                    }
                }
            }).start();


        }catch (Exception e){//En caso de surgir un fallo inesperado durante la comunicacion

            bC.setBackgroundColor(Color.RED);

            //Funcionalidad de reintento de conexion con el modulo bluetooth en caso de fallos
            bC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //MEnsaje de informacion para el usuario
                    Toast.makeText(System1Activity.this,"Reintentando conexion",Toast.LENGTH_SHORT).show();

                    //Indicamos que al reintentar la conexion lo haga en un nuevo Thread,
                    //en caso de que esta vuelva a fallar
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            inicioConexionB();
                        }
                    }).start();
                }
            });
        }

//        if(!bSocket.isConnected()){
//            //MEnsaje de informacion para el usuario
//            Toast.makeText(System1Activity.,"Error de conexion",Toast.LENGTH_SHORT).show();
//
//        }

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


    /**
     * Funcion encargada de enviar mensajes al vehiculo con el que se comunica
     * @param data: Informacion a enviar al receptor
     */
    private void enviarComando(String data){

        //Verificamos que aun estamos conectados al modulo bluetooth
        if (getBluetoothSocket().isConnected()) {

            //Conevrtimos el comando a tipo byte[]
            byte[] comando=data.getBytes();

            //Enviamos el comando de funcionamiento al modulo bluetooth
            try {
                this.bOutput.write(comando);
            } catch (IOException e) {
                Toast.makeText(System1Activity.this, "El mensaje no pudo ser enviado", Toast.LENGTH_SHORT).show();
            }

            //Actualizamos la UI para reflejar que fuel el ultimo que se pulso
            updateUI(data);

        } else {
            //Pequeño mensaje de error, en caso de haber un fallo de envio
            Toast.makeText(System1Activity.this, "La conexion esta cerrada,y no se pueden enviar mensajes", Toast.LENGTH_SHORT).show();
        }
    }


    public String leerdatos() throws IOException {

        BufferedReader r = new BufferedReader(new InputStreamReader(getInputStream()));
        String data=r.readLine();
        return data;
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
     * Funcion encargada de cerrar la conexion Bluetooth con el dispositivo remoto
     */
    private void finConexionB(){

        //Cerramos conexion
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

    /**
     * Metodo que se activa cuando se cierra esta actividad. Finaliza la comunicaciones abiertos y
     * elimina el Thread usado
     */
    @Override
    public void onDestroy(){
        super.onDestroy();

        //Solo cerraremos la conexion si sabemos que se ha llegado a establecer esta con el arduino
        if(getBluetoothSocket().isConnected()) {
            state="Off";
            //Enviamos un comando al arduino para que comprenda que se ha acabado la conexion
            enviarComando("O");
            try {
                getOutputStream().close();
                getInputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Cerramos la conexion con el arduino
            finConexionB();
        }
    }
}