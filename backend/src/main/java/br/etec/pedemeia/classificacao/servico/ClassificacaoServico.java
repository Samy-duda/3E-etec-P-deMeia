package br.etec.pedemeia.classificacao.servico;

import br.etec.pedemeia.classificacao.CriterioDesempate;
import br.etec.pedemeia.classificacao.CriterioDesempate.Candidato;
import br.etec.pedemeia.classificacao.CriterioDesempate.Posicionado;
import br.etec.pedemeia.classificacao.Mapeador;
import br.etec.pedemeia.classificacao.dto.ClassificacaoResposta;
import br.etec.pedemeia.classificacao.entidade.Classificacao;
import br.etec.pedemeia.classificacao.repositorio.ClassificacaoRepositorio;
import br.etec.pedemeia.comum.configuracao.ConfiguracaoNegocio;
import br.etec.pedemeia.comum.erro.CodigoErro;
import br.etec.pedemeia.comum.erro.NegocioException;
import br.etec.pedemeia.matriculas.entidade.Matricula;
import br.etec.pedemeia.notas.entidade.NotaProva;
import br.etec.pedemeia.notas.servico.NotaServico;
import br.etec.pedemeia.processos.entidade.ProcessoSeletivo;
import br.etec.pedemeia.processos.servico.ProcessoSeletivoServico;
import br.etec.pedemeia.provas.servico.ProvaServico;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class ClassificacaoServico {

    private final ClassificacaoRepositorio repositorio;
    private final ProcessoSeletivoServico processos;
    private final ProvaServico provas;
    private final NotaServico notas;
    private final ConfiguracaoNegocio configuracao;

    public ClassificacaoServico(ClassificacaoRepositorio repositorio, ProcessoSeletivoServico processos,
                                ProvaServico provas, NotaServico notas, ConfiguracaoNegocio configuracao) {
        this.repositorio = repositorio;
        this.processos = processos;
        this.provas = provas;
        this.notas = notas;
        this.configuracao = configuracao;
    }

    /**
     * Geração atômica (ADR-004, MVP-RNF03). Ordem do contrato (gerarClassificacao):
     * sem notas (422), já gerada (409). Depois: ordena com o desempate P01 e grava todas as posições
     * ou nenhuma. Trava o processo e a prova antes de ler (ADR-004).
     */
    @Transactional
    public ClassificacaoResposta gerar(long processoId, boolean verDadosPessoais) {
        ProcessoSeletivo processo = processos.bloquearPorId(processoId);
        long provaId = processo.getProva().getId();
        provas.bloquearPorId(provaId);

        List<NotaProva> notasDaProva = notas.listarEntidadesDaProva(provaId);
        if (notasDaProva.isEmpty()) {
            throw new NegocioException(CodigoErro.SEM_NOTAS_PARA_CLASSIFICACAO,
                    "Não há notas registradas nesta prova. A classificação não foi gerada.");
        }
        if (repositorio.existeParaProcesso(processoId)) {
            throw new NegocioException(CodigoErro.CLASSIFICACAO_JA_GERADA,
                    "A classificação deste processo já foi gerada.");
        }

        Map<Long, Matricula> matriculasPorId = new HashMap<>();
        List<Candidato> candidatos = notasDaProva.stream().map(nota -> {
            Matricula m = nota.getMatricula();
            matriculasPorId.put(m.getId(), m);
            return new Candidato(m.getId(), m.getAnoEscolar(), nota.getNota(),
                    m.getAluno().getDataNascimento(), m.getAluno().getNome());
        }).toList();

        Instant agora = Instant.now();
        int vagas = configuracao.processo().vagasPorAno();
        for (Posicionado p : CriterioDesempate.classificar(candidatos, vagas)) {
            repositorio.persist(new Classificacao(processo, matriculasPorId.get(p.matriculaId()),
                    p.notaFinal(), p.posicao(), p.selecionado(), agora));
        }
        return consultar(processoId, null, verDadosPessoais);
    }

    /** Sem geração, responde calculadaEm nulo e itens vazio (contrato, consultarClassificacao). */
    @Transactional
    public ClassificacaoResposta consultar(long processoId, Integer anoEscolar, boolean verDadosPessoais) {
        if (anoEscolar != null && (anoEscolar < 1 || anoEscolar > 3)) {
            throw NegocioException.campo("anoEscolar", "deve estar entre 1 e 3");
        }
        processos.obterEntidade(processoId);
        int vagas = configuracao.processo().vagasPorAno();
        var primeira = repositorio.find("processo.id = ?1 order by id", processoId).firstResultOptional();
        if (primeira.isEmpty()) {
            return new ClassificacaoResposta(processoId, null, vagas, List.of());
        }
        var itens = repositorio.listarPorProcesso(processoId, anoEscolar).stream()
                .map(c -> Mapeador.paraItem(c, verDadosPessoais)).toList();
        return new ClassificacaoResposta(processoId, primeira.get().getCalculadaEm(), vagas, itens);
    }

    /** Usado por bolsas: a classificação do processo já foi gerada? */
    public boolean geradaParaProcesso(long processoId) {
        return repositorio.existeParaProcesso(processoId);
    }

    /** Usado por bolsas: linha da classificação do aluno no processo, se existir. */
    public Optional<Classificacao> buscarPorProcessoEMatricula(long processoId, long matriculaId) {
        return repositorio.buscarPorProcessoEMatricula(processoId, matriculaId);
    }

    /** Usado por bolsas, na mesma transação. Ordem: ano escolar e posição. */
    public List<Classificacao> listarEntidades(long processoId) {
        return repositorio.listarPorProcesso(processoId, null);
    }
}
