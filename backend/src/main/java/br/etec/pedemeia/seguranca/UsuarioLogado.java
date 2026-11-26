package br.etec.pedemeia.seguranca;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Usuário do token da requisição. Só deve ser usado em métodos já protegidos por @RolesAllowed.
 */
@RequestScoped
public class UsuarioLogado {

    private final JsonWebToken jwt;
    private final SecurityIdentity identidade;

    @Inject
    public UsuarioLogado(JsonWebToken jwt, SecurityIdentity identidade) {
        this.jwt = jwt;
        this.identidade = identidade;
    }

    /** Id do usuário (claim sub). Usado em criada_por, lancada_por e similares (MVP-RNF06). */
    public long id() {
        return Long.parseLong(jwt.getSubject());
    }

    /** Perfis ADMIN e SECRETARIA veem dados pessoais (ADR-006, Q03). CONSULTA não vê. */
    public boolean podeVerDadosPessoais() {
        return identidade.hasRole("ADMIN") || identidade.hasRole("SECRETARIA");
    }
}
