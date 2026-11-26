package br.etec.pedemeia.provas;

import br.etec.pedemeia.provas.dto.ProvaResposta;
import br.etec.pedemeia.provas.entidade.Prova;

public final class Mapeador {

    private Mapeador() {
    }

    public static ProvaResposta paraResposta(Prova prova) {
        return new ProvaResposta(prova.getId(), prova.getPeriodo().getId(), prova.getTitulo(), prova.getDescricao(),
                prova.getDataAplicacao(), prova.getNotaMaxima(),
                prova.getCriadaPor() == null ? null : prova.getCriadaPor().getId(), prova.getCriadaEm());
    }
}
