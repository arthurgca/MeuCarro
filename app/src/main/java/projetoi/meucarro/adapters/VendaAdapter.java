package projetoi.meucarro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import projetoi.meucarro.R;
import projetoi.meucarro.models.Gasto;
import projetoi.meucarro.models.Oficina;
import projetoi.meucarro.models.Venda;

/**
 * Created by Jaaziel on 10/07/2017.
 */

public class VendaAdapter extends ArrayAdapter<Venda> {

    public VendaAdapter(Context context, ArrayList<Venda> vendasList) {
        super(context, 0, vendasList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.venda_row, parent, false);
        }

        Venda venda = getItem(position);

        TextView vendaNome = (TextView) convertView.findViewById(R.id.vendarow_modelo);
        TextView vendaAno = (TextView) convertView.findViewById(R.id.vendarow_ano);
        TextView vendaValor = (TextView) convertView.findViewById(R.id.vendarow_valor);
        ImageView alertaOferta = (ImageView) convertView.findViewById(R.id.vendarow_alertaofertaimg);

        vendaNome.setText(String.format("Modelo: %s", venda.carroModelo));
        vendaAno.setText(String.format("Ano: %s", venda.carroAno));
        vendaValor.setText(String.format("Valor: %s", venda.valor));

        if (venda.haOferta) {
            alertaOferta.setVisibility(View.VISIBLE);
        } else {
            alertaOferta.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
