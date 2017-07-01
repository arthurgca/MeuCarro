package projetoi.meucarro.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class Gasto  {

    public String descricao;
    public Date data;
    public float valor;
    public long registroKm;

    public Gasto() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Gasto(String descricao, Date data, float valor, long registroKm) {
        this.descricao = descricao;
        this.data = data;
        this.valor = valor;
        this.registroKm = registroKm;
    }

    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
        return descricao + " || " + df.format(data) + " || " + valor;
    }

    public int getDataFormatada(int dateOption) {
        Calendar c = Calendar.getInstance();
        c.setTime(this.data);
        return c.get(dateOption);
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getData() {
        return this.data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public float getValor() {
        return this.valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public long getRegistroKm() {
        return this.registroKm;
    }

    public void setRegistroKm(long registroKm) {
        this.registroKm = registroKm;
    }

    public static Comparator<Gasto> compareByData() {
        return new Comparator<Gasto>() {
            @Override
            public int compare(Gasto o1, Gasto o2) {
                return o1.data.compareTo(o2.data);
            }
        };
    }

    public static Comparator<Gasto> compareByValor() {
        return new Comparator<Gasto>() {
            @Override
            public int compare(Gasto o1, Gasto o2) {
                return (int) (o1.valor - o2.valor);
            }
        };
    }

    public String getFormattedData () {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
        return df.format(this.data);
    }
}
