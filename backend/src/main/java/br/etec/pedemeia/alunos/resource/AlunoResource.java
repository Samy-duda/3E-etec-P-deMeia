package br.etec.pedemeia.alunos.resource;

import br.etec.pedemeia.alunos.dto.AlunoRequisicao;
import br.etec.pedemeia.alunos.dto.AlunoResposta;
import br.etec.pedemeia.alunos.servico.AlunoServico;
import br.etec.pedemeia.comum.paginacao.Pagina;
import br.etec.pedemeia.comum.paginacao.PaginaRequisicao;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/alunos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA})
public class AlunoResource {

    @Inject
    AlunoServico servico;

    @Context
    UriInfo uriInfo;

    @GET
    public Pagina<AlunoResposta> listar(@QueryParam("pagina") Integer pagina,
                                        @QueryParam("tamanho") Integer tamanho,
                                        @QueryParam("matriculaEscolar") String matriculaEscolar,
                                        @QueryParam("nome") String nome) {
        return servico.listar(matriculaEscolar, nome, PaginaRequisicao.de(pagina, tamanho));
    }

    @POST
    public Response cadastrar(@Valid AlunoRequisicao requisicao) {
        AlunoResposta criado = servico.cadastrar(requisicao);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(criado.id())).build())
                .entity(criado).build();
    }

    @GET
    @Path("/{alunoId}")
    public AlunoResposta consultar(@PathParam("alunoId") long alunoId) {
        return servico.consultar(alunoId);
    }
}
