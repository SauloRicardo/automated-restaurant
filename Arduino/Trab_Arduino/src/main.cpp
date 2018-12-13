#include <Arduino.h>
#include <Wire.h>
#include <LCD5110_Basic.h>

LCD5110 tela(8, 9, 10, 12, 11);

extern uint8_t SmallFont[];
extern uint8_t MediumNumbers[];
extern uint8_t BigNumbers[];

void receiveEvent(int howMany)
{
    String s = "";
    while (0 <Wire.available())
    {
       char c = Wire.read();      /* receive byte as a character */
       s.concat(c);
       Serial.print(c);           /* print the character */
    }
    tela.clrScr();
    tela.setFont(SmallFont);
    tela.print(s, CENTER, 20);

    float temperatura = (float(analogRead(A0))*5/(1023))/0.01;
    String temp_str = String(temperatura);
    tela.print(temp_str, RIGHT, 40);
    Serial.println();             /* to newline */
}

void requestEvent()
{
    Wire.write("Hello NodeMCU");
}

void setup() {
    Wire.begin(8);
    Wire.onReceive(receiveEvent);
    Wire.onRequest(requestEvent);
    Serial.begin(9600);
    tela.InitLCD();
}

void loop() {
    delay(1000);
}
