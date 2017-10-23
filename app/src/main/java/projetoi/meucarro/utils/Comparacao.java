package projetoi.meucarro.utils;

import projetoi.meucarro.models.Carro;

/**
 * Created by Arthur on 23/10/2017.
 */


public class Comparacao {
    private String gasto;
    private Double gastoCarro1;
    private Double gastoCarro2;

    public String getGasto() {
        return gasto;
    }

    public Double getGastoCarro1() {
        return gastoCarro1;
    }

    public Double getGastoCarro2() {
        return gastoCarro2;
    }

    public Comparacao(String gasto, Double gastoCarro1, Double gastoCarro2) {
        this.gasto = gasto;
        this.gastoCarro1 = gastoCarro1;
        this.gastoCarro2 = gastoCarro2;
    }


}
