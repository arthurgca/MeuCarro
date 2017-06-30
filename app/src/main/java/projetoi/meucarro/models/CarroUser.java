package projetoi.meucarro.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projetoi.meucarro.R;

public class CarroUser {
    public String marca;
    public String modelo;
    public String ano;
    public String placa;
    public int kmRodados;
    public List<Gasto> listaGastos;
    public Map<String, Double> somaDeGastosPorTipo;
    public Double somaDeGastos;

    public CarroUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public CarroUser(String marca, String modelo, String ano, String placa, int kmRodados, List<Gasto> listaGastos) {
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.placa = placa;
        this.kmRodados = kmRodados;
        this.listaGastos = listaGastos;
        this.initSomaDeGastos();
        this.somaDeGastos = 0.0;

    }

    private void initSomaDeGastos() {
        this.somaDeGastosPorTipo = new HashMap<>();
        String[] tipoDeGastos = new String[]{
                "Combustível",
                "Troca de Óleo",
                "Troca de Pneu",
                "IPVA",
                "Compra de Peças",
                "Correia Dentada",
                "Filtro Ar Condicionado",
                "Filtro de Ar",
                "Velas",
                "Revisão"
        };

        for (int i = 0; i < tipoDeGastos.length; i++) {
            this.somaDeGastosPorTipo.put(tipoDeGastos[i], 0.0);
        }

    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getKmRodados() {
        return kmRodados;
    }

    public void setKmRodados(int kmRodados) {
        this.kmRodados = kmRodados;
    }

    public Double getSomaDeGastos() {
        return somaDeGastos;
    }

    public Double getSomaDeGastoPorTipo(String descricao) {
        return this.somaDeGastosPorTipo.get(descricao);
    }

    public void adicionaGasto(Gasto gasto) {
        this.somaDeGastosPorTipo.put(
                gasto.getDescricao(), this.somaDeGastosPorTipo.get(gasto.descricao) + gasto.getValor());
        this.somaDeGastos += gasto.getValor();
        this.listaGastos.add(gasto);

    }

    public void removeGasto(Gasto gasto) {
        this.somaDeGastosPorTipo.put(
                gasto.getDescricao(), this.somaDeGastosPorTipo.get(gasto.descricao) - gasto.getValor());
        this.somaDeGastos -= gasto.getValor();
        this.listaGastos.remove(gasto);
    }

    @Override
    public String toString() {
        return "Nome: " + modelo + "Ano: " + ano + "Placa: " + placa + " Km's Rodados: " + this.kmRodados;
    }
}
