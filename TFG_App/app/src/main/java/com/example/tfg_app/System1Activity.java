package com.example.tfg_app;


import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    //Creamos la variable que usaremos para la comunicacion
    private BluetoothThread blueThread =null;

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
        Toast.makeText(System1Activity.this, "Iniciando Comunicacion.Espere un poco",
                Toast.LENGTH_SHORT).show();

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

        blueThread =new BluetoothThread(this);
        blueThread.setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
        blueThread.setImageButton(bC);
        blueThread.setTextView(txt);
        blueThread.start();

        //Añadimos funcionalidades para cada boton del layout, para cuando se pulsen
        //Boton de accion Avance(Forward) del vehiculo arduino
        bF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res= blueThread.enviarComando("F");
                if(res==0){
                    updateUI("F");
                }else if(res==1){
                    Toast.makeText(System1Activity.this, "El mensaje no pudo ser enviado",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(System1Activity.this, "La conexion esta cerrada,y no se " +
                            "pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton de accion Retroceso(Backwards) del vehiculo arduino
        bB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res= blueThread.enviarComando("B");
                if(res==0){
                    updateUI("B");
                }else if(res==1){
                    Toast.makeText(System1Activity.this, "El mensaje no pudo ser enviado",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(System1Activity.this, "La conexion esta cerrada,y no se" +
                            " pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton de accion giro Derecha(Right) del vehiculo arduino
        bR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res= blueThread.enviarComando("R");
                if(res==0){
                    updateUI("R");
                }else if(res==1){
                    Toast.makeText(System1Activity.this, "El mensaje no pudo ser enviado",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(System1Activity.this, "La conexion esta cerrada,y no se" +
                            " pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton de accion giro Izquierda(Left) del vehiculo arduino
        bL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res= blueThread.enviarComando("L");
                if(res==0){
                    updateUI("L");
                }else if(res==1){
                    Toast.makeText(System1Activity.this, "El mensaje no pudo ser enviado",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(System1Activity.this, "La conexion esta cerrada,y no se" +
                            " pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton de accion Detenerse(Stop) del vehiculo arduino
        bS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res= blueThread.enviarComando("S");
                if(res==0){
                    updateUI("S");
                }else if(res==1){
                    Toast.makeText(System1Activity.this, "El mensaje no pudo ser enviado",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(System1Activity.this, "La conexion esta cerrada,y no se" +
                            " pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bC.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(blueThread.getBluetoothSocket().isConnected()){
                    Toast.makeText(System1Activity.super.getApplicationContext(), "La conexion esta " +
                            "activa y funcionando", Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(System1Activity.super.getApplicationContext(),"Reintentando conexion",
                            Toast.LENGTH_SHORT).show();
                    //Como no podemos destruir el Thread lo reconstruimos desde 0
                    blueThread =new BluetoothThread();
                    blueThread.setAppCompatActivity(System1Activity.this);
                    blueThread.setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
                    blueThread.setImageButton(bC);
                    blueThread.setTextView(txt);
                    blueThread.start();

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
        blueThread.destroyThread();
        blueThread =null;
    }
}