package com.saulo.arthur.cafeteria;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saulo.arthur.cafeteria.Classes_Banco.Comanda;
import com.saulo.arthur.cafeteria.Classes_Banco.Mesa;
import com.saulo.arthur.cafeteria.Classes_Banco.Produto;
import com.saulo.arthur.cafeteria.Classes_Banco.Reserva;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ControleComanda extends AppCompatActivity {

    ListView lst_comanda;
    ArrayAdapter<Produto> produtoArrayAdapter;
    Button btn_novo_pedido;
    Button btn_fechar_comanda;

    TextView edt_total;

    List<Produto> produtos;

    String username;
    Comanda comanda;

    ComunicacaoRest rest;

    String prod_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controle_comanda);

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
        lst_comanda = findViewById(R.id.lst_comanda);
        edt_total = findViewById(R.id.edt_total);

        Log.d("ASD", comanda.toString());
        edt_total.setText(String.format("Total: %s", String.valueOf(comanda.total)));

        get_string_do_produto(comanda);
    }

    public void onClick_btn_novo_pedido(View view)
    {
        Call<JsonObject> call = rest.get_produtos();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonArray produtos_json = response.body().get("response").getAsJsonArray();
                Intent nova_tela = new Intent(getApplicationContext(), PedirProduto.class);
                nova_tela.putExtra("comanda", comanda);
                nova_tela.putExtra("produtos", produtos_json.toString());
                startActivity(nova_tela);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Algo de errado aconteceu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick_btn_fechar_comanda(View view)
    {
        AlertDialog dialog = cria_alerta("Fechar Comanda", "Você deseja mesmo fechar esta comanda?");
        dialog.show();
    }

    public AlertDialog cria_alerta(String titulo, String texto)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(texto);
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<JsonObject> call = rest.fechar_comanda(comanda);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        int ok = response.body().get("response").getAsInt();
                        if(ok == 0)
                        {
                            Intent nova_tela = new Intent(getApplicationContext(), MainJaLogado.class);
                            nova_tela.putExtra("username", username);
                            startActivity(nova_tela);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Algo de errado ocorreu!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Algo de errado ocorreu!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });

        return builder.create();
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
                    comanda.total += prod.valor;

                    produtos.add(prod);
                }

                if(produtos.size() > 0) {
                    produtoArrayAdapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, produtos);
                    lst_comanda.setAdapter(produtoArrayAdapter);
                    edt_total.setText(String.format("Total: %s", String.valueOf(comanda.total)));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Algo de errado aconteceu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
