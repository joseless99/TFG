char state='0';
int i=0;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial1.begin(9600);
  while(Serial1.available()>0){
    Serial1.read();
  }
}

void loop() {
  // put your main code here, to run repeatedly:

    if(Serial1.available())
    {
      state=Serial1.read();
    }

    if(state=='1'){
 
      Serial1.println(i);
      Serial.print("Conectado\n");
      i++;
    }else
    {
      Serial.print("No conectado\n");

    while(Serial1.available()>0){
      Serial1.read();
    }
  }
    delay(500);
}
