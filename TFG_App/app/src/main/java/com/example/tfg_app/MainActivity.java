package com.example.tfg_app;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 *  Actividad principal de la aplicacion Android desarrollada para el TFG
 *
 *  Se trata de un menu de acceso  inicial a distintos posibles dispositivos remotos a los que poder comunicarse
 *  usando la aplicacion. En nuestro caso solo tenemos acceso a 2 botones con el fin de reflejar como podria verse.
 */

public class MainActivity extends AppCompatActivity {

    //Creamos los botones que estaran asociados al layout de esta actividad
    public Button bs1,bs2;
    BluetoothAdapter bAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Asignacion del boton del layout con el objeto boton
        bs1=findViewById(R.id.bsystem_1);
        bs2=findViewById(R.id.bsystem_2);

        //Cargamos el dispositivo bluetooth del movil
        bAdapter=BluetoothAdapter.getDefaultAdapter();

        //Verificamos si este existe
        if(bAdapter==null)
        {
            Toast.makeText(this,"No se ha detectado un modulo bluetooth operativo. " +
                    "La app en este dispositivo no funcionará",Toast.LENGTH_LONG).show();
        }

        bs1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bAdapter!=null) {
                    if (bAdapter.isEnabled()) {
                        startSystem1();
                    } else {
                        Toast.makeText(MainActivity.this, "El bluetooth del dispositivo " +
                                "esta desactivado. Actívelo antes de continuar", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        bs2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bAdapter!=null) {
                    if (bAdapter.isEnabled()) {
                        Toast.makeText(MainActivity.this,"Este boton es de ejemplo y no " +
                                "hace nada por el momento", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "El bluetooth del dispositivo " +
                                "esta desactivado. Actívelo antes de continuar", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    /**
     * Funcion que se encarga de iniciar la actividad asociada al vehiculo electronico de ejemplo
     */
    public void startSystem1(){
        //Creamos el Intent necesario para iniciar la actividad System1Activity
        Intent a = new Intent(this,System1Activity.class);
        //Iniciamos la actividad
        startActivity(a);
    }
}