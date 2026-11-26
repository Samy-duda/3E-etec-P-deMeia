package br.etec.pedemeia.provas.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ProvaResposta(
        Long id,
        Long periodoLetivoId,
        String titulo,
        String descricao,
        LocalDate dataAplicacao,
        BigDecimal notaMaxima,
        Long criadaPorId,
        Instant criadaEm) {
}
