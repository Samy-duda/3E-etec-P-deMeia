package br.etec.pedemeia.seguranca;

import br.etec.pedemeia.comum.configuracao.ConfiguracaoNegocio;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.Set;

/** Emite o JWT (ADR-006): sub = id, upn = e-mail, groups = perfil, iat e exp. */
@ApplicationScoped
public class TokenService {

    static final String EMISSOR = "pedemeia";

    private final ConfiguracaoNegocio configuracao;

    public TokenService(ConfiguracaoNegocio configuracao) {
        this.configuracao = configuracao;
    }

    public record Emitido(String token, Instant expiraEm) {
    }

    public Emitido emitir(long usuarioId, String email, String perfil) {
        Instant agora = Instant.now();
        Instant expiraEm = agora.plusSeconds(configuracao.jwt().expiracaoSegundos());
        String token = Jwt.issuer(EMISSOR)
                .subject(String.valueOf(usuarioId))
                .upn(email)
                .groups(Set.of(perfil))
                .issuedAt(agora.getEpochSecond())
                .expiresAt(expiraEm.getEpochSecond())
                .sign();
        return new Emitido(token, expiraEm);
    }
}
