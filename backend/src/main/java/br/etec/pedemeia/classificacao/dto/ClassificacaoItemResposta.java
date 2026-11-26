package br.etec.pedemeia.classificacao.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/** matriculaEscolar só vai para ADMIN e SECRETARIA (Q03, ADR-006). Para CONSULTA, o campo não sai no JSON. */
public record ClassificacaoItemResposta(
        Long matriculaId,
        int anoEscolar,
        int posicao,
        BigDecimal notaFinal,
        boolean selecionado,
        String nomeAluno,
        @JsonInclude(JsonInclude.Include.NON_NULL) String matriculaEscolar) {
}
