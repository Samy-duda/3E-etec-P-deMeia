package br.etec.pedemeia.comum.erro;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rede de segurança: violação de UNIQUE do banco vira 409 pelo nome da constraint
 * (02-arquitetura.md, seção 7). Qualquer outra falha vira 500 ERRO_INTERNO sem detalhe.
 */
@Provider
public class MapeadorInesperado implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(MapeadorInesperado.class.getName());

    static final Map<String, CodigoErro> CONSTRAINTS = Map.of(
            "uk_alunos_matricula_escolar", CodigoErro.ALUNO_DUPLICADO,
            "uk_usuarios_email", CodigoErro.EMAIL_DUPLICADO,
            "uk_matriculas_aluno_periodo", CodigoErro.MATRICULA_DUPLICADA,
            "uk_notas_prova_matricula", CodigoErro.NOTA_DUPLICADA,
            "uk_processos_prova", CodigoErro.PROCESSO_JA_EXISTE_PARA_PROVA,
            "uk_classificacoes_processo_matricula", CodigoErro.CLASSIFICACAO_JA_GERADA,
            "uk_bolsas_matricula_processo", CodigoErro.BOLSA_JA_EXISTE);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable e) {
        CodigoErro duplicidade = duplicidadeDoBanco(e);
        if (duplicidade != null) {
            return MapeadorErros.resposta(duplicidade.status(), duplicidade, mensagemDuplicidade(duplicidade),
                    uriInfo, null);
        }
        LOG.log(Level.SEVERE, "Erro inesperado", e);
        return MapeadorErros.resposta(500, CodigoErro.ERRO_INTERNO, "Erro interno.", uriInfo, null);
    }

    static CodigoErro duplicidadeDoBanco(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql && "23000".equals(sql.getSQLState())) {
                for (Map.Entry<String, CodigoErro> entrada : CONSTRAINTS.entrySet()) {
                    if (String.valueOf(sql.getMessage()).contains(entrada.getKey())) {
                        return entrada.getValue();
                    }
                }
            }
            if (t.getMessage() != null) {
                for (Map.Entry<String, CodigoErro> entrada : CONSTRAINTS.entrySet()) {
                    if (t.getMessage().contains(entrada.getKey())) {
                        return entrada.getValue();
                    }
                }
            }
        }
        return null;
    }

    private static String mensagemDuplicidade(CodigoErro codigo) {
        return switch (codigo) {
            case ALUNO_DUPLICADO -> "Já existe aluno com esta matrícula escolar.";
            case EMAIL_DUPLICADO -> "Já existe conta com este e-mail.";
            case MATRICULA_DUPLICADA -> "Aluno já matriculado neste período letivo.";
            case NOTA_DUPLICADA -> "Já existe nota deste aluno nesta prova.";
            case PROCESSO_JA_EXISTE_PARA_PROVA -> "Já existe processo seletivo para esta prova.";
            case CLASSIFICACAO_JA_GERADA -> "A classificação deste processo já foi gerada.";
            case BOLSA_JA_EXISTE -> "Aluno já tem bolsa neste processo.";
            default -> "Conflito com registro existente.";
        };
    }
}
