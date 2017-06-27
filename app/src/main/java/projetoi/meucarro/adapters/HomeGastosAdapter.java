package projetoi.meucarro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import projetoi.meucarro.R;
import projetoi.meucarro.models.Gasto;

public class HomeGastosAdapter extends ArrayAdapter<Gasto> {

    public HomeGastosAdapter(Context context, ArrayList<Gasto> gastosList) {
        super(context, 0, gastosList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_gasto_custom_row, parent, false);
        }

        Gasto gasto = getItem(position);

        TextView manutencaoText = (TextView) convertView.findViewById(R.id.home_adapter_manutencaotext);
        TextView dataText = (TextView) convertView.findViewById(R.id.home_adapter_datatext);
        TextView valorText = (TextView) convertView.findViewById(R.id.home_adapter_valortext);

        manutencaoText.setText(gasto.descricao);
        dataText.setText(gasto.getFormattedData());
        valorText.setText(String.valueOf(gasto.valor));

        return convertView;
    }
}
