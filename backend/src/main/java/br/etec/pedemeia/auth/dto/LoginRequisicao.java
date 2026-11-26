package br.etec.pedemeia.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Sem @Email: o domínio das contas de teste tem "_" (ADR-006). */
public record LoginRequisicao(
        @NotBlank(message = "é obrigatório") @Size(max = 255, message = "deve ter no máximo 255 caracteres") String email,
        @NotBlank(message = "é obrigatório") @Size(max = 128, message = "deve ter no máximo 128 caracteres") String senha) {
}
