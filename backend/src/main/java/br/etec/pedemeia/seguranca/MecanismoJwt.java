package br.etec.pedemeia.seguranca;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.HashSet;

/**
 * Autenticação por Bearer JWT (ADR-006, D02).
 * Valida o token com o JWTParser (assinatura, emissor e expiração). Token ausente ou inválido vira
 * identidade anônima, e a recusa acontece na camada JAX-RS (@RolesAllowed). Assim o 401 sai no
 * formato único de erro (ADR-002), e não como resposta vazia da camada HTTP.
 */
@ApplicationScoped
public class MecanismoJwt implements HttpAuthenticationMechanism {

    /** Prioridade acima da do JWT padrão do Quarkus (1000): este mecanismo decide sozinho. */
    static final int PRIORIDADE = 3000;

    private static final String PREFIXO = "Bearer ";

    @Inject
    JWTParser parser;

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext contexto, IdentityProviderManager gerenciador) {
        String cabecalho = contexto.request().getHeader("Authorization");
        if (cabecalho == null || !cabecalho.startsWith(PREFIXO)) {
            return anonima();
        }
        try {
            JsonWebToken jwt = parser.parse(cabecalho.substring(PREFIXO.length()).trim());
            return Uni.createFrom().item(QuarkusSecurityIdentity.builder()
                    .setPrincipal(jwt)
                    .addRoles(new HashSet<>(jwt.getGroups()))
                    .build());
        } catch (Exception e) {
            return anonima();
        }
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext contexto) {
        return Uni.createFrom().item(new ChallengeData(401, "WWW-Authenticate", "Bearer"));
    }

    @Override
    public int getPriority() {
        return PRIORIDADE;
    }

    private static Uni<SecurityIdentity> anonima() {
        return Uni.createFrom().item(QuarkusSecurityIdentity.builder().setAnonymous(true).build());
    }
}
