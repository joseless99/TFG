/**
 * Sketch usado para probar funcionamiento del sensor HC-SR04
 */

//Pines del sensor HC-SR04
#define TRIGGER 8
#define ECHO 7

void setup() {
  // put your setup code here, to run once:
  
  pinMode(TRIGGER,OUTPUT);
  pinMode(ECHO,INPUT);

  
  digitalWrite(TRIGGER,LOW);

  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:

  
  //Activamos el pin emisor de la onda sonica(Trigger)
  digitalWrite(TRIGGER,HIGH);
  delayMicroseconds(10);//delay de funcionamiento del pin emisor
  //Desactivamos el pin emisor
  digitalWrite(TRIGGER,LOW);
  long timer=pulseIn(ECHO,HIGH);//devuelve el tiempo que ha estado en HIGH en microsegundos
  //ecuacion de calculo de la distancia
  //0.034= velocidad de la onda (340m/s)
  //3= tiempo establecido del delay del pin trigger 
  Serial.println(timer*0.01723);
  delay(1000);

}
