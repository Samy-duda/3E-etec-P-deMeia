package br.etec.pedemeia.usuarios.dto;

import java.time.Instant;

/** Conta sem a senha (o contrato não devolve senha). */
public record UsuarioResposta(Long id, String nome, String email, String perfil, boolean ativo, Instant criadoEm) {
}
