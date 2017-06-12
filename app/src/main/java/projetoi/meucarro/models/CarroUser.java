package projetoi.meucarro.models;

import java.util.List;


public class CarroUser {
    public String modelo;
    public int kmRodados;
    public List<Gasto> listaGastos;

    public CarroUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public CarroUser(String modelo, int kmRodados, List<Gasto> listaGastos) {
        this.modelo = modelo;
        this.kmRodados = kmRodados;
        this.listaGastos = listaGastos;
    }

    @Override
    public String toString() {
        return "Nome: " + modelo + " Km's Rodados: " + this.kmRodados;
    }
}
