package br.etec.pedemeia.notas.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record NotaResposta(
        Long id,
        Long provaId,
        Long matriculaId,
        String nomeAluno,
        String matriculaEscolar,
        BigDecimal nota,
        Long lancadaPorId,
        Instant lancadaEm,
        Long alteradaPorId,
        Instant alteradaEm) {
}
