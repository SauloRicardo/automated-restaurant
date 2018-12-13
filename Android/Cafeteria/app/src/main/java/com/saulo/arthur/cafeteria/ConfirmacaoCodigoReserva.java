package com.saulo.arthur.cafeteria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.saulo.arthur.cafeteria.Classes_Banco.Comanda;
import com.saulo.arthur.cafeteria.Classes_Banco.Mesa;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfirmacaoCodigoReserva extends AppCompatActivity {

    EditText edt_reserva_confimacao;
    Button btn_validar_reserva;

    int codigo_esperado;
    int mesa_id;
    String username;

    ComunicacaoRest rest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_codigo_reserva);

        edt_reserva_confimacao = findViewById(R.id.edt_reserva_confimacao);
        btn_validar_reserva = findViewById(R.id.btn_validar_reserva);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://arthurmteodoro.pythonanywhere.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        rest = retrofit.create(ComunicacaoRest.class);

        codigo_esperado = getIntent().getExtras().getInt("cod_confirmacao");
        mesa_id = getIntent().getExtras().getInt("mesa_id");
        username = getIntent().getExtras().getString("username");
    }

    public void onClick_btn_validar_reserva(View view)
    {
        String valor_digitado = edt_reserva_confimacao.getText().toString();
        valor_digitado = valor_digitado.trim();

        if(codigo_esperado == Integer.valueOf(valor_digitado))
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String data = dateFormat.format(new Date());
            final Comanda comanda = new Comanda(data, username, mesa_id);
            Call<JsonObject> call1 = rest.criar_comanda(comanda);
            call1.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    int ok = response.body().get("result").getAsInt();
                    if(ok == 0)
                    {
                        comanda.id = response.body().get("value").getAsJsonObject().get("id").getAsInt();
                        Log.d("ASD", comanda.toString());

                        Mesa mesa = new Mesa(0, 0);
                        mesa.id = mesa_id;
                        Call<JsonObject> call2 = rest.usar_mesa(mesa);
                        call2.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                Log.d("REST", "DEU BOM");
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "Algo de errado ocorreu", Toast.LENGTH_SHORT).show();
                            }
                        });

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
            Toast.makeText(getApplicationContext(), "Código incorreto", Toast.LENGTH_SHORT).show();
        }
    }
}
