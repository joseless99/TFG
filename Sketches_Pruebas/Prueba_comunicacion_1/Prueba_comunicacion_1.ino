/**
 * Sketch de pruebas de comunicacion con el modulo bluetooth.
 * Este sketch es usado para probar el envio de datos al arduino es correcto. Solo imprimira datos si el buffer al que esta conectado el
 * modulo bluetoth tiene informacion (pines 0 y 1 del arduino)
 */
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial1.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:

    if(Serial1.available())//Comprobamos que haya contenidos a leer del modulo Bluetooth
    {
      Serial.write(Serial1.read());//Imprimimos lo leido de Serial1
    }
}
