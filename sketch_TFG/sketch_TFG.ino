/**
 * Este es el sketch a cargar el en arduino del coche.
 * Este tendra conectados 2 motores de 5V DC ademas de un modulo bluetooth, por el cual se 
 * reciben instrucciones de acciones a realizar, y un modulo emisor-receptor de ondas ultra-sonicas para calcular la distancia con el objeto enfrente del mismo. 
 * El emisor de las instrucciones es una aplicacion Android desarrollada para este proyecto.
 * 
 * @author: Juan Jose Ropero Cerro
 * 
 */
//Pines del motor 1
#define M1ENABLE 3
#define M1AVANCE 4
#define M1RETROCESO 5


//Pines del motor 2
#define M2ENABLE 9 
#define M2AVANCE 10
#define M2RETROCESO 11

//Pines del sensor HC-SR04
#define TRIGGER A0
#define ECHO A1
char dato;//Variable que almacena los datos que se leen del modulo bluetooth
bool activo;//Estado de conexion con el sistema Maestro
void setup() {
 //Iniciamos monitores a usar.Serial es solo usado para verificar el envio y recepcion de datos correcto
  Serial.begin(9600);//Borrar para la version final
  Serial1.begin(9600);//Monitor de comunicacion para el modulo bluetooth.Conectar dicho modulo a pines 0,1
  
  //Configracion inical de los pines de los motores y de variables globales usadas
  pinMode(M1ENABLE,OUTPUT);
  pinMode(M1AVANCE,OUTPUT);
  pinMode(M1RETROCESO,OUTPUT);
  
  pinMode(M2ENABLE,OUTPUT);
  pinMode(M2AVANCE,OUTPUT);
  pinMode(M2RETROCESO,OUTPUT);
  pinMode(TRIGGER,OUTPUT);
  pinMode(ECHO,INPUT);
  
  //Establecemos todo pin de salida a los motores a LOW
  digitalWrite(M1ENABLE,LOW);
  digitalWrite(M1AVANCE,LOW);
  digitalWrite(M1RETROCESO,LOW);
  digitalWrite(M2ENABLE,LOW);
  digitalWrite(M2AVANCE,LOW);
  digitalWrite(M2RETROCESO,LOW);
  digitalWrite(TRIGGER,LOW);

  dato=' ';
  activo=false;
}

void loop() {
  Serial.println(activo);//Borrar para version final
  if(activo==true){
        if(Serial1.available()>0){//Verificamos si existen datos recibidos por el canal de comunicacion en el que esta el modulo bluetooth
          
          dato=Serial1.read();//Guardamos los datos del canal de comunicacion BT en la variable dato
          
          //Usado en pruebas para verificar correcta recepcion de mensajes
          Serial.write(dato);//Borrar para version final
          
          //Comparamos los datos recibidos del monitor Serial1 con las posibles acciones a ejecutar
          //Para cada accion, ejecuta primero frenado(), y tras un pequeño delay se configura los pines para la accion recibida del modulo
          if(dato=='F'){//Avance del vehiculo
           
            frenado();
            avance();
            
          }else if(dato=='B'){//Retroceso del vehiculo
           
            frenado();
            retroceso();
      
          }else if(dato=='R'){//Giro a la derecha del vehiculo
            
            frenado();
            giroDerecha();
            
          }else if(dato=='L'){//Giro a la izquierda del vehiculo
            
            frenado();
            giroIzquierda();
            
          }else if(dato=='S'){//Detencion del vehiculo  
            
             frenado();
          }else if(dato=='1')//Finalizacion de comunicacion con el emisor
          {
            
            frenado();//detenemos el vehiculo para que no pierda el control
            activo=false;//Impedimos que el sistema siga funcionando
            
            //Desactivamos los motores
            digitalWrite(M1ENABLE,LOW);
            digitalWrite(M2ENABLE,LOW);
            
            Serial.print("Desactivado\n");//Mensaje de prueba en el arduino. Borrar en la version final
          }       
      }
      Serial.println(dato);//Borrar
      //Enviamos al dispositivo maestro la distancia del vehiculo con un obstaculo frontal
      Serial1.println(uSensor());
      
  }else{//En caso de que la comunicacion este desactivada verificamos si se ha recibido un mensaje de haber iniciado comunicaciones
    if(Serial1.available()>0){//Verificamos si se han enviado datos de inicio
        
        if(Serial1.read()=='0'){//Si se recibe el comando correcto
        
          activo=true;//Activamos el sistema de comunicacion del coche
          
          //Activamos los motores del coche
          digitalWrite(M1ENABLE,HIGH);
          digitalWrite(M2ENABLE,HIGH);
          
          Serial.print("Activado\n");//Mensaje de prueba en el arduino. Borrar en la version final
        
        }else{
     
          activo=false;
          
          //Desactivamos los motores del coche
          digitalWrite(M1ENABLE,LOW);
          digitalWrite(M2ENABLE,LOW);
        }
     }
     
  }
  //Delay para pruebas y depuracion.Borrar o reducir
  delay(100);
}

