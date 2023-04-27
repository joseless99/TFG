/**
 * Sketch de pruebas de comunicacion con el modulo bluetooth.
 * Este sketch esta diseñado para probar el envio de datos a la aplicacion android maestra. Primero verificamos el estado de la conexion
 * Cuando se establece conexion este envia el valor de la variable i, la cual es un contador que aumenta de 1 en 1
 */

char state=' ';//Variable que almacena si la conexion esta iniciada o no
int i=0;//Variable contador
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial1.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:

    if(Serial1.available())//Verificamos que se ha recibido un comando de inicio/fin de conexión
    {
      state=Serial1.read();
    }

    if(state=='0'){//Solo enviamos datos si la app nos ha confirmado que se ha establecido la conexion
 
      Serial1.println(i);//Se envian con el caracter "endline" debido a como se ha implementado la recepcion de datos en la aplicacion
      Serial.print("Conectado\n");//Mensaje para verificar el estado de conexion
      i++;
    }else
    {
      Serial.print("No conectado\n");//Mensaje para verificar el estado de conexion
  }
    delay(500);//delay añadido para observar mejor los resultados de la prueba
}
