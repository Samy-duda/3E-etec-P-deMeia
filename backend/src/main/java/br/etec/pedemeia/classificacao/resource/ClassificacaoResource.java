package br.etec.pedemeia.classificacao.resource;

import br.etec.pedemeia.classificacao.dto.ClassificacaoResposta;
import br.etec.pedemeia.classificacao.servico.ClassificacaoServico;
import br.etec.pedemeia.seguranca.Perfis;
import br.etec.pedemeia.seguranca.UsuarioLogado;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/processos-seletivos/{processoId}/classificacao")
@Produces(MediaType.APPLICATION_JSON)
public class ClassificacaoResource {

    @Inject
    ClassificacaoServico servico;

    @Inject
    UsuarioLogado usuarioLogado;

    @GET
    @RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA, Perfis.CONSULTA})
    public ClassificacaoResposta consultar(@PathParam("processoId") long processoId,
                                           @QueryParam("anoEscolar") Integer anoEscolar) {
        return servico.consultar(processoId, anoEscolar, usuarioLogado.podeVerDadosPessoais());
    }

    @POST
    @RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA})
    public Response gerar(@PathParam("processoId") long processoId) {
        return Response.status(Response.Status.CREATED)
                .entity(servico.gerar(processoId, usuarioLogado.podeVerDadosPessoais())).build();
    }
}
