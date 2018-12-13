import requests
from Classes import *


class Server:
    _ENDPOINT_ALL_MESAS = "/api/mesas/get_todas"
    _ENDPOINT_USAR_MESA = "/api/mesas/usar_mesa"
    _ENDPOINT_CRIAR_COMANDA = "/api/comanda/criar"
    _ENDPOINT_GET_PRODUTOS = "/api/produtos/get_produtos"
    _ENDPOINT_GET_PEDIDOS = "/api/comanda/get_pedidos"
    _ENDPOINT_FAZER_PEDIDO = "/api/comanda/pedir_produto"
    _ENDPOINT_FECHAR_COMANDA = "/api/comanda/fechar"
    _ENDPOINT_ULTIMA_COMANDA_MESA = "/api/comanda/ultima_reserva_mesa"

    def __init__(self, url):
        self.__basic_url = url

    def get_all_mesas(self):
        r = requests.get(self.__basic_url+Server._ENDPOINT_ALL_MESAS)
        response_json = r.json()
        todas_mesas = []

        for i in response_json['response']:
            mesa = Mesa()
            mesa.id = i['id']
            mesa.qtd_lugar = i['qtd_lugar']
            mesa.status = i['status']
            todas_mesas.append(mesa)
        return todas_mesas

    def usar_mesa(self, mesa):
        r = requests.post(self.__basic_url+Server._ENDPOINT_USAR_MESA, json=mesa.serializable())
        response_json = r.json()
        return response_json['result']

    def criar_comanda(self, comanda):
        r = requests.post(self.__basic_url+Server._ENDPOINT_CRIAR_COMANDA, json=comanda.serializable())
        response_json = r.json()
        comanda.id = response_json['value']['id']

    def get_produtos(self):
        r = requests.get(self.__basic_url+Server._ENDPOINT_GET_PRODUTOS)
        response_json = r.json()
        produtos = []

        for i in response_json['response']:
            produto = Produto("", "", 0)
            produto.id = i['id']
            produto.nome = i['nome']
            produto.descricao = i['descricao']
            produto.valor = i['valor']
            produtos.append(produto)

        return produtos

    def get_pedidos_comanda(self, comanda):
        r = requests.post(self.__basic_url+Server._ENDPOINT_GET_PEDIDOS, json=comanda.serializable())
        response_json = r.json()
        produtos = []

        for i in response_json['response']:
            produto = Produto("", "", 0)
            produto.id = i['id']
            produto.nome = i['nome']
            produto.descricao = i['descricao']
            produto.valor = i['valor']
            produtos.append(produto)

        return produtos

    def fazer_pedido(self, pedido):
        r = requests.post(self.__basic_url+Server._ENDPOINT_FAZER_PEDIDO, json=pedido.serializable())
        response_json = r.json()

        return response_json['response']

    def fechar_comanda(self, comanda):
        r = requests.post(self.__basic_url+Server._ENDPOINT_FECHAR_COMANDA, json=comanda.serializable())
        response_json = r.json()

        return response_json['response']

    def ultima_reserva_mesa(self, mesa):
        r = requests.post(self.__basic_url+Server._ENDPOINT_ULTIMA_COMANDA_MESA, json=mesa.serializable())
        response_json = r.json()['response']

        reserva = Reserva(response_json['dia_inicio'], response_json['hora_inicio'], response_json['dia_fim'],
                          response_json['hora_fim'], response_json['mesa'], response_json['username'])
        reserva.cod_confirmacao = response_json['cod_confirmacao']
        reserva.id = response_json['id']

        return reserva


if __name__ == '__main__':
    server = Server('http://arthurmteodoro.pythonanywhere.com')
    mesas = server.get_all_mesas()
    # server.usar_mesa(mesas[0])
    produtos = server.get_produtos()
    for i in produtos:
        print(i.serializable())
    print('---------------------')
    comanda = Comanda('22-11-2018', 'A', 1)
    comanda.id = 2
    a = server.get_pedidos_comanda(comanda)
    for i in a:
        print(i.serializable())
    print('---------------------')
    reserva = server.ultima_reserva_mesa(mesas[0])
    print(reserva.serializable())
