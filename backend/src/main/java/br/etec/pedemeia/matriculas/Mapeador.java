package br.etec.pedemeia.matriculas;

import br.etec.pedemeia.matriculas.dto.MatriculaResposta;
import br.etec.pedemeia.matriculas.entidade.Matricula;

public final class Mapeador {

    private Mapeador() {
    }

    public static MatriculaResposta paraResposta(Matricula matricula) {
        return new MatriculaResposta(matricula.getId(), matricula.getAluno().getId(),
                matricula.getAluno().getNome(), matricula.getAluno().getMatriculaEscolar(),
                matricula.getPeriodo().getId(), matricula.getAnoEscolar(), matricula.getTurma(),
                matricula.getStatus());
    }
}
