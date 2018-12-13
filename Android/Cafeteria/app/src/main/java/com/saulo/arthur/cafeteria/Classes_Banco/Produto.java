package com.saulo.arthur.cafeteria.Classes_Banco;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Produto implements Serializable {

    @SerializedName("id")
    public int id;
    @SerializedName("nome")
    public String nome;
    @SerializedName("descricao")
    public String descricao;
    @SerializedName("valor")
    public double valor;

    public Produto(String nome, String descricao, double valor)
    {
        this.id = -1;
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
    }

    public String toString()
    {
        return "Nome: "+this.nome+"   Descrição: "+this.descricao+"   Valor: "+this.valor;
    }
}
