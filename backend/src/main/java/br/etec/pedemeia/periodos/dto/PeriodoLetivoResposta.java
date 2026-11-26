package br.etec.pedemeia.periodos.dto;

import java.time.LocalDate;

public record PeriodoLetivoResposta(Long id, String nome, LocalDate dataInicio, LocalDate dataFim, boolean ativo) {
}
