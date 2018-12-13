package com.saulo.arthur.cafeteria;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.saulo.arthur.cafeteria.Classes_Banco.Produto;
import com.saulo.arthur.cafeteria.Classes_Banco.Reserva;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsarMesa extends AppCompatActivity {

    ListView lst_mesas_livres_sem_reserva;
    List<Mesa> mesas_livres;
    ArrayAdapter<Mesa> mesaArrayAdapter;

    String username;
    Mesa mesa;

    ComunicacaoRest rest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usar_mesa);

        username = getIntent().getExtras().getString("username");
        String json_mesas = getIntent().getExtras().getString("mesas_livres");
        mesas_livres = get_mesas_from_string(json_mesas);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://arthurmteodoro.pythonanywhere.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        rest = retrofit.create(ComunicacaoRest.class);

        lst_mesas_livres_sem_reserva = (ListView) findViewById(R.id.lst_mesas_sem_reserva);
        mesaArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mesas_livres);

        lst_mesas_livres_sem_reserva.setAdapter(mesaArrayAdapter);

        lst_mesas_livres_sem_reserva.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mesa = mesas_livres.get(position);

                AlertDialog alerta = cria_alerta_reserva("Usar Mesa", "Você realmente deseja utilizar a Mesa "+mesa.id+"?");
                alerta.show();
            }
        });
    }

    public AlertDialog cria_alerta_reserva(String titulo, String texto)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(texto);
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<JsonObject> call = rest.usar_mesa(mesa);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.d("REST", response.body().toString());
                        int ok = response.body().get("result").getAsInt();
                        if(ok == 0)
                        {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String data = dateFormat.format(new Date());
                            final Comanda comanda = new Comanda(data, username, mesa.id);
                            Call<JsonObject> call1 = rest.criar_comanda(comanda);
                            call1.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    int ok = response.body().get("result").getAsInt();
                                    if(ok == 0)
                                    {
                                        comanda.id = response.body().get("value").getAsJsonObject().get("id").getAsInt();
                                        Log.d("ASD", comanda.toString());

                                        List<Produto> produtos = new ArrayList<>();
                                        Intent nova_tela = new Intent(getApplicationContext(), ControleComanda.class);
                                        nova_tela.putExtra("username", username);
                                        nova_tela.putExtra("comanda", comanda);
                                        startActivity(nova_tela);
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Algo de errado ocorreu", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Algo de errado ocorreu", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Algo de errado ocorreu", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Algo de errado ocorreu", Toast.LENGTH_SHORT).show();
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

    public List<Mesa> get_mesas_from_string(String json_mesas)
    {
        JsonParser parser = new JsonParser();
        JsonElement tradeParser = parser.parse(json_mesas);
        JsonArray mesas_json = tradeParser.getAsJsonArray();
        List<Mesa> mesas = new ArrayList<>();
        for(JsonElement o : mesas_json)
        {
            int id = o.getAsJsonObject().get("id").getAsInt();
            int qtd_lugar = o.getAsJsonObject().get("qtd_lugar").getAsInt();
            int status = o.getAsJsonObject().get("status").getAsInt();
            Mesa mesa = new Mesa(qtd_lugar, status);
            mesa.id = id;
            mesas.add(mesa);
        }

        return mesas;
    }
}
