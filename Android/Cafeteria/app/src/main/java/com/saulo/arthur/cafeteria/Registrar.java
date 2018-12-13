package com.saulo.arthur.cafeteria;

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
import com.saulo.arthur.cafeteria.Classes_Banco.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Registrar extends AppCompatActivity {

    ComunicacaoRest rest;

    EditText edt_user_registrar;
    EditText edt_nome_registrar;
    EditText edt_senha_registrar;
    Button btn_realizar_cadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://arthurmteodoro.pythonanywhere.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        edt_nome_registrar = findViewById(R.id.edt_nome_registrar);
        edt_user_registrar = findViewById(R.id.edt_user_registrar);
        edt_senha_registrar = findViewById(R.id.edt_senha_registrar);
        btn_realizar_cadastro = findViewById(R.id.btn_realizar_registro);

        rest = retrofit.create(ComunicacaoRest.class);
    }

    void OnClick_btn_realizar_registro(View view)
    {
        String username = edt_user_registrar.getText().toString();
        String senha = edt_senha_registrar.getText().toString();
        String nome = edt_nome_registrar.getText().toString();

        Usuario user = new Usuario(username, senha, nome);

        Call<JsonObject> call = rest.registrar(user);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                int code = response.body().get("response").getAsJsonObject().get("status").getAsInt();
                Log.d("REST", String.valueOf(code));
                if(code == 0)
                {
                    Toast.makeText(getApplicationContext(), "Usuário criado com sucesso!", Toast.LENGTH_SHORT).show();
                }
                else if(code == 1062)
                {
                    Toast.makeText(getApplicationContext(), "Nome de usuário já criado, digite outro por favor!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Algo de errado aconteceu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
