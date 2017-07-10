package projetoi.meucarro.models;

import java.util.List;

/**
 * Created by Jaaziel on 03/07/2017.
 */

public class Oficina {
    private String nome;
    private String telefone;
    private String endereco;
    private Float nota;
    public List<Oficina> listaOficinas;

    public Oficina() {

    }

    public Oficina(String nome, String telefone, String endereco, Float nota) {
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.nota = nota;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Float getNota() {
        return nota;
    }

    public void setNota(Float nota) {
        this.nota = nota;
    }

    @Override
    public String toString() {
        return "Oficina{" +
                "nome='" + nome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", endereco='" + endereco + '\'' +
                ", nota=" + nota +
                '}';
    }
}
