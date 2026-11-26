package br.etec.pedemeia.provas.resource;

import br.etec.pedemeia.comum.paginacao.Pagina;
import br.etec.pedemeia.comum.paginacao.PaginaRequisicao;
import br.etec.pedemeia.provas.dto.ProvaRequisicao;
import br.etec.pedemeia.provas.dto.ProvaResposta;
import br.etec.pedemeia.provas.servico.ProvaServico;
import br.etec.pedemeia.seguranca.Perfis;
import br.etec.pedemeia.seguranca.UsuarioLogado;
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

@Path("/provas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA})
public class ProvaResource {

    @Inject
    ProvaServico servico;

    @Inject
    UsuarioLogado usuarioLogado;

    @GET
    public Pagina<ProvaResposta> listar(@QueryParam("pagina") Integer pagina,
                                        @QueryParam("tamanho") Integer tamanho,
                                        @QueryParam("periodoLetivoId") Long periodoLetivoId) {
        return servico.listar(periodoLetivoId, PaginaRequisicao.de(pagina, tamanho));
    }

    @POST
    public Response cadastrar(@Valid ProvaRequisicao requisicao) {
        return Response.status(Response.Status.CREATED)
                .entity(servico.cadastrar(requisicao, usuarioLogado.id())).build();
    }

    @GET
    @Path("/{provaId}")
    public ProvaResposta consultar(@PathParam("provaId") long provaId) {
        return servico.consultar(provaId);
    }
}
