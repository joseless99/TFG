package com.example.tfg_app;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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
public class SystemCarActivity extends AppCompatActivity {
    //Creamos la variable que usaremos para la comunicacion
    private BluetoothThread blueThread = null;

    //Botones usados en las vistas
    public Button bF, bB, bR, bL, bS;
    public TextView txt;
    public ImageButton bC;
    private ArrayList<String> listaMac;
    private ArrayAdapter adapter;
    private BluetoothAdapter scanBAdap;
    private String MAC;

    //BROADCAST RECEIVER para el escaneo de dispositivos cercanos
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (listaMac == null) {
                    listaMac = new ArrayList<>();
                }
                listaMac.add(device.getAddress());
                adapter.notifyDataSetChanged();

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Extraemos la MAC a la que nos conectamos
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            MAC = (extras.get("MAC").toString());
        }

        //Carga del Layout de la actividad
        setContentView(R.layout.system_car_layout);

        //Sincronizamos los elementos del layout con los definidos en SystemCarActivity
        bF = findViewById(R.id.bForward);
        bB = findViewById(R.id.bBack);
        bR = findViewById(R.id.bRight);
        bL = findViewById(R.id.bLeft);
        bS = findViewById(R.id.bStop);
        bC = findViewById(R.id.bConexion);
        txt = findViewById(R.id.data);

        //Tratamos de iniciar la comunicacion con el modulo bluetooth esclavo, asignado a
        //esta actividad. Este cargará de forma paralela a la carga de la interfaz principal
        scanBAdap = BluetoothAdapter.getDefaultAdapter();//Este lo guardamos tambien para despues escanear
        blueThread = new BluetoothThread(this);
        blueThread.setBluetoothAdapter(scanBAdap);
        blueThread.setbMAC(MAC);
        blueThread.setImageButton(bC);
        blueThread.setTextView(txt);
        blueThread.start();

        Toast.makeText(SystemCarActivity.this, "Iniciando Comunicacion con " + MAC,
                Toast.LENGTH_SHORT).show();


        //REGISTRAMOS EL BROADCAST RECEIVER
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);


        //Añadimos funcionalidades para cada boton del layout, para cuando se pulsen
        //Boton de accion Avance(Forward) del vehiculo arduino
        bF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res = blueThread.enviarComando("F");
                if (res == 0) {
                    updateUI("F");
                } else if (res == 1) {
                    Toast.makeText(SystemCarActivity.this, "El mensaje no pudo ser enviado",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SystemCarActivity.this, "La conexion esta cerrada,y no se " +
                            "pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton de accion Retroceso(Backwards) del vehiculo arduino
        bB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res = blueThread.enviarComando("B");
                if (res == 0) {
                    updateUI("B");
                } else if (res == 1) {
                    Toast.makeText(SystemCarActivity.this, "El mensaje no pudo ser enviado",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SystemCarActivity.this, "La conexion esta cerrada,y no se" +
                            " pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton de accion giro Derecha(Right) del vehiculo arduino
        bR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res = blueThread.enviarComando("R");
                if (res == 0) {
                    updateUI("R");
                } else if (res == 1) {
                    Toast.makeText(SystemCarActivity.this, "El mensaje no pudo ser enviado",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SystemCarActivity.this, "La conexion esta cerrada,y no se" +
                            " pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton de accion giro Izquierda(Left) del vehiculo arduino
        bL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res = blueThread.enviarComando("L");
                if (res == 0) {
                    updateUI("L");
                } else if (res == 1) {
                    Toast.makeText(SystemCarActivity.this, "El mensaje no pudo ser enviado",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SystemCarActivity.this, "La conexion esta cerrada,y no se" +
                            " pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Boton de accion Detenerse(Stop) del vehiculo arduino
        bS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int res = blueThread.enviarComando("S");
                if (res == 0) {
                    updateUI("S");
                } else if (res == 1) {
                    Toast.makeText(SystemCarActivity.this, "El mensaje no pudo ser enviado",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SystemCarActivity.this, "La conexion esta cerrada,y no se" +
                            " pueden enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Permisos en Android 12 o superior
                if (Build.VERSION.SDK_INT >= 31) {//API>=31
                    if (ActivityCompat.checkSelfPermission(SystemCarActivity.this,
                            Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(SystemCarActivity.this,
                                new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(SystemCarActivity.this,
                            Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(SystemCarActivity.this,
                                new String[]{Manifest.permission.BLUETOOTH}, 0);
                    }

                }

                //Cargamos nueva vista
                setContentView(R.layout.bt_devices);
                ListView listaDsp = findViewById(R.id.dispositivos);

                //Cargamos la lista en la vista.
                //Su contenido se actualiza en el BroadcastReceiver.
                listaMac = new ArrayList<>();
                adapter = new ArrayAdapter(SystemCarActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, listaMac);
                listaDsp.setAdapter(adapter);

                //Iniciamos descubrimiento de dispositivos
                //Verificamos previamente que no se estuviese descurbiendo
                if (scanBAdap.isDiscovering()) {
                    scanBAdap.cancelDiscovery();
                }
                scanBAdap.startDiscovery();


                //Damos una accion a la lista en caso de pulsar un elemento de esta
                listaDsp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        //Solicitud de permisos
                        if (Build.VERSION.SDK_INT >= 31) {//API>=31
                            if (ActivityCompat.checkSelfPermission(SystemCarActivity.this,
                                    Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                              ActivityCompat.requestPermissions(SystemCarActivity.this,
                                      new String[]{Manifest.permission.BLUETOOTH_SCAN},0);
                            }
                        }else {//API<31
                            if (ActivityCompat.checkSelfPermission(SystemCarActivity.this,
                                    Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(SystemCarActivity.this,
                                        new String[]{Manifest.permission.BLUETOOTH}, 0);
                            }
                        }

                        //Dejamos de descubrir
                        scanBAdap.cancelDiscovery();

                        //Extraemos la MAC a usar
                        String newMac  = adapterView.getItemAtPosition(i).toString();

                        //Reiniciamos la actividad
                        Intent rel=new Intent(SystemCarActivity.this, SystemCarActivity.class);
                        rel.putExtra("MAC",newMac);

                        //Reiniciamos la actividad
                        finish();
                        startActivity(rel);



                    }
                });

            }
        });
    }


    /**
     * Funcion encargada de resetear los colores de los botones del system_1_layout.xml al color inicial definicdo
     */
    private void resetColor() {
        //Variable que almacena el color por defecto de los botones decidido
        //Cambiar su valor en caso de cambiar el color por defecto de los botones
        int defaultButtonColor = Color.parseColor("#0000FF");

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
    private void updateUI(String data) {
        //LLamada a la funcion resetClor()
        resetColor();

        //Decidimos que boton hemos de actualizar a verde
        if (data.equals("F")) {
            bF.setBackgroundColor(Color.GREEN);
        } else if (data.equals("B")) {
            bB.setBackgroundColor(Color.GREEN);
        } else if (data.equals("R")) {
            bR.setBackgroundColor(Color.GREEN);
        } else if (data.equals("L")) {
            bL.setBackgroundColor(Color.GREEN);
        } else if (data.equals("S")) {
            bS.setBackgroundColor(Color.GREEN);
        }
    }


    /**
     * Metodo que se activa cuando se cierra esta actividad. Finaliza la comunicaciones abiertos y
     * elimina el Thread usado
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        blueThread.destroyThread();
        blueThread =null;
    }
}