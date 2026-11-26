package br.etec.pedemeia.usuarios;

import br.etec.pedemeia.seguranca.Perfis;
import br.etec.pedemeia.usuarios.servico.UsuarioServico;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Contas de teste (D18, ADR-006). Só cria se pedemeia.seed.contas-teste.ativo=true,
 * que está ligado apenas em %dev e %test. Nunca usa conta pessoal.
 */
@Startup
@ApplicationScoped
public class ContasTesteSeed {

    @ConfigProperty(name = "pedemeia.seed.contas-teste.ativo", defaultValue = "false")
    boolean ativo;

    @ConfigProperty(name = "pedemeia.seed.contas-teste.senha", defaultValue = "")
    String senha;

    @Inject
    UsuarioServico usuarios;

    @PostConstruct
    void criarContas() {
        if (!ativo) {
            return;
        }
        usuarios.garantirConta("Teste IA Admin", "test@IA_admin", senha, Perfis.ADMIN);
        usuarios.garantirConta("Teste IA Secretaria", "test@IA_secretaria", senha, Perfis.SECRETARIA);
        usuarios.garantirConta("Teste IA Consulta", "test@IA_consulta", senha, Perfis.CONSULTA);
    }
}
