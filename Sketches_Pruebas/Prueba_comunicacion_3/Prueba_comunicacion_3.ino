char state='0';
int i=0;
char data;
void setup() {
  Serial.begin(9600);
  Serial1.begin(9600);
}

void loop() {
  if(Serial1.available()>0&&state=='0'){
    state=Serial1.read();
  }
  if(state=='1'){
    if(Serial1.available()>0){
      data=Serial1.read();
      Serial.print("Instruccion recibida: ");
      Serial.write(data);
      Serial.println();
      if(data=='0'){
        state='0';
      }
    }
    Serial.print("Informacion enviada: ");
    Serial.println(i);
    Serial1.println(i);
    i++;
  }else{
    if(Serial1.available()>0){
      Serial.print("Instruccion recibida: ");
      Serial.write(Serial1.read());
    }else
    {
      Serial.println("No se ha recibido ningun comando");
    }

    Serial.println("No se ha enviado ningun comando");
  }

  delay(1000);
}
