package com.example.tfg_app;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.ParcelUuid;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Clase creada para llevar acabo una comunicacion con un modulo bluetooth remoto
 *
 * Esta clase se encarga de llevar a cabo toda la comunicacion bidireccional con un modulo bluetoth remoto.
 * Originalmente ha sido diseñada para la clase SystemCarActivity pero puede ser tambie usada para otras
 * actividades similares que requieran de comunicacion bidireccional.
 *
 * Una vez establecida la comunicacion con un modulo remoto, inmediatamente se inicia un Thread para la lectura
 * de datos que el modulo remoto envia, siempre y cuando haya una textView asociada a la clase, y que exista en
 * la AppCompatActivity que se le asocia a una instancia de esta.
 */

public class BluetoothThread extends Thread {
    //Parametros de la clase, necesarios para la comunicacion
    private BluetoothSocket bSocket;//Socket para la comunicacion por bluetooth
    private BluetoothDevice bDevice;//Variable para almacenar informacion del dispositivo a conectarnos
    private BluetoothAdapter bAdapter;//Variable que almacena datos del modulo bluetooth de nuestro movil
    private OutputStream bOutput;//Stream por el que enviamos datos al arduino
    private InputStream bInput;//Stream por el que recibimos datos enviados por el arduino
    private AppCompatActivity actividadPadre;//Actividad en la que ejecutamos esta clase. Necesario para permisos de ejecucion
    private Boolean estadoComs;//Variable usada para controlar el estado de comunicacion con el modulo bluetooth
    private Boolean estadoIStream;
    private UUID bUUID;//Identificador Unico Universal (UUID) del perfil SPP del Bluetooth
    private String bMAC;//Identificador MAC del modulo HC-06 usado

    //Parametros adicionales, especificos de la UI de SystemCarActivity.
    private ImageButton botonConexion;//Boton imagen que refleja el estado de comunicacion con el modulo Bluetooth
    private TextView vistaTxt;//Seccion de texto que refleja los datos enviados por el arduino

    //Constructor basico de la clase
    public BluetoothThread() {
        this.bSocket = null;
        this.bDevice = null;
        this.bAdapter = null;
        this.bOutput = null;
        this.bInput = null;
        this.actividadPadre = null;
        this.botonConexion = null;
        this.vistaTxt = null;
        this.estadoComs = false;
        this.estadoIStream = false;
        this.bMAC=null;
        this.bUUID=null;
    }


    //Constructor de la clase, que solicita la actividad en la que este se esta ejecutando
    public BluetoothThread(AppCompatActivity actividad) {
        this();//LLamada al constructor basico de la clase
        setAppCompatActivity(actividad);
    }

