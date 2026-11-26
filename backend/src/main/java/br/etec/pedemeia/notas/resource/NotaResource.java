package br.etec.pedemeia.notas.resource;

import br.etec.pedemeia.comum.paginacao.Pagina;
import br.etec.pedemeia.comum.paginacao.PaginaRequisicao;
import br.etec.pedemeia.notas.dto.NotaCorrecaoRequisicao;
import br.etec.pedemeia.notas.dto.NotaRequisicao;
import br.etec.pedemeia.notas.dto.NotaResposta;
import br.etec.pedemeia.notas.servico.NotaServico;
import br.etec.pedemeia.seguranca.Perfis;
import br.etec.pedemeia.seguranca.UsuarioLogado;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/provas/{provaId}/notas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA})
public class NotaResource {

    @Inject
    NotaServico servico;

    @Inject
    UsuarioLogado usuarioLogado;

    @GET
    public Pagina<NotaResposta> listar(@PathParam("provaId") long provaId,
                                       @QueryParam("pagina") Integer pagina,
                                       @QueryParam("tamanho") Integer tamanho) {
        return servico.listar(provaId, PaginaRequisicao.de(pagina, tamanho));
    }

    @POST
    public Response lancar(@PathParam("provaId") long provaId, @Valid NotaRequisicao requisicao) {
        return Response.status(Response.Status.CREATED)
                .entity(servico.lancar(provaId, requisicao, usuarioLogado.id())).build();
    }

    @PUT
    @Path("/{notaId}")
    public NotaResposta corrigir(@PathParam("provaId") long provaId, @PathParam("notaId") long notaId,
                                 @Valid NotaCorrecaoRequisicao requisicao) {
        return servico.corrigir(provaId, notaId, requisicao, usuarioLogado.id());
    }
}
