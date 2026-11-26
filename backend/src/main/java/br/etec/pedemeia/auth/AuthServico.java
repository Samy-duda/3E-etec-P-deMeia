package br.etec.pedemeia.auth;

import br.etec.pedemeia.auth.dto.LoginRequisicao;
import br.etec.pedemeia.auth.dto.LoginResposta;
import br.etec.pedemeia.auth.dto.UsuarioResumoResposta;
import br.etec.pedemeia.comum.erro.CodigoErro;
import br.etec.pedemeia.comum.erro.NegocioException;
import br.etec.pedemeia.seguranca.SenhaService;
import br.etec.pedemeia.seguranca.TokenService;
import br.etec.pedemeia.usuarios.entidade.Usuario;
import br.etec.pedemeia.usuarios.servico.UsuarioServico;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthServico {

    private final UsuarioServico usuarios;
    private final SenhaService senhas;
    private final TokenService tokens;

    public AuthServico(UsuarioServico usuarios, SenhaService senhas, TokenService tokens) {
        this.usuarios = usuarios;
        this.senhas = senhas;
        this.tokens = tokens;
    }

    public LoginResposta autenticar(LoginRequisicao requisicao) {
        // Mesma mensagem para e-mail inexistente, senha errada e conta inativa.
        Usuario usuario = usuarios.buscarPorEmail(requisicao.email())
                .filter(Usuario::isAtivo)
                .filter(u -> senhas.confere(requisicao.senha(), u.getSenhaHash()))
                .orElseThrow(() -> new NegocioException(CodigoErro.CREDENCIAIS_INVALIDAS,
                        "E-mail ou senha inválidos."));
        TokenService.Emitido emitido = tokens.emitir(usuario.getId(), usuario.getEmail(), usuario.getPerfil());
        return new LoginResposta(emitido.token(), "Bearer", emitido.expiraEm(), resumo(usuario));
    }

    public UsuarioResumoResposta usuarioAtual(long id) {
        Usuario usuario = usuarios.buscarAtivoPorId(id)
                .orElseThrow(() -> new NegocioException(CodigoErro.NAO_AUTENTICADO, "Autenticação obrigatória."));
        return resumo(usuario);
    }

    static UsuarioResumoResposta resumo(Usuario u) {
        return new UsuarioResumoResposta(u.getId(), u.getNome(), u.getEmail(), u.getPerfil());
    }
}
