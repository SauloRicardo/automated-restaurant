import tkinter as tk
import pygubu
import communication
from Classes import *
import threading
import time
import datetime
from tkinter import messagebox
import serial


class MainWindow:
    def __init__(self, master):
        self.master = master
        self.builder = pygubu.Builder()
        self.builder.add_from_file('./layout.ui')
        self.server = communication.Server('http://arthurmteodoro.pythonanywhere.com')

        self.main_window = self.builder.get_object('frm_main', self.master)
        self.master.resizable(False, False)
        self.master.title("CAFETERIA")

        self.uart = serial.Serial('/dev/ttyUSB0', 115200)

        callbacks = {
            'on_click_btn_usar_mesa': self.on_click_btn_usar_mesa,
            'on_click_btn_realizar_pedido': self.on_click_btn_realizar_pedido,
            'on_click_btn_consultar_comanda': self.on_click_btn_consultar_comanda,
            'on_click_btn_fechar_comanda': self.on_click_btn_fechar_comanda
        }
        self.builder.connect_callbacks(callbacks)

        self.lstbox_mesas = self.builder.get_object('lstbox_mesas', self.master)
        self.var_mesas = self.builder.get_variable('var_mesas')

        threading.Thread(target=self.tarefas).start()

        self.comandas_sem_app = [None for _ in range(8)]
        self.mesas = []

    def tarefas(self):
        while True:
            try:
                self.set_mesas()
                mesa = self.mesas[0]
                if mesa.status == Mesa.RESERVADA:
                    reserva = self.server.ultima_reserva_mesa(mesa)
                    command = "ESCREVE-TELA: {cod}*".format(cod=reserva.cod_confirmacao)
                    self.uart.write(command.encode())
                    print(reserva.cod_confirmacao)
                elif mesa.status == Mesa.DISPONIVEL:
                    command = "ESCREVE-TELA: Mesa Livre!*"
                    self.uart.write(command.encode())
                else:
                    command = "ESCREVE-TELA: Bom Apetite!*"
                    self.uart.write(command.encode())
            except:
                pass
            time.sleep(1)

    def on_click_btn_usar_mesa(self, event):
        top2 = tk.Toplevel(self.master)
        app2 = TelaUsarMesa(top2, self.mesas, self.comandas_sem_app, self.server)

    def on_click_btn_realizar_pedido(self, event):
        top2 = tk.Toplevel(self.master)
        app2 = TelaRealizarPedido(top2, self.comandas_sem_app, self.server)

    def on_click_btn_consultar_comanda(self, event):
        top2 = tk.Toplevel(self.master)
        app2 = TelaConsultaComanda(top2, self.comandas_sem_app, self.server)

    def on_click_btn_fechar_comanda(self, event):
        top2 = tk.Toplevel(self.master)
        app2 = TelaFecharComanda(top2, self.comandas_sem_app, self.server)

    def set_mesas(self):
        self.lstbox_mesas.delete(0, tk.END)
        self.mesas = self.server.get_all_mesas()
        for i in self.mesas:
            if i.status == Mesa.DISPONIVEL:
                status = "Disponível"
            elif i.status == Mesa.RESERVADA:
                status = "Reservada"
            else:
                status = "Ocupada"
            self.lstbox_mesas.insert(tk.END, "Mesa {id} - Quantidade de Lugares: {qtd} - Status: {status}".format(
                id=i.id, qtd=i.qtd_lugar, status=status))


class TelaUsarMesa:
    def __init__(self, master, mesas, comandas, server):
        self.master = master
        self.builder = pygubu.Builder()
        self.builder.add_from_file('./layout_usar_mesa.ui')

        self.mesas_livres = []
        self.server = server
        for i in mesas:
            if i.status == Mesa.DISPONIVEL:
                self.mesas_livres.append(i)

        self.comandas = comandas

        self.main_window = self.builder.get_object('frm_main', self.master)
        self.master.resizable(False, False)
        self.master.title("CAFETERIA")

        callbacks = {
            'on_click_btn_criar_comanda': self.on_click_btn_criar_comanda
        }
        self.builder.connect_callbacks(callbacks)

        self.cbx_mesa = self.builder.get_object('cbx_mesa', self.master)
        self.cbx_mesa['values'] = self.mesas_livres

    def on_click_btn_criar_comanda(self, event):
        mesa = self.mesas_livres[self.cbx_mesa.current()]
        now = datetime.datetime.now()
        data = now.strftime("%d-%m-%Y")
        user = "semapp"
        comanda = Comanda(data, user, mesa.id)
        self.server.usar_mesa(mesa)
        self.server.criar_comanda(comanda)
        self.comandas[mesa.id-1] = comanda
        messagebox.showinfo('Comanda', 'Comanda Criada com Sucesso')


