#include <Arduino.h>
#include <Wire.h>
#include <ESP8266WiFi.h>

const char* ssid = "ESP8266"; // nome da rede wifi
const char* password = "ESP8266Test"; // senha da rede wifi criada

IPAddress server(192,168,0,80);
WiFiClient client;

void setup() {
    Wire.begin(D1, D2);

    Serial.begin(115200);
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        Serial.print(".");
        delay(500);
    }

    Serial.print("Status: "); Serial.println(WiFi.status());    // Network parameters
    Serial.print("IP: ");     Serial.println(WiFi.localIP());
    Serial.print("Subnet: "); Serial.println(WiFi.subnetMask());
    Serial.print("Gateway: "); Serial.println(WiFi.gatewayIP());
    Serial.print("SSID: "); Serial.println(WiFi.SSID());
    Serial.print("Signal: "); Serial.println(WiFi.RSSI());

    while(!client.connect(server, 6565))
    {
        Serial.print("*");
    }
}

void loop() {
    if(client.available() > 0)
    {
        String value = client.readStringUntil('*');
        Serial.println(value);
        if(value.startsWith("ESCREVE-TELA: "))
        {
            value.trim();
            value.remove(0, 14);
            Wire.beginTransmission(8);
            Wire.write(value.c_str());
            Wire.endTransmission();
        }
    }
}
