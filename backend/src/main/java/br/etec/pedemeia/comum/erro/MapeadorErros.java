package br.etec.pedemeia.comum.erro;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

/** Monta a resposta ErroResposta. Usado pelos mapeadores de exceção. */
final class MapeadorErros {

    private MapeadorErros() {
    }

    static Response resposta(int status, CodigoErro codigo, String mensagem, UriInfo uriInfo,
                             List<CampoErro> campos) {
        String caminho = uriInfo == null ? "" : uriInfo.getRequestUri().getPath();
        ErroResposta corpo = ErroResposta.de(status, codigo, mensagem, caminho, campos);
        return Response.status(status).type(MediaType.APPLICATION_JSON_TYPE).entity(corpo).build();
    }
}
