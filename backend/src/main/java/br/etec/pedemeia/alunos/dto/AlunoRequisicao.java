package br.etec.pedemeia.alunos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** LGPD mínima (MVP-RNF02): nome, matrícula escolar e data de nascimento obrigatórios; contatos opcionais. */
public record AlunoRequisicao(
        @NotBlank(message = "é obrigatório") @Size(max = 120, message = "deve ter no máximo 120 caracteres") String nome,
        @NotBlank(message = "é obrigatório") @Size(max = 30, message = "deve ter no máximo 30 caracteres") String matriculaEscolar,
        @NotNull(message = "é obrigatório") LocalDate dataNascimento,
        @Size(max = 255, message = "deve ter no máximo 255 caracteres") String email,
        @Size(max = 20, message = "deve ter no máximo 20 caracteres") String telefone) {
}
