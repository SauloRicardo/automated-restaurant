package com.saulo.arthur.cafeteria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.saulo.arthur.cafeteria.Classes_Banco.Mesa;
import com.saulo.arthur.cafeteria.Classes_Banco.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainJaLogado extends AppCompatActivity {

    Button btn_reserva;
    ComunicacaoRest rest;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ja_logado);

        username = getIntent().getExtras().getString("username");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://arthurmteodoro.pythonanywhere.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        btn_reserva = findViewById(R.id.btn_reserva);

        rest = retrofit.create(ComunicacaoRest.class);
    }

    public void onClick_btn_reserva(View view)
    {
        Call<JsonObject> call = rest.mesas_livres();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonArray mesas_json = response.body().get("response").getAsJsonArray();
                Intent nova_tela = new Intent(getApplicationContext(), ReservarMesa.class);
                nova_tela.putExtra("username", username);
                nova_tela.putExtra("mesas_livres", mesas_json.toString());
                startActivity(nova_tela);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Algo de errado aconteceu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick_btn_usar_mesa(View view)
    {
        Call<JsonObject> call = rest.mesas_livres();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonArray mesas_json = response.body().get("response").getAsJsonArray();
                Intent nova_tela = new Intent(getApplicationContext(), UsarMesa.class);
                nova_tela.putExtra("username", username);
                nova_tela.putExtra("mesas_livres", mesas_json.toString());
                startActivity(nova_tela);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Algo de errado aconteceu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick_btn_ver_comandas(View view)
    {
        Usuario user = new Usuario(username, "", "");
        Call<JsonObject> call = rest.listar_comandas(user);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonArray resposta = response.body().getAsJsonArray("response");
                Intent nova_tela = new Intent(getApplicationContext(), ListagemComandas.class);
                nova_tela.putExtra("json_comandas", resposta.toString());
                startActivity(nova_tela);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Algo de errado aconteceu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