class TelaRealizarPedido:
    def __init__(self, master, comandas, server):
        self.master = master
        self.builder = pygubu.Builder()
        self.builder.add_from_file('./layout_fazer_pedido.ui')

        self.main_window = self.builder.get_object('frm_main', self.master)
        self.master.resizable(False, False)
        self.master.title("CAFETERIA")

        self.comandas = comandas
        self.server = server

        self.cbx_comanda_aberta = self.builder.get_object('cbx_comanda_aberta', self.master)
        self.cbx_produto = self.builder.get_object('cbx_produto', self.master)
        self.spb_quantidade = self.builder.get_object('spb_quantidade', self.master)

        self.produtos = self.server.get_produtos()
        self.cbx_produto['values'] = self.produtos

        self.comandas_sem_none = [x for x in self.comandas if x is not None]
        self.cbx_comanda_aberta['values'] = self.comandas_sem_none

        callbacks = {
            'on_click_btn_pedir': self.on_click_btn_pedir
        }
        self.builder.connect_callbacks(callbacks)

    def on_click_btn_pedir(self, event):
        comanda = self.comandas_sem_none[self.cbx_comanda_aberta.current()]
        produto = self.produtos[self.cbx_produto.current()]
        qtd = int(self.spb_quantidade.get())

        for i in range(qtd):
            pedido = Pedido(comanda.id, produto.id)
            self.server.fazer_pedido(pedido)
        messagebox.showinfo('Pedido', 'Pedido Criado com Sucesso')


class TelaConsultaComanda:
    def __init__(self, master, comandas, server):
        self.master = master
        self.builder = pygubu.Builder()
        self.builder.add_from_file('./layout_consultar_comanda.ui')

        self.main_window = self.builder.get_object('frm_main', self.master)
        self.master.resizable(False, False)
        self.master.title("CAFETERIA")

        self.comandas = comandas
        self.server = server

        self.cbx_comanda = self.builder.get_object('cbx_comanda', self.master)
        self.lsb_produtos = self.builder.get_object('lsb_produtos', self.master)
        self.lbl_total = self.builder.get_object('lbl_total', self.master)

        self.comandas_sem_none = [x for x in self.comandas if x is not None]
        self.cbx_comanda['values'] = self.comandas_sem_none

        callbacks = {
            'on_select_cbx_comanda': self.on_select_cbx_comanda
        }

        self.builder.connect_callbacks(callbacks)

    def on_select_cbx_comanda(self, event):
        comanda = self.comandas_sem_none[self.cbx_comanda.current()]
        produtos = self.server.get_pedidos_comanda(comanda)

        total = 0
        self.lsb_produtos.delete(0, tk.END)
        for i in produtos:
            self.lsb_produtos.insert(tk.END, "Nome: {nome} Valor: {val}".format(nome=i.nome, val=i.valor))
            total += float(i.valor)
        self.lbl_total['text'] = "Total: %.2f" % total


class TelaFecharComanda:
    def __init__(self, master, comandas, server):
        self.master = master
        self.builder = pygubu.Builder()
        self.builder.add_from_file('./layout_fechar_comanda.ui')

        self.main_window = self.builder.get_object('frm_main', self.master)
        self.master.resizable(False, False)
        self.master.title("CAFETERIA")

        self.comandas = comandas
        self.server = server

        self.cbx_comanda = self.builder.get_object('cbx_comanda', self.master)

        self.comandas_sem_none = [x for x in self.comandas if x is not None]
        self.cbx_comanda['values'] = self.comandas_sem_none

        callbacks = {
            'on_click_btn_fechar': self.on_click_btn_fechar
        }
        self.builder.connect_callbacks(callbacks)

    def on_click_btn_fechar(self, event):
        comanda = self.comandas_sem_none[self.cbx_comanda.current()]
        self.server.fechar_comanda(comanda)
        messagebox.showinfo('Comanda', 'Comanda Fechada com Sucesso')


if __name__ == '__main__':
    root = tk.Tk()
    app = MainWindow(root)
    root.mainloop()
