package com.saulo.arthur.cafeteria.Classes_Banco;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Usuario implements Serializable {
    @SerializedName("id")
    int id;
    @SerializedName("username")
    String username;
    @SerializedName("password")
    String password;
    @SerializedName("nome")
    String nome;

    public Usuario(String username, String passwd, String nome)
    {
        this.id = -1;
        this.username = username;
        this.password = passwd;
        this.nome = nome;
    }
}
