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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saulo.arthur.cafeteria.Classes_Banco.Mesa;
import com.saulo.arthur.cafeteria.Classes_Banco.Reserva;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReservarMesa extends AppCompatActivity {

    ListView lst_mesas_livres;
    List<Mesa> mesas_livres;
    ArrayAdapter<Mesa> mesaArrayAdapter;

    String username;
    Mesa mesa;

    ComunicacaoRest rest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_mesa);

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

        lst_mesas_livres = (ListView) findViewById(R.id.lst_mesas_livres);
        mesaArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mesas_livres);

        lst_mesas_livres.setAdapter(mesaArrayAdapter);

        lst_mesas_livres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mesa = mesas_livres.get(position);

                AlertDialog alerta = cria_alerta_reserva("Reserva", "Você realmente deseja reservar a Mesa "+mesa.id+"?");
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
                Reserva reserva = new Reserva(mesa.id, username);
                Call<JsonObject> call = rest.reservar_mesa(reserva);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.d("REST", response.body().toString());
                        int ok = response.body().get("result").getAsInt();
                        if(ok == 0)
                        {
                            int cod_confirmacao = response.body().get("value").getAsJsonObject().get("cod_confirmacao").getAsInt();
                            Intent nova_tela = new Intent(getApplicationContext(), ConfirmacaoCodigoReserva.class);
                            nova_tela.putExtra("cod_confirmacao", cod_confirmacao);
                            nova_tela.putExtra("username", username);
                            nova_tela.putExtra("mesa_id", mesa.id);
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
