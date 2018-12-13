import MySQLdb


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


class DataBase:
    def __init__(self):
        self.db = MySQLdb.connect(
            host="arthurmteodoro.mysql.pythonanywhere-services.com",
            user="arthurmteodoro",
            passwd="Arthur99041701",
            db="arthurmteodoro$cafeteria"
        )
        self.cursor = self.db.cursor()

    def get_usuarios(self):
        sql = 'select * from Usuario;'
        self.cursor.execute(sql)
        result = self.cursor.fetchall()
        return_value = []
        for i in result:
            usuario = Usuario(i[1], i[2], i[3])
            usuario.id = i[0]
            return_value.append(usuario)
        return return_value

    def add_usuario(self, usuario):
        try:
            all_users = self.get_usuarios()
            if len(all_users) > 0:
                last_id = all_users[-1].id
            else:
                last_id = -1
            values = (last_id + 1, usuario.username, usuario.password, usuario.nome)
            sql = 'insert into Usuario (use_id, use_username, use_password, use_nome) values ({id}, \'{user}\', ' \
                  '\'{pw}\', \'{nome}\');'.format(id=values[0], user=values[1], pw=values[2], nome=values[3])
            self.cursor.execute(sql)
            self.db.commit()
            return 0
        except MySQLdb.IntegrityError as err:
            return err.args[0]

    def get_usuario(self, username):
        try:
            sql = 'select * from Usuario where use_username = \'{user}\''.format(user=username)
            self.cursor.execute(sql)
            result = self.cursor.fetchone()
            if result is None:
                return None
            else:
                return_value = Usuario(result[1], result[2], result[3])
                return_value.id = result[0]
                return return_value
        except MySQLdb.IntegrityError as err:
            return err.args[0]

    def get_all_mesa(self):
        try:
            sql = 'select * from Mesa;'
            self.cursor.execute(sql)
            result = self.cursor.fetchall()
            mesas = []
            for i in result:
                mesa = Mesa()
                mesa.id = i[0]
                mesa.qtd_lugar = i[1]
                mesa.status = i[2]
                mesas.append(mesa)
            return mesas
        except MySQLdb.IntegrityError as err:
            return err.args[0]

    def get_mesas_livres(self):
        try:
            sql = 'select * from Mesa where mes_status = 0;'
            self.cursor.execute(sql)
            result = self.cursor.fetchall()
            mesas = []
            for i in result:
                mesa = Mesa()
                mesa.id = i[0]
                mesa.qtd_lugar = i[1]
                mesa.status = i[2]
                mesas.append(mesa)
            return mesas
        except MySQLdb.IntegrityError as err:
            return err.args[0]

    def reservar_mesa(self, mesa):
        try:
            sql = 'update Mesa set mes_status = 1 where mes_id = {mesa_id}'.format(mesa_id=mesa)
            self.cursor.execute(sql)
            self.db.commit()
            return 0
        except MySQLdb.IntegrityError as err:
            return err.args[0]

    def usar_mesa(self, mesa):
        try:
            sql = 'update Mesa set mes_status = 2 where mes_id = {mesa_id}'.format(mesa_id=mesa.id)
            self.cursor.execute(sql)
            self.db.commit()
            return 0
        except MySQLdb.IntegrityError as err:
            return err.args[0]

    def liberar_mesa(self, mesa):
        try:
            sql = 'update Mesa set mes_status = 0 where mes_id = {mesa_id}'.format(mesa_id=mesa.id)
            self.cursor.execute(sql)
            self.db.commit()
            return 0
        except MySQLdb.IntegrityError as err:
            return err.args[0]

    def get_all_reservas(self):
        sql = 'select * from Reserva;'
        self.cursor.execute(sql)
        result = self.cursor.fetchall()
        return_value = []
        for i in result:
            reserva = Reserva(i[1], i[2], i[4], i[3], i[6], i[5])
            reserva.id = i[0]
            reserva.concluida = i[7]
            reserva.cod_confirmacao = i[8]
            return_value.append(reserva)
        return return_value

    def add_reserva(self, reserva):
        try:
            reservas = self.get_all_reservas()
            if len(reservas) > 0:
                last_id = reservas[-1].id
            else:
                last_id = -1
            usuario = self.get_usuario(reserva.username)
            sql = "insert into Reserva (res_id, res_dia_inicio, res_hora_inicio, res_hora_fim, res_dia_fim, " \
                  "res_use_id, res_mesa, res_concluida, res_cod_confirmacao) values ({id}, \'{dia_inicio}\', " \
                  "\'{hora_inicio}\', \'{hora_fim}\', \'{dia_fim}\', {use_id}, {mesa}, {concluida}, \'{cod_confirmacao}\')"\
                  ";".format(id=last_id+1, dia_inicio=reserva.dia_inicio, hora_inicio=reserva.hora_inicio,
                            hora_fim=reserva.hora_fim, dia_fim=reserva.dia_fim, use_id=usuario.id, mesa=reserva.mesa,
                            concluida=reserva.concluida, cod_confirmacao=reserva.cod_confirmacao)
            self.cursor.execute(sql)
            self.db.commit()
            return 0
        except MySQLdb.IntegrityError as err:
            return err.args[0]

    def get_all_comandas(self):
        sql = 'select * from Comanda;'
        self.cursor.execute(sql)
        result = self.cursor.fetchall()
        return_value = []
        for i in result:
            comanda = Comanda(i[1], i[2], i[3])
            comanda.id = i[0]
            comanda.total = i[4]
            return_value.append(comanda)
        return return_value

    def add_comanda(self, comanda):
        try:
            comandas = self.get_all_comandas()
            if len(comandas) > 0:
                last_id = comandas[-1].id
            else:
                last_id = 0
            usuario = self.get_usuario(comanda.usuario)
            sql = "insert into Comanda (com_id, com_data, com_use_id, com_mesa, com_total) values ({id}, \'{data}\', " \
                  "{use_id}, {mesa}, {total})".format(id=last_id+1, data=comanda.data, use_id=usuario.id,
                                                      mesa=comanda.mesa, total=comanda.total)
            self.cursor.execute(sql)
            self.db.commit()
            return 0, last_id+1
        except MySQLdb.IntegrityError as err:
            return err.args[0], None

    def get_produtos(self):
        sql = 'select * from Produto;'
        self.cursor.execute(sql)
        result = self.cursor.fetchall()
        return_value = []
        for i in result:
            produto = Produto(i[1], i[2], i[3])
            produto.id = i[0]
            return_value.append(produto)
        return return_value

    def get_produto(self, produto_id):
        sql = 'select * from Produto where prod_id = {id};'.format(id=produto_id)
        self.cursor.execute(sql)
        result = self.cursor.fetchone()
        if result is None:
            return None
        else:
            return_value = Produto(result[1], result[2], result[3])
            return_value.id = result[0]
            return return_value

    def get_comanda(self, comanda_id):
        sql = 'select * from Comanda where com_id = {id};'.format(id=comanda_id)
        self.cursor.execute(sql)
        result = self.cursor.fetchone()
        if result is None:
            return None
        else:
            return_value = Comanda(result[1], result[2], result[3])
            return_value.id = result[0]
            return_value.total = result[4]
            return return_value

    def get_id_produtos_da_comanda(self, comanda_id):
        sql = 'select * from Comanda_Produto where cpd_com_id = {id}'.format(id=comanda_id)
        self.cursor.execute(sql)
        result = self.cursor.fetchall()
        return_value = []
        for i in result:
            return_value.append(i[1])
        return return_value

    def add_produto_comanda(self, produto_id, comanda_id):
        try:
            sql = 'insert into Comanda_Produto (cpd_com_id, cpd_prod_id) values ({com_id}, {prod_id});'.format(com_id=comanda_id, prod_id=produto_id)
            self.cursor.execute(sql)
            self.db.commit()
            return 0
        except MySQLdb.IntegrityError as err:
            return err.args[0]

    def novo_valor_comanda(self, comanda_id, novo_preco):
        try:
            sql = 'update Comanda set com_total = {preco} where com_id = {com_id}'.format(preco=novo_preco, com_id=comanda_id)
            self.cursor.execute(sql)
            self.db.commit()
            return 0
        except MySQLdb.IntegrityError as err:
            return err.args[0]

    def retornar_comandas(self, username):
        try:
            cliente = self.get_usuario(username)
            sql = "select * from Comanda where com_use_id = {cli_id}".format(cli_id=cliente.id)
            self.cursor.execute(sql)
            result = self.cursor.fetchall()
            return_value = []
            for i in result:
                produto = Comanda(i[1], i[2], i[3])
                produto.id = i[0]
                produto.total = i[4]
                return_value.append(produto)
            return return_value
        except MySQLdb.IntegrityError as err:
            return []


if __name__ == '__main__':
    db = DataBase()
    reserva = Reserva("15.11.2018", "20:31", "15.11.2018", "22:31", 1, "arthurmteodoro")
    db.add_reserva(reserva)
