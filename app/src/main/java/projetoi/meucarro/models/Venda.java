package projetoi.meucarro.models;

/**
 * Created by Arthur on 12/08/2017.
 */

public class Venda {

    public String compradorId;
    public String senhaConfirmacao;
    public double valor;
    public boolean haOferta;

    public Venda() {

    }

    public Venda(String compradorId, String senhaConfirmacao, double valor, boolean haOferta) {
        this.compradorId = compradorId;
        this.senhaConfirmacao = senhaConfirmacao;
        this.valor = valor;
        this.haOferta = haOferta;
    }


}
