package br.etec.pedemeia.comum.configuracao;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Parâmetros de negócio e de inicialização (02-arquitetura.md, seção 10).
 * O valor da bolsa vive só aqui (D07): nenhuma regra fixa no fluxo.
 * O mapeamento cobre todas as chaves do prefixo, porque a validação é estrita.
 */
@ConfigMapping(prefix = "pedemeia")
public interface ConfiguracaoNegocio {

    Bolsa bolsa();

    Processo processo();

    Jwt jwt();

    Seed seed();

    Bootstrap bootstrap();

    interface Bolsa {
        /** Valor mensal padrão da bolsa (RN03, D07). */
        @WithDefault("300.00")
        BigDecimal valorMensal();
    }

    interface Processo {
        /** Vagas por ano escolar (RN01, P05). */
        @WithDefault("10")
        int vagasPorAno();
    }

    interface Jwt {
        @WithDefault("28800")
        long expiracaoSegundos();
    }

    interface Seed {
        @WithName("contas-teste")
        ContasTeste contasTeste();
    }

    interface ContasTeste {
        /** Ligado só em %dev e %test (D18, ADR-006). */
        @WithDefault("false")
        boolean ativo();

        @WithDefault("")
        String senha();
    }

    interface Bootstrap {
        Admin admin();
    }

    interface Admin {
        /** Vazio = bootstrap desligado (Q10). */
        Optional<String> email();

        Optional<String> senha();

        @WithDefault("Administrador")
        String nome();
    }
}
