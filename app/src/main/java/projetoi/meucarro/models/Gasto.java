package projetoi.meucarro.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Gasto {

    public String descricao;
    public Date data;
    public float valor;

    public Gasto() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Gasto(String descricao, Date data, float valor) {
        this.descricao = descricao;
        this.data = data;
        this.valor = valor;
    }

    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return descricao + " || " + df.format(data) + " || " + valor;
    }
}
