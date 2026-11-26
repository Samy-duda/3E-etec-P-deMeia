package br.etec.pedemeia.provas.servico;

import br.etec.pedemeia.comum.erro.CodigoErro;
import br.etec.pedemeia.comum.erro.NegocioException;
import br.etec.pedemeia.comum.paginacao.Pagina;
import br.etec.pedemeia.comum.paginacao.PaginaRequisicao;
import br.etec.pedemeia.periodos.servico.PeriodoLetivoServico;
import br.etec.pedemeia.provas.Mapeador;
import br.etec.pedemeia.provas.dto.ProvaRequisicao;
import br.etec.pedemeia.provas.dto.ProvaResposta;
import br.etec.pedemeia.provas.entidade.Prova;
import br.etec.pedemeia.provas.repositorio.ProvaRepositorio;
import br.etec.pedemeia.usuarios.servico.UsuarioServico;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;

@ApplicationScoped
public class ProvaServico {

    private final ProvaRepositorio repositorio;
    private final PeriodoLetivoServico periodos;
    private final UsuarioServico usuarios;

    public ProvaServico(ProvaRepositorio repositorio, PeriodoLetivoServico periodos, UsuarioServico usuarios) {
        this.repositorio = repositorio;
        this.periodos = periodos;
        this.usuarios = usuarios;
    }

    @Transactional
    public ProvaResposta cadastrar(ProvaRequisicao requisicao, long usuarioId) {
        var periodo = periodos.obterEntidade(requisicao.periodoLetivoId());
        var usuario = usuarios.obterEntidade(usuarioId);
        Prova prova = new Prova(periodo, requisicao.titulo(), requisicao.descricao(), requisicao.dataAplicacao(),
                requisicao.notaMaxima(), usuario, Instant.now());
        repositorio.persist(prova);
        return Mapeador.paraResposta(prova);
    }

    @Transactional
    public ProvaResposta consultar(long id) {
        return Mapeador.paraResposta(obterEntidade(id));
    }

    @Transactional
    public Pagina<ProvaResposta> listar(Long periodoLetivoId, PaginaRequisicao paginacao) {
        if (periodoLetivoId != null) {
            periodos.obterEntidade(periodoLetivoId);
        }
        var consulta = repositorio.listar(periodoLetivoId);
        var conteudo = consulta.page(Page.of(paginacao.pagina(), paginacao.tamanho())).list()
                .stream().map(Mapeador::paraResposta).toList();
        return Pagina.de(conteudo, paginacao, consulta.count());
    }

    /** Usado por outros módulos (notas, processos). Falha se a prova não existir. */
    public Prova obterEntidade(long id) {
        return repositorio.findByIdOptional(id)
                .orElseThrow(() -> new NegocioException(CodigoErro.RECURSO_NAO_ENCONTRADO, "Prova não encontrada."));
    }

    /** Trava a linha da prova na transação atual (ADR-004). Deve ser chamado antes de qualquer leitura da transação. */
    public Prova bloquearPorId(long id) {
        return repositorio.bloquear(id)
                .orElseThrow(() -> new NegocioException(CodigoErro.RECURSO_NAO_ENCONTRADO, "Prova não encontrada."));
    }
}
