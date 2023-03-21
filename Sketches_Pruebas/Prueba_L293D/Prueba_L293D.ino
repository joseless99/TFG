/**
 * SKetch usado para probar el funcionamiento del controlador de motores L293D
 */

#define EN1 2
#define IN1 3
#define IN2 4
#define EN2 11
#define IN3 9
#define IN4 10

void setup()
{
  pinMode(EN1,OUTPUT);
  pinMode(IN1,OUTPUT);
  pinMode(IN2,OUTPUT);
  
  pinMode(EN2,OUTPUT);
  pinMode(IN3,OUTPUT);
  pinMode(IN4,OUTPUT);
  
  digitalWrite(EN1,HIGH);
  digitalWrite(EN2,HIGH);
}
void loop()
{ 
  
  digitalWrite(IN2,LOW);
  digitalWrite(IN4,LOW);
  digitalWrite(IN1,HIGH);
  digitalWrite(IN3,HIGH);
  delay(1000);
  digitalWrite(IN1,LOW);
  digitalWrite(IN3,LOW);
  digitalWrite(IN2,HIGH);
  digitalWrite(IN4,HIGH);
  delay(1000);
}
