package br.etec.pedemeia.periodos.servico;

import br.etec.pedemeia.comum.erro.CodigoErro;
import br.etec.pedemeia.comum.erro.NegocioException;
import br.etec.pedemeia.periodos.Mapeador;
import br.etec.pedemeia.periodos.dto.PeriodoLetivoRequisicao;
import br.etec.pedemeia.periodos.dto.PeriodoLetivoResposta;
import br.etec.pedemeia.periodos.entidade.PeriodoLetivo;
import br.etec.pedemeia.periodos.repositorio.PeriodoLetivoRepositorio;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PeriodoLetivoServico {

    private final PeriodoLetivoRepositorio repositorio;

    public PeriodoLetivoServico(PeriodoLetivoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public PeriodoLetivoResposta criar(PeriodoLetivoRequisicao requisicao) {
        if (requisicao.dataFim().isBefore(requisicao.dataInicio())) {
            throw NegocioException.campo("dataFim", "deve ser igual ou posterior a dataInicio");
        }
        PeriodoLetivo periodo = new PeriodoLetivo(requisicao.nome(), requisicao.dataInicio(), requisicao.dataFim());
        repositorio.persist(periodo);
        return Mapeador.paraResposta(periodo);
    }

    @Transactional
    public List<PeriodoLetivoResposta> listar() {
        return repositorio.listAll().stream().map(Mapeador::paraResposta).toList();
    }

    /** Usado por outros módulos. Falha se o período não existir. */
    public PeriodoLetivo obterEntidade(long id) {
        return repositorio.findByIdOptional(id)
                .orElseThrow(() -> new NegocioException(CodigoErro.RECURSO_NAO_ENCONTRADO, "Período letivo não encontrado."));
    }
}
