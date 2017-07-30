package projetoi.meucarro.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projetoi.meucarro.R;

import static android.R.attr.logo;
import static android.R.attr.x;

public class Carro {
    public String marca;
    public String modelo;
    public String ano;
    public String placa;
    public int kmRodados;
    public List<Gasto> listaGastos;
    public Map<String, Double> somaDeGastosPorTipo;
    public Double somaDeGastos;

    public Carro() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Carro(String marca, String modelo, String ano, String placa, int kmRodados, List<Gasto> listaGastos) {
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.placa = placa;
        this.kmRodados = kmRodados;
        this.listaGastos = listaGastos;
        this.somaDeGastosPorTipo = new HashMap<>();
        this.somaDeGastos = 0.0;
        this.initSomaDeGastos();
    }

    private void initSomaDeGastos() {
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
        return this.marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return this.modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getAno() {
        return this.ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getPlaca() {
        return this.placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getKmRodados() {
        return this.kmRodados;
    }

    public void setKmRodados(int kmRodados) {
        this.kmRodados = kmRodados;
    }

    public List<Gasto> getListaGastos() {
        return this.listaGastos;
    }

    public boolean estaSemGastos() {
        return this.listaGastos == null;
    }

    public Double getSomaDeGastos() {
        return this.somaDeGastos;
    }

    public Double getSomaDeGastoPorTipo(String descricao) {
        return this.somaDeGastosPorTipo.get(descricao);
    }

    public void adicionaGasto(Gasto gasto) {
        this.somaDeGastosPorTipo.put(
                gasto.getDescricao(), this.somaDeGastosPorTipo.get(gasto.getDescricao()) + gasto.getValor());
        this.somaDeGastos += gasto.getValor();
        this.listaGastos.add(gasto);

    }

    public void removeGasto(Gasto gasto) {
        this.somaDeGastosPorTipo.put(
                gasto.getDescricao(), this.somaDeGastosPorTipo.get(gasto.getDescricao()) - gasto.getValor());
        this.somaDeGastos -= gasto.getValor();

        int gastoIndex = this.listaGastos.indexOf(gasto);
        int indexUltimo = this.listaGastos.size() - 1;

        if (gastoIndex == indexUltimo) {
            if (listaGastos.size() == 1) {
                this.kmRodados = 0;
            } else {
                Gasto gastoAnterior = listaGastos.get(gastoIndex - 1);
                if (gastoAnterior.registroKm != gasto.registroKm) {
                    this.kmRodados = (int) gastoAnterior.registroKm;
                }
            }
        }

        this.listaGastos.remove(gasto);


    }

    public ArrayList<ArrayList<Gasto>> getExpensesByYear(int year) {
        int MONTHS = 12;
        ArrayList<ArrayList<Gasto>> expensesByYear = new ArrayList<>();

        for (int m = 0; m < MONTHS; m++) {
            expensesByYear.add(new ArrayList<Gasto>());
        }

        for (Gasto expense : this.getListaGastos()) {
            int expanseYear = expense.getDataFormatada(Calendar.YEAR);

            if (expanseYear == year) {
                int expanseMonth = expense.getDataFormatada(Calendar.MONTH);
                expensesByYear.get(expanseMonth).add(expense);
            }
        }

        return expensesByYear;
    }

    public Double calculateExpensesSum (List<Gasto> expensesList) {
        Double sum = 0.0;

        for (Gasto expense : expensesList) {
            sum += expense.getValor();
        }

        return sum;
    }

    public ArrayList<Gasto> getExpensesByType (List<Gasto> expensesList, String type) {
        ArrayList<Gasto> expensesByType = new ArrayList<>();

        for (Gasto expense : expensesList) {
            if (expense.getDescricao().equals(type)) {
                expensesByType.add(expense);
            }
        }

        return expensesByType;
    }

    @Override
    public String toString() {
        return "Nome: " + this.modelo + "Ano: " + this.ano + "Placa: " + this.placa + " Km's Rodados: " + this.kmRodados;
    }
}
