package br.etec.pedemeia.comum.erro;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

/**
 * Os 401 e 403 de segurança do Quarkus (mapeadores internos do RESTEasy Reactive) saem sem corpo.
 * Este filtro preenche o corpo com ErroResposta, no formato único (ADR-002).
 * Não altera o status nem os cabeçalhos (ex.: WWW-Authenticate).
 */
@Provider
public class FiltroErroSeguranca implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requisicao, ContainerResponseContext resposta) {
        int status = resposta.getStatus();
        if ((status != 401 && status != 403) || resposta.hasEntity()) {
            return;
        }
        CodigoErro codigo = status == 401 ? CodigoErro.NAO_AUTENTICADO : CodigoErro.ACESSO_NEGADO;
        String mensagem = status == 401 ? "Autenticação obrigatória." : "Perfil sem permissão para esta operação.";
        resposta.setEntity(ErroResposta.de(status, codigo, mensagem,
                requisicao.getUriInfo().getRequestUri().getPath(), List.of()));
        resposta.getHeaders().putSingle("Content-Type", MediaType.APPLICATION_JSON);
    }
}
