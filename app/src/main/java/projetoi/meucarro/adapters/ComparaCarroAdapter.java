package projetoi.meucarro.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import projetoi.meucarro.R;
import projetoi.meucarro.models.Gasto;
import projetoi.meucarro.utils.Comparacao;

/**
 * Created by Arthur on 23/10/2017.
 */
public class ComparaCarroAdapter extends ArrayAdapter<Comparacao> {

    public ComparaCarroAdapter(Context context, ArrayList<Comparacao> comparacoes) {
        super(context, 0, comparacoes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comparagasto_custom_row, parent, false);
        }

        Comparacao comparacao = getItem(position);

        TextView nomeGasto = (TextView) convertView.findViewById(R.id.nomeGasto);
        TextView gastoC1 = (TextView) convertView.findViewById(R.id.gastoC1);
        TextView gastoC2 = (TextView) convertView.findViewById(R.id.gastoC2);

        String stringGasto = comparacao.getGasto();
        nomeGasto.setText(stringGasto);

        if (comparacao.getGastoCarro1().compareTo(comparacao.getGastoCarro2()) == 0) {
            gastoC1.setTextColor(Color.BLUE);
            gastoC2.setTextColor(Color.BLUE);
        } else if (comparacao.getGastoCarro1().compareTo(comparacao.getGastoCarro2()) > 0) {
            gastoC1.setTextColor(Color.RED);
            gastoC2.setTextColor(Color.GREEN);
        } else if (comparacao.getGastoCarro1().compareTo(comparacao.getGastoCarro2()) < 0)  {
            gastoC2.setTextColor(Color.RED);
            gastoC1.setTextColor(Color.GREEN);
        }


        gastoC1.setText(String.valueOf(comparacao.getGastoCarro1()));
        gastoC2.setText(String.valueOf(comparacao.getGastoCarro2()));

        return convertView;
    }
}