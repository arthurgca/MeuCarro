package projetoi.meucarro.models;

/**
 * Created by Arthur on 12/08/2017.
 */

public class Venda {

    public String donoId;
    public String compradorId;
    public Carro carroVendam;
    public boolean haOferta;

    public Venda() {

    }

    public Venda(String donoId, String compradorId, String idCarro, boolean haOferta) {
        this.donoId = donoId;
        this.compradorId = compradorId;
        this.carroVendam = carroVendam;
        this.haOferta = haOferta;
    }


}
