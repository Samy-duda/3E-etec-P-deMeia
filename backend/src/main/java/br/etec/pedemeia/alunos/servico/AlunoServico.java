package br.etec.pedemeia.alunos.servico;

import br.etec.pedemeia.alunos.Mapeador;
import br.etec.pedemeia.alunos.dto.AlunoRequisicao;
import br.etec.pedemeia.alunos.dto.AlunoResposta;
import br.etec.pedemeia.alunos.entidade.Aluno;
import br.etec.pedemeia.alunos.repositorio.AlunoRepositorio;
import br.etec.pedemeia.comum.erro.CodigoErro;
import br.etec.pedemeia.comum.erro.NegocioException;
import br.etec.pedemeia.comum.paginacao.Pagina;
import br.etec.pedemeia.comum.paginacao.PaginaRequisicao;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;

@ApplicationScoped
public class AlunoServico {

    private final AlunoRepositorio repositorio;

    public AlunoServico(AlunoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /** Duplicidade pela matrícula escolar (PROPOSTA-C, Q02). Recusa sem alterar o registro original. */
    @Transactional
    public AlunoResposta cadastrar(AlunoRequisicao requisicao) {
        if (repositorio.buscarPorMatriculaEscolar(requisicao.matriculaEscolar()).isPresent()) {
            throw new NegocioException(CodigoErro.ALUNO_DUPLICADO, "Já existe aluno com esta matrícula escolar.");
        }
        Aluno aluno = new Aluno(requisicao.nome(), requisicao.matriculaEscolar(), requisicao.dataNascimento(),
                requisicao.email(), requisicao.telefone(), Instant.now());
        repositorio.persist(aluno);
        return Mapeador.paraResposta(aluno);
    }

    @Transactional
    public AlunoResposta consultar(long id) {
        return Mapeador.paraResposta(obterEntidade(id));
    }

    @Transactional
    public Pagina<AlunoResposta> listar(String matriculaEscolar, String nome, PaginaRequisicao paginacao) {
        var consulta = repositorio.buscar(matriculaEscolar, nome);
        var conteudo = consulta.page(Page.of(paginacao.pagina(), paginacao.tamanho())).list()
                .stream().map(Mapeador::paraResposta).toList();
        return Pagina.de(conteudo, paginacao, consulta.count());
    }

    /** Usado por outros módulos (matrícula). */
    public Aluno obterEntidade(long id) {
        return repositorio.findByIdOptional(id)
                .orElseThrow(() -> new NegocioException(CodigoErro.RECURSO_NAO_ENCONTRADO, "Aluno não encontrado."));
    }
}
