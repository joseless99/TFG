/**
 * Sketch de prueba de comunicacion con el modulo bluetooth.
 * En este sketch se prueba un caso de comunicacion bidireccional, similar a como se implementa en el sketch final del TFG
 */

bool state=false;//Refleja el estado de la comunicacion
int i=0;
char data;
void setup() {
  Serial.begin(9600);
  Serial1.begin(9600);
}

void loop() {
  //Cuando no haya comunicacion se verifica si se ha intentado establecer, viendo si se recibe '0'
  if(Serial1.available()>0 && Serial1.read()=='0'){
    state=true;
  }else{
    state=false;
  }

  //Accion si se ha establecido comunicacion establecida
  if(state){
    if(Serial1.available()>0){
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
    
    //Enviamos el valor del contador i
    Serial.print("Informacion enviada: ");
    Serial.println(i);
    Serial1.println(i);
    i++;
    
  }else{
    Serial.println("No se ha recibido ningun comando");
    Serial.println("No se ha enviado ningun comando");
  }
  delay(1000);
}
