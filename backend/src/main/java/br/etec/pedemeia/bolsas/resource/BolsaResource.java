package br.etec.pedemeia.bolsas.resource;

import br.etec.pedemeia.bolsas.dto.BolsaResposta;
import br.etec.pedemeia.bolsas.dto.ConcessaoBolsaRequisicao;
import br.etec.pedemeia.bolsas.servico.BolsaServico;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/** Sem endpoint de cancelamento ou alteração de situação (MVP-CA30). */
@Path("/processos-seletivos/{processoId}/bolsas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BolsaResource {

    @Inject
    BolsaServico servico;

    @Inject
    UsuarioLogado usuarioLogado;

    @GET
    @RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA, Perfis.CONSULTA})
    public List<BolsaResposta> listar(@PathParam("processoId") long processoId) {
        return servico.listar(processoId, usuarioLogado.podeVerDadosPessoais());
    }

    @POST
    @RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA})
    public Response conceder(@PathParam("processoId") long processoId,
                             @Valid ConcessaoBolsaRequisicao requisicao) {
        List<BolsaResposta> criadas = servico.conceder(processoId, requisicao, usuarioLogado.id(),
                usuarioLogado.podeVerDadosPessoais());
        return Response.status(Response.Status.CREATED).entity(criadas).build();
    }
}
