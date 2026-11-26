package br.etec.pedemeia.comum.erro;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Erros HTTP do framework (segurança, rota inexistente, corpo mal formado) no formato único.
 * 401 e 403 de segurança passam por aqui (ADR-002).
 */
@Provider
public class MapeadorHttp implements ExceptionMapper<WebApplicationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(WebApplicationException e) {
        int status = e.getResponse() == null ? 500 : e.getResponse().getStatus();
        CodigoErro codigo = switch (status) {
            case 401 -> CodigoErro.NAO_AUTENTICADO;
            case 403 -> CodigoErro.ACESSO_NEGADO;
            case 404, 405 -> CodigoErro.RECURSO_NAO_ENCONTRADO;
            default -> status >= 500 ? CodigoErro.ERRO_INTERNO : CodigoErro.VALIDACAO_INVALIDA;
        };
        String mensagem = switch (codigo) {
            case NAO_AUTENTICADO -> "Autenticação obrigatória.";
            case ACESSO_NEGADO -> "Perfil sem permissão para esta operação.";
            case RECURSO_NAO_ENCONTRADO -> "Rota ou recurso não encontrado.";
            case ERRO_INTERNO -> "Erro interno.";
            default -> "Requisição inválida.";
        };
        return MapeadorErros.resposta(status, codigo, mensagem, uriInfo, null);
    }
}
