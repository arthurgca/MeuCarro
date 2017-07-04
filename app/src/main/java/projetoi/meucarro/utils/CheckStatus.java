package projetoi.meucarro.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import projetoi.meucarro.models.CarroUser;
import projetoi.meucarro.models.Gasto;

/**
 * Created by Arthur on 04/07/2017.
 */

public class CheckStatus {


    private static ArrayList<StatusAdapterPlaceholder> placeholderList;

    public static ArrayList<StatusAdapterPlaceholder> checaStatus(HashMap manutencao, CarroUser currentCar) {
        placeholderList = new ArrayList<>();

        for (Object i : manutencao.keySet()) {
            StatusAdapterPlaceholder placeholder;
            String mensagem = "";

            Gasto ultimoGasto = null;
            int quantidadeTrocas = 0;

            long valorKm = (long) ((HashMap) manutencao.get(i)).get("Kilometragem");

            for (Gasto gasto : currentCar.listaGastos) {
                if (gasto.descricao.equals(i.toString())) {
                    ultimoGasto = gasto;
                    quantidadeTrocas++;
                }
            }

            boolean atrasado = false;
            String dataManutencao = (String) ((HashMap) manutencao.get(i)).get("Tempo");

            if (ultimoGasto != null) {
                long diferenca = (valorKm - (currentCar.kmRodados - ultimoGasto.registroKm));
                Log.d("valorKm", String.valueOf(valorKm));
                Log.d("currentCar.kmRodados", String.valueOf(currentCar.kmRodados));
                Log.d("ultimoGasto.registroKm", String.valueOf(ultimoGasto.registroKm));


                if (diferenca <= 0) {
                    atrasado = true;
                    mensagem = mensagem.concat("Manutenção deveria ter sido efetuada" + "\n" +
                            "Já se passaram " + -diferenca + " km's.");
                } else {
                    mensagem = mensagem.concat("Quantidade efetuada: " + quantidadeTrocas + "\n" +
                            "Faltam: " + diferenca + " km's.");
                }

                if (dataManutencao != null)
                    if (checkAtrasoData(ultimoGasto.data, dataManutencao)) {
                        atrasado = true;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                        String ultimaManutencao = dateFormat.format(ultimoGasto.data);
                        mensagem = "Atraso por data \n" +
                                "Última manutenção: " + ultimaManutencao + "\nPrazo: " + dataManutencao;
                    }

            } else {
                long diferenca = (valorKm - currentCar.kmRodados);
                if (diferenca <= 0) {
                    atrasado = true;
                    mensagem = mensagem.concat("Manutenção deveria ter sido efetuada" + "\n" +
                            "Já se passaram " + -diferenca + " km's.");
                } else {
                    mensagem = mensagem.concat("Faltam: " + diferenca + " km's.");
                }
            }
            placeholder = new StatusAdapterPlaceholder(i.toString(), mensagem, atrasado);
            placeholderList.add(placeholder);
        }
        return placeholderList;
    }

    private static boolean checkAtrasoData(Date data, String dataString) {
        Calendar dataAtual = Calendar.getInstance();
        Calendar dataManutencao = Calendar.getInstance();
        dataManutencao.setTime(data);

        if (dataString.equals("3 anos")) {
            dataManutencao.add(Calendar.YEAR, 3);
        } else if (dataString.equals("1 ano")) {
            dataManutencao.add(Calendar.YEAR, 1);
        } else if (dataString.equals("6 meses")) {
            dataManutencao.add(Calendar.MONTH, 6);
        }

        if (dataAtual.compareTo(dataManutencao) > 0) {
            Log.d("dataAtual", dataAtual.toString());
            Log.d("dataManutencao", dataManutencao.toString());
            return true;
        } else
            return false;
    }
}
