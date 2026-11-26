package br.etec.pedemeia.periodos;

import br.etec.pedemeia.periodos.dto.PeriodoLetivoResposta;
import br.etec.pedemeia.periodos.entidade.PeriodoLetivo;

public final class Mapeador {

    private Mapeador() {
    }

    public static PeriodoLetivoResposta paraResposta(PeriodoLetivo periodo) {
        return new PeriodoLetivoResposta(periodo.getId(), periodo.getNome(), periodo.getDataInicio(),
                periodo.getDataFim(), periodo.isAtivo());
    }
}
