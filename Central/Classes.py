class Usuario:
    def __init__(self, username, password, nome):
        self.id = -1
        self.username = username
        self.password = password
        self.nome = nome


class Produto:
    def __init__(self, nome, descricao, valor):
        self.id = -1
        self.nome = nome
        self.descricao = descricao
        self.valor = valor

    def serializable(self):
        return {
            "id": self.id,
            "nome": self.nome,
            "descricao": self.descricao,
            "valor": self.valor
        }

    def __str__(self):
        return "Nome: {name} Descrição: {desc} Valor: {valor}".format(name=self.nome, desc=self.descricao,
                                                                      valor=self.valor)


class Reserva:
    def __init__(self, dia_inicio, hora_inicio, dia_fim, hora_fim, mesa, usuario):
        self.id = -1
        self.dia_inicio = dia_inicio
        self.hora_inicio = hora_inicio
        self.dia_fim = dia_fim
        self.hora_fim = hora_fim
        self.mesa = mesa
        self.usuario = None
        self.username = usuario
        self.concluida = 0
        self.cod_confirmacao = ""

    def serializable(self):
        return {
            'id': self.id,
            'dia_inicio': self.dia_inicio,
            'hora_inicio': self.hora_inicio,
            'dia_fim': self.dia_fim,
            'hora_fim': self.hora_fim,
            'mesa': self.mesa,
            'username': self.username,
            'cod_confirmacao': self.cod_confirmacao
        }


class Comanda:
    def __init__(self, data, usuario, mesa):
        self.id = -1
        self.data = data
        self.usuario = usuario
        self.mesa = mesa
        self.total = 0

    def serializable(self):
        return {
            "id": self.id,
            "data": self.data,
            "usuario": self.usuario,
            "mesa": self.mesa,
            "total": self.total
        }

    def __str__(self):
        return "Comanda: {id}, {username}, {mesa}".format(id=self.id, username=self.usuario, mesa=self.mesa)


class Pedido(object):
    def __init__(self, comanda, produto):
        self.comanda = comanda
        self.produto = produto

    def serializable(self):
        return {
            "comanda": self.comanda,
            "produto": self.produto
        }


class Mesa(object):
    DISPONIVEL = 0
    RESERVADA = 1
    OCUPADA = 2

    def __init__(self):
        self.id = -1
        self.qtd_lugar = 0
        self.status = 0

    def serializable(self):
        return {
            'id': self.id,
            'qtd_lugar': self.qtd_lugar,
            'status': self.status
        }

    def __str__(self):
        return "Mesa {id}".format(id=self.id)
