/**
 * Sketch de pruebas de comunicacion con el modulo bluetooth.
 * Este sketch esta diselñado para probar el envio de datos a la aplicacion android maestra.
 * Cuando se establece conexion este envia el valor de la variable i, la cual es un contador que aumenta de 1 en 1
 */

char state=' ';
int i=0;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial1.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:

    if(Serial1.available())
    {
      state=Serial1.read();
    }

    if(state=='0'){
 
      Serial1.println(i);
      Serial.print("Conectado\n");
      i++;
    }else
    {
      Serial.print("No conectado\n");
  }
    delay(500);
}
