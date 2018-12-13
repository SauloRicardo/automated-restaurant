package com.saulo.arthur.cafeteria.Classes_Banco;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Comanda implements Serializable {
    @SerializedName("id")
    public int id;
    @SerializedName("data")
    public String data;
    @SerializedName("usuario")
    public String usuario;
    @SerializedName("mesa")
    public int mesa;
    @SerializedName("total")
    public double total;

    public Comanda(String data, String username, int mesa)
    {
        this.id = -1;
        this.data = data;
        this.usuario = username;
        this.mesa = mesa;
        this.total = 0;
    }

    @Override
    public String toString() {
        return  "Data: "+this.data+" Mesa: "+this.mesa+" Total: "+this.total;
    }
}
