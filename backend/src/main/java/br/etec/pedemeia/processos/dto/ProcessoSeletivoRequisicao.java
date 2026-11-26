package br.etec.pedemeia.processos.dto;

import jakarta.validation.constraints.NotNull;

public record ProcessoSeletivoRequisicao(
        @NotNull(message = "é obrigatório") Long periodoLetivoId,
        @NotNull(message = "é obrigatório") Long provaId) {
}
