/**
 * Sketch usado para probar funcionamiento del sensor HC-SR04.
 * Una vez cargado, mantendremos un objeto frente al sensor, que iremos acercando/alejando para probar su funcionamiento e implementacion
 * 
 */

//Pines del sensor HC-SR04
#define TRIGGER A0
#define ECHO A1
 long timer;

void setup() {
  // put your setup code here, to run once:

  //COnfiguracion de pines del sensor
  pinMode(TRIGGER,OUTPUT);
  pinMode(ECHO,INPUT);

  //Variable que almacena el tiempo que la onda ha tardado
  timer=0;
  
  digitalWrite(TRIGGER,LOW);

  //Monitor para visualizar los resultados
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:

  
  //Activamos el pin emisor de la onda sonica(Trigger)
  digitalWrite(TRIGGER,HIGH);
  
  delayMicroseconds(10);//delay de funcionamiento del pin emisor
  //Desactivamos el pin emisor
  digitalWrite(TRIGGER,LOW);
  
  timer=pulseIn(ECHO,HIGH);//devuelve el tiempo que ha tardado en cambiar de LOW a HIGH
  
  //ecuacion de calculo de la distancia:
  //(tiempo de viaje de la onda)/2 x velocidad de la onda == distancia
  Serial.println((timer/2)*0.0343);
  delay(1000);

}
