package projetoi.meucarro.utils;

/**
 * Created by Arthur on 26/06/2017.
 */

public class StatusAdapterPlaceholder {
    private final String manutencao;
    private final String mensagem;
    private final boolean atrasado;

    public String getManutencao() {
        return manutencao;
    }

    public String getMensagem() {
        return mensagem;
    }

    public boolean isAtrasado() {
        return atrasado;
    }

    public StatusAdapterPlaceholder(String manutencao, String mensagem, boolean atrasado) {
        this.manutencao = manutencao;
        this.mensagem = mensagem;
        this.atrasado = atrasado;
    }
}
