package br.etec.pedemeia.auth.dto;

import java.time.Instant;

public record LoginResposta(String token, String tipo, Instant expiraEm, UsuarioResumoResposta usuario) {
}
