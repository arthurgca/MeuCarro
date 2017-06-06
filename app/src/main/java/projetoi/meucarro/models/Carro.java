package projetoi.meucarro.models;


import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Carro {

    public String nome;
    public String modelo;
    public String motor;
    public String combustivel;
    public String anoFabricacao;
    public String anoModelo;


    public Carro() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Carro(String nome, String modelo, String motor, String combustivel, String anoFabricacao, String anoModelo) {
        this.nome = nome;
        this.modelo = modelo;
        this.motor = motor;
        this.combustivel = combustivel;
        this.anoFabricacao = anoFabricacao;
        this.anoModelo = anoModelo;
    }

}