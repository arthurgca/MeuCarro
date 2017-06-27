package projetoi.meucarro.models;

import java.util.Date;


public class GastoCombustivel extends Gasto {

    public float valorUnidade;

    public GastoCombustivel(String descricao, Date data, float valor, long registroKm, float valorUnidade) {
        super(descricao, data, valor, registroKm);
        this.valorUnidade = valorUnidade;
    }

    @Override
    public String toString() {
        return super.toString() + " Litros: " + valorUnidade;
    }
}
