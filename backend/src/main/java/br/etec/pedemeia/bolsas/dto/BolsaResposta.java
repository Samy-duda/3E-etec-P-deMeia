package br.etec.pedemeia.bolsas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** matriculaEscolar, criadaPorId e criadaEm só vão para ADMIN e SECRETARIA (contrato, ADR-006). */
public record BolsaResposta(
        Long id,
        Long processoSeletivoId,
        Long matriculaId,
        int anoEscolar,
        int posicao,
        String nomeAluno,
        @JsonInclude(JsonInclude.Include.NON_NULL) String matriculaEscolar,
        BigDecimal valorMensal,
        String situacao,
        LocalDate dataInicio,
        LocalDate dataFim,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long criadaPorId,
        @JsonInclude(JsonInclude.Include.NON_NULL) Instant criadaEm) {
}
