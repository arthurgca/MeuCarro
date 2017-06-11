package projetoi.meucarro.models;

import java.util.Date;

/**
 * Created by Arthur on 11/06/2017.
 */

public class Gasto {

    public String descricao;
    public Date data;

    public Gasto() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Gasto(String descricao, Date data) {
        this.descricao = descricao;
        this.data = data;
    }
}
