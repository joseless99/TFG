/**
 * Sketch de prueba de integracion del L293D y el HC-SR04.
 * Este sketch ha sido usado para probar si se podian alimentar al HC-SR04 y el pin Vcc1 del L293D, con el pin de 5V del arduino
 */
#define TRIGGER 7
#define ECHO 8
#define EN1 4
#define IN1 5
#define IN2 6
#define EN2 9
#define IN3 10
#define IN4 11

void setup() {
  // put your setup code here, to run once:
  
  pinMode(TRIGGER,OUTPUT);
  pinMode(EN1,OUTPUT);
  pinMode(IN1,OUTPUT);
  pinMode(IN2,OUTPUT);
  pinMode(EN2,OUTPUT);
  pinMode(IN3,OUTPUT);
  pinMode(IN4,OUTPUT);
  pinMode(ECHO,INPUT);

  //Establecemos a LOW todos los pines  de salida usados
  digitalWrite(TRIGGER,LOW);
  digitalWrite(EN1,LOW);
  digitalWrite(IN1,LOW);
  digitalWrite(IN2,LOW);
  digitalWrite(EN2,LOW);
  digitalWrite(IN3,LOW);
  digitalWrite(IN4,LOW);

  //Activamos el terminal para iprimir que se recibe del HC-SR04
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  digitalWrite(EN1,HIGH);
  digitalWrite(IN1,HIGH);
  digitalWrite(IN2,LOW);
  digitalWrite(EN2,HIGH);
  digitalWrite(IN3,HIGH);
  digitalWrite(IN4,LOW);
  uSensor();
  delay(1000);

}

void uSensor(){

  
  //Activamos el pin emisor de la onda sonica(Trigger)
  digitalWrite(TRIGGER,HIGH);
  
  delayMicroseconds(10);//delay de funcionamiento del pin emisor
  //Desactivamos el pin emisor
  digitalWrite(TRIGGER,LOW);
  
  long timer=pulseIn(ECHO,HIGH);//devuelve el tiempo que ha tardado en cambiar de LOW a HIGH
  
  //ecuacion de calculo de la distancia:
  //(tiempo de viaje de la onda)/2 x velocidad de la onda == distancia
  Serial.println((timer/2)*0.0343);
}
