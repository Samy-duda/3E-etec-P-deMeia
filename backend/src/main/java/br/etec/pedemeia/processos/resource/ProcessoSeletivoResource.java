package br.etec.pedemeia.processos.resource;

import br.etec.pedemeia.comum.paginacao.Pagina;
import br.etec.pedemeia.comum.paginacao.PaginaRequisicao;
import br.etec.pedemeia.processos.dto.ProcessoSeletivoRequisicao;
import br.etec.pedemeia.processos.dto.ProcessoSeletivoResposta;
import br.etec.pedemeia.processos.servico.ProcessoSeletivoServico;
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

@Path("/processos-seletivos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProcessoSeletivoResource {

    @Inject
    ProcessoSeletivoServico servico;

    @Inject
    UsuarioLogado usuarioLogado;

    /** CONSULTA lista processos para navegar até classificação e bolsas (x-perfis). */
    @GET
    @RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA, Perfis.CONSULTA})
    public Pagina<ProcessoSeletivoResposta> listar(@QueryParam("pagina") Integer pagina,
                                                   @QueryParam("tamanho") Integer tamanho,
                                                   @QueryParam("periodoLetivoId") Long periodoLetivoId) {
        return servico.listar(periodoLetivoId, PaginaRequisicao.de(pagina, tamanho));
    }

    @POST
    @RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA})
    public Response criar(@Valid ProcessoSeletivoRequisicao requisicao) {
        return Response.status(Response.Status.CREATED)
                .entity(servico.criar(requisicao, usuarioLogado.id())).build();
    }

    @GET
    @Path("/{processoId}")
    @RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA, Perfis.CONSULTA})
    public ProcessoSeletivoResposta consultar(@PathParam("processoId") long processoId) {
        return servico.consultar(processoId);
    }
}
