package br.etec.pedemeia.usuarios.servico;

import br.etec.pedemeia.comum.erro.CodigoErro;
import br.etec.pedemeia.comum.erro.NegocioException;
import br.etec.pedemeia.comum.paginacao.Pagina;
import br.etec.pedemeia.comum.paginacao.PaginaRequisicao;
import br.etec.pedemeia.seguranca.SenhaService;
import br.etec.pedemeia.usuarios.Mapeador;
import br.etec.pedemeia.usuarios.dto.UsuarioRequisicao;
import br.etec.pedemeia.usuarios.dto.UsuarioResposta;
import br.etec.pedemeia.usuarios.entidade.Usuario;
import br.etec.pedemeia.usuarios.repositorio.UsuarioRepositorio;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioServico {

    private final UsuarioRepositorio repositorio;
    private final SenhaService senhas;

    public UsuarioServico(UsuarioRepositorio repositorio, SenhaService senhas) {
        this.repositorio = repositorio;
        this.senhas = senhas;
    }

    @Transactional
    public UsuarioResposta criar(UsuarioRequisicao requisicao) {
        if (repositorio.buscarPorEmail(requisicao.email()).isPresent()) {
            throw new NegocioException(CodigoErro.EMAIL_DUPLICADO, "Já existe conta com este e-mail.");
        }
        Usuario usuario = new Usuario(requisicao.nome(), requisicao.email(), senhas.gerarHash(requisicao.senha()),
                requisicao.perfil(), Instant.now());
        repositorio.persist(usuario);
        return Mapeador.paraResposta(usuario);
    }

    @Transactional
    public Pagina<UsuarioResposta> listar(PaginaRequisicao paginacao) {
        List<UsuarioResposta> conteudo = repositorio.findAll()
                .page(Page.of(paginacao.pagina(), paginacao.tamanho()))
                .stream().map(Mapeador::paraResposta).toList();
        return Pagina.de(conteudo, paginacao, repositorio.count());
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repositorio.buscarPorEmail(email);
    }

    /** Usado por outros módulos (criada_por, lancada_por). Falha se o usuário não existir. */
    public Usuario obterEntidade(long id) {
        return repositorio.findByIdOptional(id)
                .orElseThrow(() -> new NegocioException(CodigoErro.RECURSO_NAO_ENCONTRADO, "Usuário não encontrado."));
    }

    public Optional<Usuario> buscarAtivoPorId(long id) {
        return repositorio.findByIdOptional(id).filter(Usuario::isAtivo);
    }

    public long contar() {
        return repositorio.count();
    }

    /** Cria a conta de teste se não existir (D18). Não altera conta existente. */
    @Transactional
    public void garantirConta(String nome, String email, String senha, String perfil) {
        if (repositorio.buscarPorEmail(email).isEmpty()) {
            repositorio.persist(new Usuario(nome, email, senhas.gerarHash(senha), perfil, Instant.now()));
        }
    }
}
