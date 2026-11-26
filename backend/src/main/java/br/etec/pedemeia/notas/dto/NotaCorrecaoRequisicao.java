package br.etec.pedemeia.notas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record NotaCorrecaoRequisicao(
        @NotNull(message = "é obrigatório")
        @DecimalMin(value = "0", message = "não pode ser negativa")
        @Digits(integer = 3, fraction = 2, message = "deve ter até 3 dígitos inteiros e 2 decimais")
        BigDecimal nota) {
}
