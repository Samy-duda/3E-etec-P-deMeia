package br.etec.pedemeia.usuarios.resource;

import br.etec.pedemeia.comum.paginacao.Pagina;
import br.etec.pedemeia.comum.paginacao.PaginaRequisicao;
import br.etec.pedemeia.seguranca.Perfis;
import br.etec.pedemeia.usuarios.dto.UsuarioRequisicao;
import br.etec.pedemeia.usuarios.dto.UsuarioResposta;
import br.etec.pedemeia.usuarios.servico.UsuarioServico;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(Perfis.ADMIN)
public class UsuarioResource {

    @Inject
    UsuarioServico servico;

    @GET
    public Pagina<UsuarioResposta> listar(@QueryParam("pagina") Integer pagina,
                                          @QueryParam("tamanho") Integer tamanho) {
        return servico.listar(PaginaRequisicao.de(pagina, tamanho));
    }

    @POST
    public Response criar(@Valid UsuarioRequisicao requisicao) {
        return Response.status(Response.Status.CREATED).entity(servico.criar(requisicao)).build();
    }
}
