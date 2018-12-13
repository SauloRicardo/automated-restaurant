package com.saulo.arthur.cafeteria.Classes_Banco;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Pedido implements Serializable {
    @SerializedName("produto")
    public int produto;
    @SerializedName("comanda")
    public int comanda;

    public Pedido(int produto, int comanda)
    {
        this.produto = produto;
        this.comanda = comanda;
    }
}
