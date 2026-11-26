package br.etec.pedemeia.bolsas.dto;

import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

/**
 * Corpo opcional. Sem corpo ou sem matriculaId, a concessão é em lote (MVP-CA24).
 * valorMensal, se informado, deve ser igual ao valor de configuração (D07, MVP-CA25).
 */
public record ConcessaoBolsaRequisicao(
        Long matriculaId,
        @Digits(integer = 8, fraction = 2, message = "deve ter até 8 dígitos inteiros e 2 decimais")
        BigDecimal valorMensal) {
}
