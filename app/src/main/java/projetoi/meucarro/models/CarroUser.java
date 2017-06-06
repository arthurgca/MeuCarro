package projetoi.meucarro.models;

import java.util.List;

/**
 * Created by Arthur on 05/06/2017.
 */

public class CarroUser {
    public Carro carro;
    public int kmRodados;

    public CarroUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public CarroUser(Carro carro, int kmRodados) {
        this.carro = carro;
        this.kmRodados = kmRodados;
    }

    @Override
    public String toString() {
        return "Nome: " + carro.nome + " Km's Rodados: " + this.kmRodados;
    }
}
