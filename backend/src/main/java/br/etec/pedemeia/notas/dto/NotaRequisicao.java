package br.etec.pedemeia.notas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Valor negativo ou ausente é VALIDACAO_INVALIDA (400). Acima da nota máxima é regra de negócio (422). */
public record NotaRequisicao(
        @NotNull(message = "é obrigatório") Long matriculaId,
        @NotNull(message = "é obrigatório")
        @DecimalMin(value = "0", message = "não pode ser negativa")
        @Digits(integer = 3, fraction = 2, message = "deve ter até 3 dígitos inteiros e 2 decimais")
        BigDecimal nota) {
}
