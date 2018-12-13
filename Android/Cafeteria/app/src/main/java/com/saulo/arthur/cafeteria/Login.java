package com.saulo.arthur.cafeteria;

import android.app.Application;
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
import com.saulo.arthur.cafeteria.Classes_Banco.Usuario;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    ComunicacaoRest rest;

    Button btn_login;
    Button btn_registrar;
    EditText edt_username;
    EditText edt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://arthurmteodoro.pythonanywhere.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_registrar = (Button) findViewById(R.id.btn_registrar);
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_password = (EditText) findViewById(R.id.edt_senha);

        rest = retrofit.create(ComunicacaoRest.class);

    }

    public void onClick_btn_registar(View view)
    {
        Intent nova_tela = new Intent(this, Registrar.class);
        startActivity(nova_tela);
    }

    public void onClick_btn_login(View view)
    {
        final String username = edt_username.getText().toString();
        String senha = edt_password.getText().toString();
        Usuario usuario = new Usuario(username, senha, "");
        Call<JsonObject> call = rest.login(usuario);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String resposta = response.body().get("response").getAsJsonObject().get("status").getAsString();
                if(resposta.equals("user dont exist"))
                {
                    Toast.makeText(getApplicationContext(), "O usuário digitado não existe!", Toast.LENGTH_LONG).show();
                }
                else if(resposta.equals("username and password dont match"))
                {
                    Toast.makeText(getApplicationContext(), "O usuário digitado e a senha não conhecidem", Toast.LENGTH_LONG).show();
                }
                else if(resposta.equals("ok"))
                {
                    Intent nova_tela = new Intent(getApplicationContext(), MainJaLogado.class);
                    nova_tela.putExtra("username", username);
                    startActivity(nova_tela);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ocorreu algum erro", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
