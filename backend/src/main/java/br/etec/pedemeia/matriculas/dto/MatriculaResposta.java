package br.etec.pedemeia.matriculas.dto;

public record MatriculaResposta(
        Long id,
        Long alunoId,
        String nomeAluno,
        String matriculaEscolar,
        Long periodoLetivoId,
        int anoEscolar,
        String turma,
        String status) {
}
