package com.saulo.arthur.cafeteria.Classes_Banco;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Reserva implements Serializable {
    @SerializedName("id")
    public int id;
    @SerializedName("dia_inicio")
    public String dia_inicio;
    @SerializedName("hora_inicio")
    public String hora_inicio;
    @SerializedName("dia_fim")
    public String dia_fim;
    @SerializedName("hora_fim")
    public String hora_fim;
    @SerializedName("mesa")
    public int mesa;
    @SerializedName("usuario")
    public int usuario;
    @SerializedName("username")
    public String username;
    @SerializedName("concluida")
    public boolean concluida;
    @SerializedName("cod_confirmacao")
    public String cod_confirmacao;

    public Reserva(int mesa, String usuario)
    {
        this.id = -1;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.dia_inicio = dateFormat.format(cal.getTime());

        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
        this.hora_inicio = hourFormat.format(cal.getTime());

        cal.add(Calendar.HOUR_OF_DAY, 2);
        this.dia_fim = dateFormat.format(cal.getTime());
        this.hora_fim = hourFormat.format(cal.getTime());

        this.mesa = mesa;
        this.usuario = -1;
        this.username = usuario;
        this.concluida = false;
        this.cod_confirmacao = "";
    }
}
