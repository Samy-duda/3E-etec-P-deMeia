package br.etec.pedemeia.processos.servico;

import br.etec.pedemeia.comum.erro.CodigoErro;
import br.etec.pedemeia.comum.erro.NegocioException;
import br.etec.pedemeia.comum.paginacao.Pagina;
import br.etec.pedemeia.comum.paginacao.PaginaRequisicao;
import br.etec.pedemeia.periodos.servico.PeriodoLetivoServico;
import br.etec.pedemeia.processos.Mapeador;
import br.etec.pedemeia.processos.dto.ProcessoSeletivoRequisicao;
import br.etec.pedemeia.processos.dto.ProcessoSeletivoResposta;
import br.etec.pedemeia.processos.entidade.ProcessoSeletivo;
import br.etec.pedemeia.processos.repositorio.ProcessoSeletivoRepositorio;
import br.etec.pedemeia.provas.servico.ProvaServico;
import br.etec.pedemeia.usuarios.servico.UsuarioServico;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProcessoSeletivoServico {

    private final ProcessoSeletivoRepositorio repositorio;
    private final PeriodoLetivoServico periodos;
    private final ProvaServico provas;
    private final UsuarioServico usuarios;

    public ProcessoSeletivoServico(ProcessoSeletivoRepositorio repositorio, PeriodoLetivoServico periodos,
                                   ProvaServico provas, UsuarioServico usuarios) {
        this.repositorio = repositorio;
        this.periodos = periodos;
        this.provas = provas;
        this.usuarios = usuarios;
    }

    /** Uma prova tem no máximo um processo (P-DER-4, Q16). A prova precisa pertencer ao período (MVP-CA15). */
    @Transactional
    public ProcessoSeletivoResposta criar(ProcessoSeletivoRequisicao requisicao, long usuarioId) {
        var periodo = periodos.obterEntidade(requisicao.periodoLetivoId());
        var prova = provas.obterEntidade(requisicao.provaId());
        if (!prova.getPeriodo().getId().equals(periodo.getId())) {
            throw new NegocioException(CodigoErro.PROVA_NAO_PERTENCE_AO_PERIODO,
                    "A prova não pertence ao período letivo informado.");
        }
        if (repositorio.buscarPorProva(prova.getId()).isPresent()) {
            throw new NegocioException(CodigoErro.PROCESSO_JA_EXISTE_PARA_PROVA,
                    "Já existe processo seletivo para esta prova.");
        }
        var processo = new ProcessoSeletivo(periodo, prova, usuarios.obterEntidade(usuarioId));
        repositorio.persist(processo);
        return Mapeador.paraResposta(processo);
    }

    @Transactional
    public ProcessoSeletivoResposta consultar(long id) {
        return Mapeador.paraResposta(obterEntidade(id));
    }

    @Transactional
    public Pagina<ProcessoSeletivoResposta> listar(Long periodoLetivoId, PaginaRequisicao paginacao) {
        if (periodoLetivoId != null) {
            periodos.obterEntidade(periodoLetivoId);
        }
        var consulta = repositorio.listar(periodoLetivoId);
        var conteudo = consulta.page(Page.of(paginacao.pagina(), paginacao.tamanho())).list()
                .stream().map(Mapeador::paraResposta).toList();
        return Pagina.de(conteudo, paginacao, consulta.count());
    }

    public ProcessoSeletivo obterEntidade(long id) {
        return repositorio.findByIdOptional(id)
                .orElseThrow(() -> new NegocioException(CodigoErro.RECURSO_NAO_ENCONTRADO, "Processo seletivo não encontrado."));
    }

    /** Trava o processo na transação atual (ADR-004). */
    public ProcessoSeletivo bloquearPorId(long id) {
        return repositorio.bloquear(id)
                .orElseThrow(() -> new NegocioException(CodigoErro.RECURSO_NAO_ENCONTRADO, "Processo seletivo não encontrado."));
    }

    /** Usado por notas: a classificação do processo desta prova já foi gerada? (Q07). */
    public boolean classificacaoGeradaParaProva(long provaId) {
        return repositorio.existeClassificacaoParaProva(provaId);
    }
}
