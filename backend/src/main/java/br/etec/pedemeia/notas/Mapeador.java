package br.etec.pedemeia.notas;

import br.etec.pedemeia.notas.dto.NotaResposta;
import br.etec.pedemeia.notas.entidade.NotaProva;

public final class Mapeador {

    private Mapeador() {
    }

    public static NotaResposta paraResposta(NotaProva nota) {
        return new NotaResposta(nota.getId(), nota.getProva().getId(), nota.getMatricula().getId(),
                nota.getMatricula().getAluno().getNome(), nota.getMatricula().getAluno().getMatriculaEscolar(),
                nota.getNota(), nota.getLancadaPor().getId(), nota.getLancadaEm(),
                nota.getAlteradaPor() == null ? null : nota.getAlteradaPor().getId(), nota.getAlteradaEm());
    }
}
