package br.etec.pedemeia.notas.servico;

import br.etec.pedemeia.comum.erro.CodigoErro;
import br.etec.pedemeia.comum.erro.NegocioException;
import br.etec.pedemeia.comum.paginacao.Pagina;
import br.etec.pedemeia.comum.paginacao.PaginaRequisicao;
import br.etec.pedemeia.matriculas.servico.MatriculaServico;
import br.etec.pedemeia.notas.Mapeador;
import br.etec.pedemeia.notas.dto.NotaCorrecaoRequisicao;
import br.etec.pedemeia.notas.dto.NotaRequisicao;
import br.etec.pedemeia.notas.dto.NotaResposta;
import br.etec.pedemeia.notas.entidade.NotaProva;
import br.etec.pedemeia.notas.repositorio.NotaRepositorio;
import br.etec.pedemeia.processos.servico.ProcessoSeletivoServico;
import br.etec.pedemeia.provas.entidade.Prova;
import br.etec.pedemeia.provas.servico.ProvaServico;
import br.etec.pedemeia.usuarios.servico.UsuarioServico;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Lançamento e correção de notas. Cada escrita trava a prova antes de ler qualquer coisa (ADR-004),
 * para que a geração da classificação e os lançamentos da mesma prova fiquem em fila.
 */
@ApplicationScoped
public class NotaServico {

    private final NotaRepositorio repositorio;
    private final ProvaServico provas;
    private final MatriculaServico matriculas;
    private final ProcessoSeletivoServico processos;
    private final UsuarioServico usuarios;

    public NotaServico(NotaRepositorio repositorio, ProvaServico provas, MatriculaServico matriculas,
                       ProcessoSeletivoServico processos, UsuarioServico usuarios) {
        this.repositorio = repositorio;
        this.provas = provas;
        this.matriculas = matriculas;
        this.processos = processos;
        this.usuarios = usuarios;
    }

    /**
     * Ordem de verificação do contrato (lancarNota): matrícula no período (422), intervalo (422),
     * duplicidade (409), classificação já gerada (409). A validação de formato (400) ocorre antes.
     */
    @Transactional
    public NotaResposta lancar(long provaId, NotaRequisicao requisicao, long usuarioId) {
        Prova prova = provas.bloquearPorId(provaId);
        var matricula = matriculas.obterEntidade(requisicao.matriculaId());
        if (!matricula.getPeriodo().getId().equals(prova.getPeriodo().getId())) {
            throw new NegocioException(CodigoErro.ALUNO_NAO_MATRICULADO_NO_PERIODO,
                    "O aluno não está matriculado no período da prova.");
        }
        if (requisicao.nota().compareTo(prova.getNotaMaxima()) > 0) {
            throw new NegocioException(CodigoErro.NOTA_FORA_DO_INTERVALO,
                    "A nota deve estar entre 0 e a nota máxima da prova (" + prova.getNotaMaxima().toPlainString() + ").");
        }
        if (repositorio.existeParaMatriculaNaProva(provaId, matricula.getId())) {
            throw new NegocioException(CodigoErro.NOTA_DUPLICADA, "Já existe nota deste aluno nesta prova.");
        }
        if (processos.classificacaoGeradaParaProva(provaId)) {
            throw new NegocioException(CodigoErro.CLASSIFICACAO_JA_GERADA,
                    "A classificação deste processo já foi gerada. Não é possível lançar notas.");
        }
        NotaProva nota = new NotaProva(prova, matricula, requisicao.nota(), usuarios.obterEntidade(usuarioId),
                Instant.now());
        repositorio.persist(nota);
        return Mapeador.paraResposta(nota);
    }

    /** Correção permitida só antes da classificação (Q07). Registra quem e quando (MVP-RNF06). */
    @Transactional
    public NotaResposta corrigir(long provaId, long notaId, NotaCorrecaoRequisicao requisicao, long usuarioId) {
        Prova prova = provas.bloquearPorId(provaId);
        NotaProva nota = repositorio.buscarNaProva(notaId, provaId)
                .orElseThrow(() -> new NegocioException(CodigoErro.RECURSO_NAO_ENCONTRADO, "Nota não encontrada."));
        if (requisicao.nota().compareTo(prova.getNotaMaxima()) > 0) {
            throw new NegocioException(CodigoErro.NOTA_FORA_DO_INTERVALO,
                    "A nota deve estar entre 0 e a nota máxima da prova (" + prova.getNotaMaxima().toPlainString() + ").");
        }
        if (processos.classificacaoGeradaParaProva(provaId)) {
            throw new NegocioException(CodigoErro.CLASSIFICACAO_JA_GERADA,
                    "A classificação deste processo já foi gerada. Não é possível corrigir notas.");
        }
        nota.corrigir(requisicao.nota(), usuarios.obterEntidade(usuarioId), Instant.now());
        return Mapeador.paraResposta(nota);
    }

    @Transactional
    public Pagina<NotaResposta> listar(long provaId, PaginaRequisicao paginacao) {
        provas.obterEntidade(provaId);
        var consulta = repositorio.listarPorProva(provaId);
        var conteudo = consulta.page(Page.of(paginacao.pagina(), paginacao.tamanho())).list()
                .stream().map(Mapeador::paraResposta).toList();
        return Pagina.de(conteudo, paginacao, consulta.count());
    }

    /** Usado pela geração da classificação, na mesma transação. */
    public List<NotaProva> listarEntidadesDaProva(long provaId) {
        return repositorio.listarTodasDaProva(provaId);
    }
}
