package br.etec.pedemeia.classificacao.dto;

import java.time.Instant;
import java.util.List;

/** calculadaEm é nulo e itens vazio enquanto a classificação não foi gerada. */
public record ClassificacaoResposta(
        Long processoSeletivoId,
        Instant calculadaEm,
        int vagasPorAno,
        List<ClassificacaoItemResposta> itens) {
}
