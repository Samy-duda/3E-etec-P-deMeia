package br.etec.pedemeia.matriculas.resource;

import br.etec.pedemeia.comum.paginacao.Pagina;
import br.etec.pedemeia.comum.paginacao.PaginaRequisicao;
import br.etec.pedemeia.matriculas.dto.MatriculaRequisicao;
import br.etec.pedemeia.matriculas.dto.MatriculaResposta;
import br.etec.pedemeia.matriculas.servico.MatriculaServico;
import br.etec.pedemeia.seguranca.Perfis;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/periodos-letivos/{periodoId}/matriculas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA})
public class MatriculaResource {

    @Inject
    MatriculaServico servico;

    @GET
    public Pagina<MatriculaResposta> listar(@PathParam("periodoId") long periodoId,
                                            @QueryParam("pagina") Integer pagina,
                                            @QueryParam("tamanho") Integer tamanho) {
        return servico.listarPorPeriodo(periodoId, PaginaRequisicao.de(pagina, tamanho));
    }

    @POST
    public Response matricular(@PathParam("periodoId") long periodoId, @Valid MatriculaRequisicao requisicao) {
        return Response.status(Response.Status.CREATED).entity(servico.matricular(periodoId, requisicao)).build();
    }
}
