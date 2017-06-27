package projetoi.meucarro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import projetoi.meucarro.R;
import projetoi.meucarro.utils.StatusAdapterPlaceholder;

public class StatusRowAdapter extends ArrayAdapter<StatusAdapterPlaceholder> {

    public StatusRowAdapter(Context context, ArrayList<StatusAdapterPlaceholder> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.carro_status_custom_row, parent, false);
        }
        StatusAdapterPlaceholder placeholder = getItem(position);

        TextView manutencaoText = (TextView) convertView.findViewById(R.id.status_custom_adapter_manutencao);
        TextView descricaoText = (TextView) convertView.findViewById(R.id.status_custom_adapter_descricao);
        ImageView alertImg = (ImageView) convertView.findViewById(R.id.status_custom_adapter_alertimg);


        if (placeholder.isAtrasado()) {
            alertImg.setVisibility(View.VISIBLE);
        }

        manutencaoText.setText(placeholder.getManutencao());
        descricaoText.setText(placeholder.getMensagem());

        return convertView;
    }
}
