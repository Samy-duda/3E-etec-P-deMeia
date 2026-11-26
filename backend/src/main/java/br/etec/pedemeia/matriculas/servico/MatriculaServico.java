package br.etec.pedemeia.matriculas.servico;

import br.etec.pedemeia.alunos.servico.AlunoServico;
import br.etec.pedemeia.comum.erro.CodigoErro;
import br.etec.pedemeia.comum.erro.NegocioException;
import br.etec.pedemeia.comum.paginacao.Pagina;
import br.etec.pedemeia.comum.paginacao.PaginaRequisicao;
import br.etec.pedemeia.matriculas.Mapeador;
import br.etec.pedemeia.matriculas.dto.MatriculaRequisicao;
import br.etec.pedemeia.matriculas.dto.MatriculaResposta;
import br.etec.pedemeia.matriculas.entidade.Matricula;
import br.etec.pedemeia.matriculas.repositorio.MatriculaRepositorio;
import br.etec.pedemeia.periodos.servico.PeriodoLetivoServico;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MatriculaServico {

    private final MatriculaRepositorio repositorio;
    private final AlunoServico alunos;
    private final PeriodoLetivoServico periodos;

    public MatriculaServico(MatriculaRepositorio repositorio, AlunoServico alunos, PeriodoLetivoServico periodos) {
        this.repositorio = repositorio;
        this.alunos = alunos;
        this.periodos = periodos;
    }

    /** Uma matrícula por aluno e período (MVP-CA07, D04). Qualquer ano escolar é recusado se já existir matrícula. */
    @Transactional
    public MatriculaResposta matricular(long periodoId, MatriculaRequisicao requisicao) {
        var periodo = periodos.obterEntidade(periodoId);
        var aluno = alunos.obterEntidade(requisicao.alunoId());
        if (repositorio.existeParaAlunoNoPeriodo(aluno.getId(), periodo.getId())) {
            throw new NegocioException(CodigoErro.MATRICULA_DUPLICADA, "Aluno já matriculado neste período letivo.");
        }
        Matricula matricula = new Matricula(aluno, periodo, requisicao.anoEscolar(), requisicao.turma());
        repositorio.persist(matricula);
        return Mapeador.paraResposta(matricula);
    }

    @Transactional
    public Pagina<MatriculaResposta> listarPorPeriodo(long periodoId, PaginaRequisicao paginacao) {
        periodos.obterEntidade(periodoId);
        var consulta = repositorio.listarPorPeriodo(periodoId);
        var conteudo = consulta.page(Page.of(paginacao.pagina(), paginacao.tamanho())).list()
                .stream().map(Mapeador::paraResposta).toList();
        return Pagina.de(conteudo, paginacao, consulta.count());
    }

    /** Usado por outros módulos (notas, bolsas). */
    public Matricula obterEntidade(long id) {
        return repositorio.findByIdOptional(id)
                .orElseThrow(() -> new NegocioException(CodigoErro.RECURSO_NAO_ENCONTRADO, "Matrícula não encontrada."));
    }
}
