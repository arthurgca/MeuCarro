package projetoi.meucarro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import projetoi.meucarro.R;
import projetoi.meucarro.models.Gasto;
import projetoi.meucarro.models.Oficina;

/**
 * Created by Jaaziel on 10/07/2017.
 */

public class OficinaAdapter extends ArrayAdapter<Oficina> {

    public OficinaAdapter(Context context, ArrayList<Oficina> gastosList) {
        super(context, 0, gastosList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.oficina_row, parent, false);
        }

        Oficina oficina = getItem(position);

        TextView oficinaText = (TextView) convertView.findViewById(R.id.oficina_nome);
        TextView enderecoText = (TextView) convertView.findViewById(R.id.oficina_endereco);
        TextView telefoneText = (TextView) convertView.findViewById(R.id.oficina_telefone);
        RatingBar notaRating = (RatingBar) convertView.findViewById(R.id.oficina_nota);

        oficinaText.setText(oficina.getNome());
        enderecoText.setText(oficina.getEndereco());
        telefoneText.setText(oficina.getTelefone());
        notaRating.setRating(oficina.getNota());

        return convertView;
    }
}
