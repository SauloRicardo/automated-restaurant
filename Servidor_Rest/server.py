from flask import Flask, request, jsonify, abort
import database_control as db_control
import random

app = Flask(__name__)

db = db_control.DataBase()


@app.route('/api/usuario/registrar', methods=['POST'])
def add_usuarios():
    req_json = request.get_json()
    novo_usario = db_control.Usuario(req_json['username'], req_json['password'], req_json['nome'])
    ok = db.add_usuario(novo_usario)
    return jsonify({"response": {"status": "{cod}".format(cod=ok)}})


@app.route('/api/usuario/login', methods=['POST'])
def login():
    req_json = request.get_json()
    user = db.get_usuario(req_json['username'])
    if user is None:
        return jsonify({"response": {"status": "user dont exist"}})
    else:
        if user.password == req_json['password']:
            return jsonify({"response": {"status": "ok"}})
        else:
            return jsonify({"response": {"status": "username and password dont match"}})


@app.route('/api/mesas/get_livres', methods=['GET'])
def get_mesas_livres():
    mesas_livres = db.get_mesas_livres()
    return jsonify({"response": [i.serializable() for i in mesas_livres]})


@app.route("/api/mesas/get_todas", methods=['GET'])
def get_all_livres():
    mesas = db.get_all_mesa()
    return jsonify({"response": [i.serializable() for i in mesas]})


@app.route('/api/mesas/reservar', methods=['POST'])
def reservar_mesa():
    req_json = request.get_json()
    reserva = db_control.Reserva(req_json['dia_inicio'], req_json['hora_inicio'], req_json['dia_fim'],
                                 req_json['hora_fim'], req_json['mesa'], req_json['username'])
    rand = random.randint(1, 999999)
    reserva.cod_confirmacao = rand
    ok_add_reserva = db.add_reserva(reserva)
    ok_add_mesa = db.reservar_mesa(reserva.mesa)

    if ok_add_mesa == 0 and ok_add_reserva == 0:
        return_value = reserva.__dict__
        return_value['usuario'] = db.get_usuario(reserva.username).__dict__
        return jsonify({"result": 0, "value": return_value})
    else:
        return jsonify({'result': -1, "value": {"reserva": ok_add_reserva, "mesa": ok_add_mesa}})


@app.route("/api/mesas/usar_mesa", methods=["POST"])
def usar_mesa():
    req_json = request.get_json()
    mesa = db_control.Mesa()

    mesa.id = req_json['id']
    mesa.qtd_lugar = req_json['qtd_lugar']
    mesa.status = req_json['status']

    ok = db.usar_mesa(mesa)
    return jsonify({"result": ok})


@app.route("/api/comanda/criar", methods=["POST"])
def criar_comanda():
    req_json = request.get_json()
    comanda = db_control.Comanda(req_json["data"], req_json["username"], req_json["mesa"])
    ok, id = db.add_comanda(comanda)
    if ok == 0:
        comanda.id = id
        return jsonify({"result": 0, "value": comanda.__dict__})
    else:
        return jsonify({"result": 0, "value": ok})


@app.route("/api/produtos/get_produtos", methods=["GET"])
def get_produtos():
    produtos = db.get_produtos()
    return jsonify({"response": [i.serializable() for i in produtos]})


@app.route("/api/comanda/get_pedidos", methods=["POST"])
def get_produtos_comanda():
    req_json = request.get_json()
    lista_produtos_id = db.get_id_produtos_da_comanda(req_json['id'])
    lista_produtos = []
    for i in lista_produtos_id:
        prod = db.get_produto(i)
        lista_produtos.append(prod)
    return jsonify({"response": [i.serializable() for i in lista_produtos]})


@app.route("/api/comanda/pedir_produto", methods=["POST"])
def pedir_produto():
    req_json = request.get_json()
    prod_id = req_json['produto']
    com_id = req_json['comanda']

    prod = db.get_produto(prod_id)
    comanda = db.get_comanda(com_id)

    ok_insert = db.add_produto_comanda(prod_id, com_id)
    ok_update = db.novo_valor_comanda(com_id, comanda.total+float(prod.valor))

    if ok_insert == 0 and ok_update == 0:
        return jsonify({"response": 0})
    else:
        return jsonify({"response": -1})


@app.route("/api/comanda/fechar", methods=["POST"])
def fechar_comanda():
    req_json = request.get_json()

    mesa = db_control.Mesa()
    mesa.id = req_json["mesa"]

    ok = db.liberar_mesa(mesa)
    return jsonify({"response": ok})


@app.route("/api/comanda/consultar_comandas", methods=["POST"])
def get_comandas():
    req_json = request.get_json()

    username_comanda = req_json["username"]
    result = db.retornar_comandas(username_comanda)
    return jsonify({"response": [i.serializable() for i in result]})


@app.route("/api/comanda/ultima_reserva_mesa", methods=['POST'])
def get_ultima_reserva_mesa():
    req_json = request.get_json()

    mesa_id = req_json["id"]
    reservas = db.get_all_reservas()
    reservas_da_mesa = [x for x in reservas if x.mesa == mesa_id]

    if len(reservas_da_mesa) > 0:
        return jsonify({'response': reservas_da_mesa[-1].serializable()})
    else:
        return jsonify({'response': []})
