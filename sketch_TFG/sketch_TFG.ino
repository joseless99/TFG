/**
 * Este es el sketch a cargar el en arduino del coche.
 * Este tendra conectados 2 motores de 5V DC ademas de un modulo bluetooth, por el cual se 
 * reciben instrucciones de acciones a realizar. El emisor de las instrucciones es una aplicacion
 * Android desarrollada para este proyecto.
 * 
 * @author: Juan Jose Ropero Cerro
 * 
 */
#define M1AVANCE 8
#define M1RETROCESO 9
#define M2AVANCE 10
#define M2RETROCESO 11

char dato;//Variable que almacena los datos que se leen del modulo bluetooth
void setup() {
 //Iniciamos monitores a usar.Serial es solo usado para verificar el envio y recepcion de datos correcto
  Serial.begin(9600);//Deshabilitar para la version final
  Serial1.begin(9600);
  
  //Configracion inical de los pines de los motores y de variables globales a usar
  pinMode(M1AVANCE,OUTPUT);
  pinMode(M1RETROCESO,OUTPUT);
  pinMode(M2AVANCE,OUTPUT);
  pinMode(M2RETROCESO,OUTPUT);

  digitalWrite(M1AVANCE,LOW);
  digitalWrite(M1RETROCESO,LOW);
  digitalWrite(M2AVANCE,LOW);
  digitalWrite(M2RETROCESO,LOW);

  dato=' ';
  
}
void loop() {
  //Verificamos si existen datos recibidos por el canal de comunicacion en el que esta el modulo bluetooth
  if(Serial1.available()>0){
      dato=Serial1.read();//Guardamos los datos del canal de comunicacion BT en la variable dato
      //Usado en pruebas para verificar correcta recepcion de mensajes
      Serial.write(dato);//Deshabilitar para version final
    //Comparamos los datos recibidos del monitor Serial1 con las posibles acciones a ejecutar
    if(dato=='F'){//Avance del vehiculo
     //Previa detencion del vehiculo para el la correcta configuracion final    
       motorStop();
       delay(50);
      //Activamos los pines de cada motor para el avanze del vehiculo
      digitalWrite(M1AVANCE,HIGH);
      digitalWrite(M2AVANCE,HIGH);
    }else if(dato=='B'){//Retroceso del vehiculo
      //Previa detencion del vehiculo para el la correcta configuracion final    
      motorStop();
      delay(50);
      //Activamos los pines de cada motor para el retroceso del vehiculo
      digitalWrite(M1RETROCESO,HIGH);
      digitalWrite(M2RETROCESO,HIGH);

    }else if(dato=='R'){//Giro a la derecha del vehiculo
      //Previa detencion del vehiculo para el la correcta configuracion final    
      motorStop();
      //Activamos los pines de cada motor para el retroceso del vehiculo
      digitalWrite(M1AVANCE,HIGH);
      digitalWrite(M2RETROCESO,HIGH);
    }else if(dato=='L'){//Giro a la izquierda del vehiculo
      //Previa detencion del vehiculo para el la correcta configuracion final    
      motorStop();
      //Activamos los pines de cada motor para el retroceso del vehiculo
      digitalWrite(M1RETROCESO,HIGH);
      digitalWrite(M2AVANCE,HIGH);
    }else if(dato=='S'){//Detencion del vehiculo  
      //Solo basta con llamar a la funcion motoStop()                 
       motorStop();
       delay(50);
    }       
  }
}

/**
 * Funcion del arduino encargada de configurar los pines de los motores en el estado de Detencion (Stop)
 * Este estado se consigue estableciendo a LOW todos y cada uno de los pines dedicados a los 2 motores del arduino
 * Su frecuente uso es para evitar cortocircuitos en las conexiones electronicas a los motores
 */
void motorStop(){
      
  digitalWrite(M1AVANCE,LOW);
  digitalWrite(M1RETROCESO,LOW);
  digitalWrite(M2AVANCE,LOW);
  digitalWrite(M2RETROCESO,LOW);
}
