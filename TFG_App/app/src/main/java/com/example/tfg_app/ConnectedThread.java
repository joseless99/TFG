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
    private  BluetoothSocket bSocket;
    private  OutputStream bData;

    public ConnectedThread(){
        bSocket=null;
        bData=null;
    }

    /**
     * Constructor de la clase ConnectedThread.
     * @param socket: Socket de comunicacion bluetooth
     */
    public ConnectedThread(BluetoothSocket socket){
        setSocket(socket);
        //Al llamar setOutputStream() y pasarle null, cogera el oputputStream de bSocket, que es lo que queremos que tenga bData
        setOutputStream(null);
    }
    //Metodos get/set de la variables de la clase
    public BluetoothSocket getSocket(){
        return bSocket;
    }

    public OutputStream getOutputStream(){
        return bData;
    }

    public void setSocket(BluetoothSocket newSocket) {
        if (bSocket != null) {
            try {
                bSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(newSocket!=null){
            bSocket=newSocket;
        }else{
            bSocket=null;
        }

    }

    public void setOutputStream(OutputStream newStream){
        if(newStream!=null){//Verificamos que el nuevo OutputStream no es null
            bData=newStream;
        }else{//Si el nuevo OutputStream es null.
            if(bSocket!=null){//Si el socket de la clase no es null
                //Try/Catche en caso de posible error
                try {
                    bData= bSocket.getOutputStream();//Tomamos el OutputStream del socket
                } catch (IOException e) {
                    e.printStackTrace();
                    bData=null;//en caso de erro lo dejamos a null
                }

            }else{//Si el socket es null OutputStream es null
                bData=null;
            }
        }
    }

    /**
     * Funcion encargada de enviar datos que se le envian al dispositivo bluetooth receptor
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
