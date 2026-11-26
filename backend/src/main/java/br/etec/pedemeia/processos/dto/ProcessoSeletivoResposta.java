package br.etec.pedemeia.processos.dto;

public record ProcessoSeletivoResposta(
        Long id,
        Long periodoLetivoId,
        Long provaId,
        String status,
        Long criadoPorId) {
}
