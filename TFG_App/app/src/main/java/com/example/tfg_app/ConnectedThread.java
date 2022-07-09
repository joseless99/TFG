/**
 * Clase ConnectedThread encargada de enviar datos usando el modulo bluetooth del sistema
 *
 * Esta clase extiende de la clase Thread de Java, y se trata de un hilo de proceso usado para las comunicaciones bluetooth
 * de la aplicacion
 */

package com.example.tfg_app;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;

public class ConnectedThread extends Thread{
    private final BluetoothSocket bSocket;
    private final OutputStream bData;

    public ConnectedThread(){
        bSocket=null;
        bData=null;
    }

    /**
     * Constructor de la clase ConnectedThread.
     * @param socket: Socket de comunicacion bluetooth
     */
    public ConnectedThread(BluetoothSocket socket){
        bSocket=socket;
        OutputStream temp=null;
        try {
            temp= socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bData=temp;
    }

    /**
     * Funcion encargada de enviar datos por bluetooth
     * @param data Informacion a enviar por bluetooth
     */
    public void write(byte [] data){
        try {
            bData.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
