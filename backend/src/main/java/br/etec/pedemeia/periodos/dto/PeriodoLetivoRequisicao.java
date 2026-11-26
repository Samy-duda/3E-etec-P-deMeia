package br.etec.pedemeia.periodos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PeriodoLetivoRequisicao(
        @NotBlank(message = "é obrigatório") @Size(max = 60, message = "deve ter no máximo 60 caracteres") String nome,
        @NotNull(message = "é obrigatório") LocalDate dataInicio,
        @NotNull(message = "é obrigatório") LocalDate dataFim) {
}
