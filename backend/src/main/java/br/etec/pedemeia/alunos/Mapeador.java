package br.etec.pedemeia.alunos;

import br.etec.pedemeia.alunos.dto.AlunoResposta;
import br.etec.pedemeia.alunos.entidade.Aluno;

public final class Mapeador {

    private Mapeador() {
    }

    public static AlunoResposta paraResposta(Aluno aluno) {
        return new AlunoResposta(aluno.getId(), aluno.getNome(), aluno.getMatriculaEscolar(),
                aluno.getDataNascimento(), aluno.getEmail(), aluno.getTelefone(), aluno.isAtivo(),
                aluno.getCriadoEm());
    }
}
