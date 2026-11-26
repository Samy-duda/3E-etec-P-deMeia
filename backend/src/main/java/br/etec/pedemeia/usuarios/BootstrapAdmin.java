package br.etec.pedemeia.usuarios;

import br.etec.pedemeia.comum.configuracao.ConfiguracaoNegocio;
import br.etec.pedemeia.seguranca.Perfis;
import br.etec.pedemeia.usuarios.servico.UsuarioServico;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.logging.Logger;

/**
 * Cria o primeiro ADMIN a partir de variáveis de ambiente, só se USUARIOS estiver vazia (Q10, ADR-006).
 */
@Startup
@ApplicationScoped
public class BootstrapAdmin {

    private static final Logger LOG = Logger.getLogger(BootstrapAdmin.class.getName());

    private final ConfiguracaoNegocio configuracao;
    private final UsuarioServico usuarios;

    public BootstrapAdmin(ConfiguracaoNegocio configuracao, UsuarioServico usuarios) {
        this.configuracao = configuracao;
        this.usuarios = usuarios;
    }

    @PostConstruct
    void criarPrimeiroAdmin() {
        var admin = configuracao.bootstrap().admin();
        var email = admin.email().filter(v -> !v.isBlank());
        var senha = admin.senha().filter(v -> !v.isBlank());
        if (email.isEmpty() || senha.isEmpty() || usuarios.contar() > 0) {
            return;
        }
        usuarios.garantirConta(admin.nome(), email.get(), senha.get(), Perfis.ADMIN);
        LOG.info("Primeiro ADMIN criado a partir da configuração de bootstrap.");
    }
}
