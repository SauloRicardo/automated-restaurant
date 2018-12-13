package com.saulo.arthur.cafeteria;

import com.google.gson.JsonObject;
import com.saulo.arthur.cafeteria.Classes_Banco.Comanda;
import com.saulo.arthur.cafeteria.Classes_Banco.Mesa;
import com.saulo.arthur.cafeteria.Classes_Banco.Pedido;
import com.saulo.arthur.cafeteria.Classes_Banco.Reserva;
import com.saulo.arthur.cafeteria.Classes_Banco.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface ComunicacaoRest {
    @POST("/api/usuario/login")
    Call<JsonObject> login(@Body Usuario usuario);

    @POST("/api/usuario/registrar")
    Call<JsonObject> registrar(@Body Usuario usuario);

    @GET("/api/mesas/get_livres")
    Call<JsonObject> mesas_livres();

    @POST("/api/mesas/reservar")
    Call<JsonObject> reservar_mesa(@Body Reserva reserva);

    @POST("/api/mesas/usar_mesa")
    Call<JsonObject> usar_mesa(@Body Mesa mesa);

    @POST("/api/comanda/criar")
    Call<JsonObject> criar_comanda(@Body Comanda comanda);

    @GET("/api/produtos/get_produtos")
    Call<JsonObject> get_produtos();

    @POST("/api/comanda/get_pedidos")
    Call<JsonObject> get_pedidos_comanda(@Body Comanda comanda);

    @POST("/api/comanda/pedir_produto")
    Call<JsonObject> fazer_pedido(@Body Pedido pedido);

    @POST("/api/comanda/fechar")
    Call<JsonObject> fechar_comanda(@Body Comanda comanda);

    @POST("/api/comanda/consultar_comandas")
    Call<JsonObject> listar_comandas(@Body Usuario usuario);
}
