package br.etec.pedemeia.auth;

import br.etec.pedemeia.auth.dto.LoginRequisicao;
import br.etec.pedemeia.auth.dto.LoginResposta;
import br.etec.pedemeia.auth.dto.UsuarioResumoResposta;
import br.etec.pedemeia.seguranca.Perfis;
import br.etec.pedemeia.seguranca.UsuarioLogado;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthServico servico;

    @Inject
    UsuarioLogado usuarioLogado;

    @POST
    @Path("/login")
    @PermitAll
    public LoginResposta autenticar(@Valid LoginRequisicao requisicao) {
        return servico.autenticar(requisicao);
    }

    @GET
    @Path("/me")
    @RolesAllowed({Perfis.ADMIN, Perfis.SECRETARIA, Perfis.CONSULTA})
    public UsuarioResumoResposta usuarioAtual() {
        return servico.usuarioAtual(usuarioLogado.id());
    }
}
