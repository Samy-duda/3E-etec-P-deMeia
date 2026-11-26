package br.etec.pedemeia.seguranca;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;

/** Hash e conferência de senha com BCrypt (ADR-006). A senha nunca é gravada nem logada. */
@ApplicationScoped
public class SenhaService {

    public String gerarHash(String senha) {
        return BcryptUtil.bcryptHash(senha);
    }

    public boolean confere(String senha, String hash) {
        return senha != null && hash != null && BcryptUtil.matches(senha, hash);
    }
}
