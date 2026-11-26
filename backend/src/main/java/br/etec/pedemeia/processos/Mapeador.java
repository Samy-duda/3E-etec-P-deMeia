package br.etec.pedemeia.processos;

import br.etec.pedemeia.processos.dto.ProcessoSeletivoResposta;
import br.etec.pedemeia.processos.entidade.ProcessoSeletivo;

public final class Mapeador {

    private Mapeador() {
    }

    public static ProcessoSeletivoResposta paraResposta(ProcessoSeletivo processo) {
        return new ProcessoSeletivoResposta(processo.getId(), processo.getPeriodo().getId(), processo.getProva().getId(),
                processo.getStatus(), processo.getCriadoPor() == null ? null : processo.getCriadoPor().getId());
    }
}
