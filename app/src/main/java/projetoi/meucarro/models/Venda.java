package projetoi.meucarro.models;

import java.util.Locale;

/**
 * Created by Arthur on 12/08/2017.
 */

public class Venda {

    public String vendedorId;
    public String compradorId;
    public String carroId;
    public String carroModelo;
    public String carroAno;
    public String senhaConfirmacao;
    public double valor;
    public boolean haOferta;

    public Venda() {

    }

    public Venda(String vendedorId, String compradorId, String carroId, String carroModelo, String carroAno,
                 String senhaConfirmacao, double valor, boolean haOferta) {
        this.vendedorId = vendedorId;
        this.compradorId = compradorId;
        this.carroId = carroId;
        this.carroModelo = carroModelo;
        this.carroAno = carroAno;
        this.senhaConfirmacao = senhaConfirmacao;
        this.valor = valor;
        this.haOferta = haOferta;
    }

    @Override
    public String toString() {
        String s = String.format(Locale.ENGLISH,
                "Modelo: %s\n" +
                "Ano: %s \n" +
                "Valor: %.2f", carroModelo, carroAno, valor);
        return s;
    }
}
