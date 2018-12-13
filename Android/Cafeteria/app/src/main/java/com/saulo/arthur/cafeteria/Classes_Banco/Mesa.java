package com.saulo.arthur.cafeteria.Classes_Banco;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Mesa implements Serializable {
    @SerializedName("id")
    public int id;
    @SerializedName("qtd_lugar")
    public int qtd_lugar;
    @SerializedName("status")
    public int status;

    public Mesa(int qtd_lugar, int status)
    {
        this.id = -1;
        this.qtd_lugar = qtd_lugar;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Mesa "+this.id+" - Lugares: "+qtd_lugar;
    }
}
