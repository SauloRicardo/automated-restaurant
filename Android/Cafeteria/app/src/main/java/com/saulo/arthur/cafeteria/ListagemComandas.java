package com.saulo.arthur.cafeteria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.saulo.arthur.cafeteria.Classes_Banco.Comanda;
import com.saulo.arthur.cafeteria.Classes_Banco.Mesa;

import java.util.ArrayList;
import java.util.List;

public class ListagemComandas extends AppCompatActivity {

    ListView lst_comandas_passadas;
    ArrayAdapter<Comanda> comandaArrayAdapter;
    List<Comanda> comandas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_comandas);

        lst_comandas_passadas = findViewById(R.id.lst_comandas_passadas);
        String json = getIntent().getExtras().getString("json_comandas");
        trata_json_comandas(json);

        lst_comandas_passadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent nova_tela = new Intent(getApplicationContext(), DetalhesComanda.class);
                nova_tela.putExtra("comanda", comandas.get(position));
                startActivity(nova_tela);
            }
        });

    }

    void trata_json_comandas(String json)
    {
        JsonParser parser = new JsonParser();
        JsonElement tradeParser = parser.parse(json);
        JsonArray comanda_json = tradeParser.getAsJsonArray();

        comandas = new ArrayList<>();
        for(JsonElement o : comanda_json)
        {
            int id = o.getAsJsonObject().get("id").getAsInt();
            String data = o.getAsJsonObject().get("data").getAsString();
            String usuario = o.getAsJsonObject().get("usuario").getAsString();
            int mesa = o.getAsJsonObject().get("mesa").getAsInt();
            float total = o.getAsJsonObject().get("total").getAsFloat();

            Comanda comanda = new Comanda(data, usuario, mesa);
            comanda.id = id;
            comanda.total = total;
            comandas.add(comanda);
        }

        comandaArrayAdapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, comandas);
        lst_comandas_passadas.setAdapter(comandaArrayAdapter);
    }
}
