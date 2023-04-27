/**
 * Sketch de prueba de comunicacion con el modulo bluetooth.
 * En este sketch se prueba un caso de comunicacion bidireccional, similar a como se implementa en el sketch final del TFG
 */

bool state=false;//Refleja el estado de la comunicacion
int i=0;//Contador que enviamos 
char data;//Variable que guarda temporalmente datos leidos del Serial1

void setup() {
  Serial.begin(9600);
  Serial1.begin(9600);
}

void loop() {
  
  //Cuando no haya comunicacion se verifica si se ha intentado establecer, viendo si se recibe '0'
  if(Serial1.available()>0 && state==false){
    if(Serial1.read()=='0'){
      state=true;}
  }

  //Accion si se ha establecido comunicacion establecida
  if(state){
    if(Serial1.available()>0){
      
      //Leemos los datos y los imprimimos en el monitor del Arduino.
      data=Serial1.read();
      Serial.print("Instruccion recibida: ");
      Serial.write(data);
      Serial.println();
      
      if(data=='1'){
        state=false;
      }
      
    }else
    {
      Serial.println("No se ha recibido ningun comando");
    }
    
    //Enviamos el valor del contador i y lo imprimimos en el monitor de Arduino para verificacion de los datos
    Serial.print("Informacion enviada: ");
    Serial.println(i);
    Serial1.println(i);
    i++;
    
  }else{
    Serial.println("No se ha recibido ningun comando");
    Serial.println("No se ha enviado ningun comando");
  }
  
  delay(1000);//Delay para visualizar resulatdos de la prueba mejor
}