/**
 * Funcion del arduino encargada de configurar los pines de los motores en el estado de detencion (Stop)
 * Este estado se consigue estableciendo a LOW todos y cada uno de los pines dedicados a los 2 motores del arduino
 * Su frecuente uso es para evitar cortocircuitos en las conexiones electronicas a los motores
 */
void frenado(){
      
  digitalWrite(M1AVANCE,LOW);
  digitalWrite(M1RETROCESO,LOW);
  digitalWrite(M2AVANCE,LOW);
  digitalWrite(M2RETROCESO,LOW);
  delay(10);
}

/**
 * Funcion encargada de configurar los pines de los motores para el avance del vehiculo
 * Solo activa los pines necesarios para el avance, ya que se asume que todos los pines estan en LOW, previo a la llamada a esta
 */
void avance(){

    digitalWrite(M1AVANCE,HIGH);
    digitalWrite(M2AVANCE,HIGH);
}


/**
 * Funcion encargada de configurar los pines de los motores para el retroceso del vehiculo
 * Solo activa los pines necesarios para el retroceso, ya que se asume que todos los pines estan en LOW, previo a la llamada a esta
 */
void retroceso(){

    digitalWrite(M1RETROCESO,HIGH);
    digitalWrite(M2RETROCESO,HIGH);
}

/**
 * Funcion encargada de configurar los pines de los motores para el giro a la izquierda del vehiculo
 * Solo activa los pines necesarios para el giro a la izquierda, ya que se asume que todos los pines estan en LOW, previo a la llamada a esta
 */
void giroIzquierda(){

    digitalWrite(M1RETROCESO,HIGH);
    digitalWrite(M2AVANCE,HIGH);
}

/**
 * Funcion encargada de configurar los pines de los motores para el giro a la derecha del vehiculo
 * Solo activa los pines necesarios para el giro a la derecha, ya que se asume que todos los pines estan en LOW, previo a la llamada a esta
 */
void giroDerecha(){

    digitalWrite(M1AVANCE,HIGH);
    digitalWrite(M2RETROCESO,HIGH);
}

/**
 * Funcion que usa el sensor ultrasonico para calcular la distancia del vehiculo con un posible obstaculo que haya frente al mismo.
 * 
 * Esta activa el pin del emisor del sensor ultrasonico(Trigger) por un pequeño periodo de tiempo. Al activarlo a HIGH, el pin receptor (Echo) 
 * tambien cambia a estado HIGH, indicando que esta recibiendo la onda sonica del sensor(cambiara a LOW una vez deje de recibir señales). 
 * Tras cambiar el pin Trigger a LOW, mediremos el tiempo que el pin Echo estuvo en HIGH en microsegundos y junto con la velocidad de la onda sonica (340 m/s)
 * y el delay que el pin Trigger estuvo activo calculamos la distancia del vehiculo al objeto mas proximo
 */

float uSensor(){


  //Activamos el pin emisor de la onda sonica(Trigger)
  digitalWrite(TRIGGER,HIGH);
  
  delayMicroseconds(10);//delay de funcionamiento del pin emisor
  //Desactivamos el pin emisor
  digitalWrite(TRIGGER,LOW);
  
  long timer=pulseIn(ECHO,HIGH);//devuelve el tiempo que ha tardado en cambiar de LOW a HIGH
   
  //ecuacion de calculo de la distancia:
  //(tiempo de viaje de la onda)/2 x velocidad de la onda == distancia
  return((timer/2)*0.0343);

 }
