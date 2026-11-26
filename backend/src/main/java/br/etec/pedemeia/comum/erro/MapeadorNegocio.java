package br.etec.pedemeia.comum.erro;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class MapeadorNegocio implements ExceptionMapper<NegocioException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(NegocioException e) {
        return MapeadorErros.resposta(e.codigo().status(), e.codigo(), e.getMessage(), uriInfo, e.campos());
    }
}
