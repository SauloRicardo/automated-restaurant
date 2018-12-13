package com.saulo.arthur.cafeteria;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.saulo.arthur.cafeteria.Classes_Banco.Comanda;
import com.saulo.arthur.cafeteria.Classes_Banco.Produto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetalhesComanda extends AppCompatActivity {

    ListView lst_produtos_detalhe;
    ArrayAdapter<Produto> produtoArrayAdapter;
    List<Produto> produtos;

    TextView lbl_total_detalhe;
    TextView lbl_mesa_detalhe;
    TextView lbl_data_detalhe;

    ComunicacaoRest rest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_comanda);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://arthurmteodoro.pythonanywhere.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        rest = retrofit.create(ComunicacaoRest.class);

        lst_produtos_detalhe = findViewById(R.id.lst_produtos_detalhe);
        lbl_data_detalhe = findViewById(R.id.lbl_data_detalhe);
        lbl_mesa_detalhe = findViewById(R.id.lbl_mesa_detalhe);
        lbl_total_detalhe = findViewById(R.id.lbl_total_detalhe);

        Comanda comanda = (Comanda) getIntent().getExtras().get("comanda");
        get_string_do_produto(comanda);
    }

    void get_string_do_produto(final Comanda comanda)
    {
        Call<JsonObject> call = rest.get_pedidos_comanda(comanda);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("REST", response.body().toString());
                JsonArray produto_json = response.body().get("response").getAsJsonArray();
                produtos = new ArrayList<>();

                for (JsonElement o : produto_json) {
                    int id = o.getAsJsonObject().get("id").getAsInt();
                    String nome = o.getAsJsonObject().get("nome").getAsString();
                    String descricao = o.getAsJsonObject().get("descricao").getAsString();
                    double valor = Double.parseDouble(o.getAsJsonObject().get("valor").getAsString());

                    Produto prod = new Produto(nome, descricao, valor);
                    prod.id = id;

                    produtos.add(prod);
                }

                produtoArrayAdapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, produtos);
                lst_produtos_detalhe.setAdapter(produtoArrayAdapter);

                lbl_data_detalhe.setText(String.format("Data: %s", comanda.data));
                lbl_mesa_detalhe.setText(String.format("Mesa: %d", comanda.mesa));
                lbl_total_detalhe.setText(String.format("Total: %s", comanda.total));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Algo de errado aconteceu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
