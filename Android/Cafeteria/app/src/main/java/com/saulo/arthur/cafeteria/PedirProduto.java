package com.saulo.arthur.cafeteria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saulo.arthur.cafeteria.Classes_Banco.Comanda;
import com.saulo.arthur.cafeteria.Classes_Banco.Mesa;
import com.saulo.arthur.cafeteria.Classes_Banco.Pedido;
import com.saulo.arthur.cafeteria.Classes_Banco.Produto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PedirProduto extends AppCompatActivity {

    List<Produto> produtos;
    String username;
    Comanda comanda;

    EditText edt_qtd_produto;

    ListView lst_produtos_pedir;
    ArrayAdapter<Produto> produtoArrayAdapter;

    Produto produto_escolhido = null;

    boolean produto_ja_escolhido;

    ComunicacaoRest rest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedir_produto);

        String produtos_json = getIntent().getExtras().getString("produtos");
        username = getIntent().getExtras().getString("username");
        comanda = (Comanda) getIntent().getExtras().get("comanda");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://arthurmteodoro.pythonanywhere.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        rest = retrofit.create(ComunicacaoRest.class);

        produto_ja_escolhido = false;

        edt_qtd_produto = findViewById(R.id.edt_qtd_produto);

        Log.d("QWE", produtos_json);
        produtos = produtos_from_json(produtos_json);

        lst_produtos_pedir = findViewById(R.id.lst_produtos_pedir);
        produtoArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, produtos);

        lst_produtos_pedir.setAdapter(produtoArrayAdapter);

        lst_produtos_pedir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                produto_escolhido = produtos.get(position);
                List<Produto> lista_produtos = new ArrayList<>();
                lista_produtos.add(produto_escolhido);

                produtoArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, lista_produtos);
                lst_produtos_pedir.setAdapter(produtoArrayAdapter);

                produto_ja_escolhido = true;
            }
        });

    }

    public void onClick_btn_cancelar_pedido(View view)
    {
        if(produto_ja_escolhido)
        {
            produtoArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, produtos);
            lst_produtos_pedir.setAdapter(produtoArrayAdapter);

            produto_escolhido = null;
            produto_ja_escolhido = false;
        }
        else
        {
            Intent nova_tela = new Intent(getApplicationContext(), ControleComanda.class);
            nova_tela.putExtra("username", username);
            nova_tela.putExtra("comanda", comanda);
            startActivity(nova_tela);
        }
    }

    public void onClick_btn_pedir(View view)
    {
        if(produto_ja_escolhido)
        {
            int qtd_prod = Integer.parseInt(edt_qtd_produto.getText().toString());
            for(int i = 0; i < qtd_prod; i++)
            {
                Pedido pedido = new Pedido(produto_escolhido.id, comanda.id);
                Log.d("ASD", "Produto: "+produto_escolhido.id+" Comanda: "+comanda.id);
                Call<JsonObject> call = rest.fazer_pedido(pedido);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.d("REST", String.valueOf(response.code()));
                        int ok = response.body().get("response").getAsInt();
                        if(ok == -1)
                        {
                            Toast.makeText(getApplicationContext(), "Algo de errado aconteceu!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Log.d("DEBUG", "Valor da comanda: "+comanda.total);
                            comanda.total += produto_escolhido.valor;
                            Log.d("DEBUG", "Valor da comanda: "+comanda.total+" Valor do Produto: "+produto_escolhido.valor);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Algo de errado aconteceu!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            Intent nova_tela = new Intent(getApplicationContext(), ControleComanda.class);
            nova_tela.putExtra("username", username);
            nova_tela.putExtra("comanda", comanda);
            startActivity(nova_tela);
        }
    }

    List<Produto> produtos_from_json(String json)
    {
        JsonParser parser = new JsonParser();
        JsonElement tradeParser = parser.parse(json);
        JsonArray produtos_json = tradeParser.getAsJsonArray();
        List<Produto> produtos = new ArrayList<>();
        for(JsonElement o : produtos_json)
        {
            int id = o.getAsJsonObject().get("id").getAsInt();
            String nome = o.getAsJsonObject().get("nome").getAsString();
            String descricao = o.getAsJsonObject().get("descricao").getAsString();
            double valor = Double.parseDouble(o.getAsJsonObject().get("valor").getAsString());

            Produto prod = new Produto(nome, descricao, valor);
            prod.id = id;

            produtos.add(prod);
        }
        return produtos;
    }
}
