package br.etec.pedemeia.comum.erro;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.ArrayList;
import java.util.List;

/** Bean Validation: 400 VALIDACAO_INVALIDA com os campos afetados. */
@Provider
public class MapeadorValidacao implements ExceptionMapper<ConstraintViolationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException e) {
        List<CampoErro> campos = new ArrayList<>();
        for (ConstraintViolation<?> violacao : e.getConstraintViolations()) {
            String caminho = violacao.getPropertyPath().toString();
            int ultimo = caminho.lastIndexOf('.');
            String campo = ultimo >= 0 ? caminho.substring(ultimo + 1) : caminho;
            campos.add(new CampoErro(campo, violacao.getMessage()));
        }
        return MapeadorErros.resposta(400, CodigoErro.VALIDACAO_INVALIDA, "Dados inválidos.", uriInfo, campos);
    }
}
