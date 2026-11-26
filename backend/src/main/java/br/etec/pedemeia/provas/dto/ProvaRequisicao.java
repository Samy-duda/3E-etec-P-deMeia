package br.etec.pedemeia.provas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Nota máxima maior que zero, com duas casas decimais (P04, MVP-CA09). */
public record ProvaRequisicao(
        @NotNull(message = "é obrigatório") Long periodoLetivoId,
        @NotBlank(message = "é obrigatório") @Size(max = 150, message = "deve ter no máximo 150 caracteres") String titulo,
        @Size(max = 10000, message = "deve ter no máximo 10000 caracteres") String descricao,
        @NotNull(message = "é obrigatório") LocalDate dataAplicacao,
        @NotNull(message = "é obrigatório")
        @DecimalMin(value = "0", inclusive = false, message = "deve ser maior que zero")
        @Digits(integer = 3, fraction = 2, message = "deve ter até 3 dígitos inteiros e 2 decimais")
        BigDecimal notaMaxima) {
}
