#include <Arduino.h>
#include <ESP8266WiFi.h>

const char* ssid = "ESP8266"; // nome da rede wifi
const char* password = "ESP8266Test"; // senha da rede wifi criada

WiFiServer server(6565);

IPAddress ip(192, 168, 0, 80);
IPAddress gateway(192,168,0,1);
IPAddress subnet(255,255,255,0);

WiFiClient mesa;

void setup() {
    Serial.begin(115200);
    Serial.print("Configuring WiFi access point...");

    WiFi.softAPConfig(ip, gateway, subnet);
    boolean result = WiFi.softAP(ssid, password);
    if(result==true)
    {
        Serial.println("done!");
    }
    else
    {
        Serial.println("error! Something went wrong...");
    }

    server.begin();
    Serial.print("Ip Local: "); Serial.println(WiFi.softAPIP().toString());
}

void loop() {
    WiFiClient client = server.available();
    if(client)
    {
        if(client.connected())
        {
            mesa = client;
        }
    }

    if(Serial.available() > 0)
    {
        String send_client = Serial.readStringUntil('\n');
        mesa.print(send_client);
    }
}
