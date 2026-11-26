package br.etec.pedemeia.matriculas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Ano escolar 1 a 3 (P05). Fora disso, VALIDACAO_INVALIDA (MVP-CA06). Turma opcional (Q09). */
public record MatriculaRequisicao(
        @NotNull(message = "é obrigatório") Long alunoId,
        @NotNull(message = "é obrigatório") @Min(value = 1, message = "deve estar entre 1 e 3")
        @Max(value = 3, message = "deve estar entre 1 e 3") Integer anoEscolar,
        @Size(max = 20, message = "deve ter no máximo 20 caracteres") String turma) {
}