    //Metodos set de la clase
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
        this.bSocket = socket;
    }

    public void setOutputStream(OutputStream stream) {
        this.bOutput = stream;
    }

    public void setInputStream(InputStream stream) {
        this.bInput = stream;
    }

    public void setAppCompatActivity(AppCompatActivity actividad) {
        this.actividadPadre = actividad;
    }

    public void setTextView(TextView vista) {
        this.vistaTxt = vista;
    }

    public void setImageButton(ImageButton button) {
        this.botonConexion = button;
    }

    public void setEstadoComs(Boolean actual) {
        this.estadoComs = actual;
    }

    public void setEstadoIStream(Boolean actual) {
        this.estadoIStream = actual;
    }

    public void setbUUID(UUID newUuid) {
        this.bUUID = newUuid;
    }


    public void setbMAC(String newMac) {
        this.bMAC = newMac;
    }

    //Metodos get de la clase
    public BluetoothAdapter getBluetoothAdapter() {
        return this.bAdapter;
    }

    public BluetoothSocket getBluetoothSocket() {
        return this.bSocket;
    }

    public BluetoothDevice getBluetoothDevice() {
        return this.bDevice;
    }

    public OutputStream getOutputStream() {
        return this.bOutput;
    }

    public InputStream getInputStream() {
        return this.bInput;
    }

    public AppCompatActivity getAppCompatActivity() {
        return this.actividadPadre;
    }

    public Boolean getEstadoComs() {
        return this.estadoComs;
    }

    public Boolean getEstadoIStream() {
        return this.estadoIStream;
    }

    public ImageButton getImageButton() {
        return this.botonConexion;
    }

    public TextView getTextView() {
        return this.vistaTxt;
    }


    public UUID getbUUID() {
        return this.bUUID;
    }

    public String getbMAC() {
        return this.bMAC;
    }


    /**
     * Funcion run() de la clase Thread sobre-excritas. Esta se encargara ahora de inicializar la comunicacion
     * bluetooth de la aplicacion con un dispositivo receptor. Esta se ejecutara cuando, al instaciar la clase
     * en una actividad se ejecute la funcion start() heredada de la clase Thread.
     *
     * Si la comunicacion se ha llevado acabo exitosamente, el usuario vera en la interfaz que el boton de conexion
     * bluetooth ha cambiado a color verde, ademas de que el LED del HC-06 dejara de parpadear.
     * En caso de no poder haberse conectado, el boton de comunicacion bluetooth se vera de color rojo
     *
     * NOTA: Para que esta se lleve acabo correctamente antes hay que haber emparejado el modulo HC-06 con el movil a usar
     *
     */
    @Override
    public void run() {
        //Verificamos que tengamos los permisos necesarios autorizado
        //Para dispositivos con API>=31(Android 12 o superior) solicitamos permisos BLUETOOTH_CONNECT
        //Para dispositivos con API<31(Android 11 o previos) Solicitanos permisos BLUETOOTH
        if (Build.VERSION.SDK_INT >= 31) {//API>=31
            //Verificamos que tengamos o no los permisos necesarios
            if (ActivityCompat.checkSelfPermission(actividadPadre.getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                //Solicitamos el permiso de BLUETOOTH_CONNECT al no tenerlo
                ActivityCompat.requestPermissions(getAppCompatActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
            }
        } else {//API<31
            //Verificamos que tengamos o no los permisos necesarios
            if (ActivityCompat.checkSelfPermission(actividadPadre.getApplicationContext(), Manifest.permission.BLUETOOTH)
                    != PackageManager.PERMISSION_GRANTED) {
                //Solicitamos el permiso de BLUETOOTH al no tenerlo
                ActivityCompat.requestPermissions(getAppCompatActivity(), new String[]{Manifest.permission.BLUETOOTH},
                        0);
            }
        }

        try {
            //Tras tener los permissos necesarios iniciamos la conexion con el modulo
            //Accededemos al Bluetooth del nuestro dispositivo
            setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());

            //Cargamos el dispositivo remoto al que deseamos conectarnos con bMAC
            setBluetoothDevice(this.bAdapter.getRemoteDevice(bMAC));

            //Establecemos el puerto de comunicacion necesario con bUUID
            setBluetoothSocket(this.bDevice.createRfcommSocketToServiceRecord(bUUID));

            //Abrimos la conexion por el puerto con el dispositivo esclavo de bluetooth
            getBluetoothSocket().connect();

            //Enlazamos la via de conexion de datos entre los 2 dispositivos
            setOutputStream(getBluetoothSocket().getOutputStream());
            setInputStream(getBluetoothSocket().getInputStream());


            //Cambiamos la variable de estado de comunicacion a true
            setEstadoComs(true);

            //Enviamos un mensaje al arduino de que estamos listos para enviar instrucciones, asi como
            //de recibir aquellas que este mande
            enviarComando("0");


            //Este thread de recepcipon de datos lo crearemos solo si existe una vista a donde enviar la informacion
            inicarLecturaDatos(null);

        } catch (Exception e) {//Si hay cualquier fallo marcamos que no se ha establecido la conexion

            //Actualizamos el color del Boton y la variable de estado de control
            setEstadoComs(false);
            setEstadoIStream(false);
            //Cambiamos el color del boton de comunicacion a rojo en el caso de que exista
            if (getImageButton() != null) {
                botonConexion.setBackgroundColor(Color.RED);
            }
        }

        this.actividadPadre.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String mssg;
                if(estadoComs) {
                    mssg="Comunicacion establecida";
                }else{
                    mssg="Error, no pudo hacerse la conexion";
                }

                Toast.makeText(actividadPadre, mssg, Toast.LENGTH_SHORT).show();
            }
        });
        //Cambiamos el color del boton de comunicacion a verde en el caso de que exista, y la comunicacion
        //haya sido existosa
        if (getEstadoComs() && getImageButton() != null) {
            botonConexion.setBackgroundColor(Color.GREEN);
        }


    }

    /**
     * Esta funcion se encarga de crear un hilo, por el que se puedan recibir datos que el modulo bluetoth
     * remoto envie a nuestra app. Los datos se envia a una TextView, que se almacenara en la clase
     *
     * @param vista: Objeto TextView que recibira los datos que se leen de InputStream. Valores esperados
     *          -null: se usara la TextView que esta cargada en la clase, cuando se instanció.
     *          -TextView: se establecera como Textview de la clase y se le enviara los datos leidos de InputStream a esta
     *
     * NOTA: En caso de que tanto vista como la TextView de la clase sean null, esta funcion no hara nada
     */
    public void inicarLecturaDatos(TextView vista) {

        //En caso de querer cambiar la vista a la que se enviaran los datos
        if (vista != null) {
            setTextView(vista);
        }

        //Hacemos que un BluetothThread que este leyendo de IStream finalize
        if (getEstadoIStream()) {
            setEstadoIStream(false);
        }

        //Creacion del Thread de lectura del InputStream
        if (getTextView() != null && getEstadoComs()) {

            setEstadoIStream(true);
            //Lectura de InputStream
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Verificamos que la comunicacion sique estando activa
                    while (getEstadoComs()) {
                        try {
                            vistaTxt.setText(leerdatos());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    /**
     * Funcion encargada de enviar mensajes al dispositivo receptor, a traves del OutputStream de la
     * Comunicacion bluetooth
     *
     * @param data: Informacion a enviar al receptor
     *
     * @return 0:  EL mensaje se envio correctamente
     *         1:  Hubo un error al enviar el mensaje
     *         -1: No existe comunicacion con un modulo bluetooth remoto
     */
    public int enviarComando(String data) {

        //Verificamos que aun estamos conectados al modulo bluetooth
        if (getEstadoComs()) {

            //Conevrtimos el comando a tipo byte[]
            byte[] comando = data.getBytes();

            //Enviamos el comando de funcionamiento al modulo bluetooth
            try {
                getOutputStream().write(comando);
                return 0;
            } catch (IOException e) {
                //Hubo algun error al enviar un mensaje
                e.printStackTrace();
                return 1;
            }
        } else {
            //Codigo de error de que no hay una comunicacion abierta
            return -1;
        }
    }

    /**
     *Funcion encargada de leer el buffer de datos que el movil recibe del arduino.
     *
     *En cada llamada de la funcion, se leen los datos de InputStream hasta llegar a un final de linea
     *
     * @return String: Cadena String de los datos que se han leido del InputStream
     */
    public String leerdatos() throws IOException {

        BufferedReader r = new BufferedReader(new InputStreamReader(getInputStream()));
        return r.readLine();
    }


    /**
     * Funcion encargada de cerrar la comunicacion con el modulo bluetooth remoto.
     *
     * Esta se encargara de cerrar todos los procesos asociados a la comunicacion con el modulo bluetooth
     * remoto. Solo se usa en destroyThread() cuando vamos a cerrar la conexion y descartar este Thread
     *
     */
    private void finConexion() {
        if (ActivityCompat.checkSelfPermission(actividadPadre.getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getAppCompatActivity(),new String[]{Manifest.permission.BLUETOOTH_SCAN},0);
        }
        if (this.bAdapter.isDiscovering()) {

            this.bAdapter.cancelDiscovery();
        }


        if (getEstadoComs()) {
            //Enviamos un comando al arduino para que comprenda que se ha acabado la conexion
            enviarComando("1");

            //Cambiamos el estado de la comunicacion
            this.estadoComs = false;
            this.estadoIStream = false;
            try {
                this.bInput.close();
                this.bOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Cerramos la conexion con el arduino
            try {
                this.bSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Vaciamos las demas variables, cambiando su  valor a null
        this.bSocket = null;
        this.bDevice = null;
        this.bAdapter = null;
        this.bInput = null;
        this.bOutput = null;
        this.botonConexion = null;
        this.vistaTxt = null;
    }






    /**
     * Funcion a la que se llama para poder destruir correctamente la clase BluetoothThread, o para cerrar
     * definitivamente la comunicacion bluetooth.
     *
     * Esta hara primero una llamada al metodo finConexion() con el que cerrar la conexion. Por ultimo
     * se ejecutara el metodo interrupt() de la clase.
     *
     */
    public void destroyThread(){

        finConexion();
        interrupt();
    }

}
