package projetoi.meucarro.models;

/**
 * Created by Arthur on 12/08/2017.
 */

public class Venda {

    public String compradorId;
    public String compradorNome;
    public String compradorCep;
    public String carroModelo;
    public String senhaConfirmacao;
    public double valor;
    public boolean haOferta;

    public Venda() {

    }

    public Venda(String compradorId, String compradorNome, String compradorCep, String carroModelo, String senhaConfirmacao, double valor, boolean haOferta) {
        this.compradorId = compradorId;
        this.compradorNome = compradorNome;
        this.compradorCep = compradorCep;
        this.carroModelo = carroModelo;
        this.senhaConfirmacao = senhaConfirmacao;
        this.valor = valor;
        this.haOferta = haOferta;
    }


}
