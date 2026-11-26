package br.etec.pedemeia.alunos.dto;

import java.time.Instant;
import java.time.LocalDate;

public record AlunoResposta(
        Long id,
        String nome,
        String matriculaEscolar,
        LocalDate dataNascimento,
        String email,
        String telefone,
        boolean ativo,
        Instant criadoEm) {
}
