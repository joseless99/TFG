package com.example.tfg_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Creamos los botones que estaran asociados al layout de esta actividad
    public Button bs1,bs2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Asignacion del boton del layout con el objeto boton
        bs1=findViewById(R.id.bsystem_1);
        bs2=findViewById(R.id.bsystem_2);

        bs1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSystem1();
            }
        });
        bs2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Este boton es de ejemplo y no hace nada por el momento", Toast.LENGTH_SHORT);
            }
        });
    }
    public void startSystem1(){
        //Creamos el Intent necesario para iniciar la actividad System1Activity
        Intent a = new Intent(this,System1Activity.class);
        //Iniciamos la actividad
        startActivity(a);
    }
}