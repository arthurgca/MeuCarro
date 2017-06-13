package projetoi.meucarro.models;

import java.util.List;


public class CarroUser {
    public String modelo;
    public String ano;
    public String placa;
    public int kmRodados;
    public List<Gasto> listaGastos;

    public CarroUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public CarroUser(String modelo, String ano, String placa, int kmRodados, List<Gasto> listaGastos) {
        this.modelo = modelo;
        this.ano = ano;
        this.placa = placa;
        this.kmRodados = kmRodados;
        this.listaGastos = listaGastos;
    }

    @Override
    public String toString() {
        return "Nome: " + modelo + "Ano: " + ano + "Placa: " + placa + " Km's Rodados: " + this.kmRodados;
    }
}
