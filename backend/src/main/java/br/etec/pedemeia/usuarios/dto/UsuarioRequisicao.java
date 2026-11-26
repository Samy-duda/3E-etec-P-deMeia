package br.etec.pedemeia.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequisicao(
        @NotBlank(message = "é obrigatório") @Size(max = 120, message = "deve ter no máximo 120 caracteres") String nome,
        @NotBlank(message = "é obrigatório") @Size(max = 255, message = "deve ter no máximo 255 caracteres") String email,
        @NotBlank(message = "é obrigatório") @Size(min = 8, max = 128, message = "deve ter entre 8 e 128 caracteres") String senha,
        @NotNull(message = "é obrigatório") @Pattern(regexp = "ADMIN|SECRETARIA|CONSULTA", message = "deve ser ADMIN, SECRETARIA ou CONSULTA") String perfil) {
}
