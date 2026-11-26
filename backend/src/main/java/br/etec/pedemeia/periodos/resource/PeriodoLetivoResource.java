package br.etec.pedemeia.periodos.resource;

import br.etec.pedemeia.periodos.dto.PeriodoLetivoRequisicao;
import br.etec.pedemeia.periodos.dto.PeriodoLetivoResposta;
import br.etec.pedemeia.periodos.servico.PeriodoLetivoServico;
import br.etec.pedemeia.seguranca.Perfis;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/periodos-letivos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA})
public class PeriodoLetivoResource {

    @Inject
    PeriodoLetivoServico servico;

    @GET
    public List<PeriodoLetivoResposta> listar() {
        return servico.listar();
    }

    @POST
    public Response criar(@Valid PeriodoLetivoRequisicao requisicao) {
        return Response.status(Response.Status.CREATED).entity(servico.criar(requisicao)).build();
    }
}
