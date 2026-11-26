package br.etec.pedemeia.bolsas.servico;

import br.etec.pedemeia.bolsas.Mapeador;
import br.etec.pedemeia.bolsas.dto.BolsaResposta;
import br.etec.pedemeia.bolsas.dto.ConcessaoBolsaRequisicao;
import br.etec.pedemeia.bolsas.entidade.Bolsa;
import br.etec.pedemeia.bolsas.repositorio.BolsaRepositorio;
import br.etec.pedemeia.classificacao.entidade.Classificacao;
import br.etec.pedemeia.classificacao.servico.ClassificacaoServico;
import br.etec.pedemeia.comum.configuracao.ConfiguracaoNegocio;
import br.etec.pedemeia.comum.erro.CodigoErro;
import br.etec.pedemeia.comum.erro.NegocioException;
import br.etec.pedemeia.matriculas.entidade.Matricula;
import br.etec.pedemeia.matriculas.servico.MatriculaServico;
import br.etec.pedemeia.processos.entidade.ProcessoSeletivo;
import br.etec.pedemeia.processos.servico.ProcessoSeletivoServico;
import br.etec.pedemeia.usuarios.entidade.Usuario;
import br.etec.pedemeia.usuarios.servico.UsuarioServico;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Concessão e consulta de bolsas (MVP-RF09, MVP-RF10). Sem cancelamento (MVP-CA30).
 * Trava o processo antes de ler (ADR-004). Lote é atômico: qualquer falha reverte tudo.
 */
@ApplicationScoped
public class BolsaServico {

    /** Fuso da data de concessão (Q17). Data de início, não instante. */
    static final ZoneId FUSO_NEGOCIO = ZoneId.of("America/Sao_Paulo");

    private final BolsaRepositorio repositorio;
    private final ProcessoSeletivoServico processos;
    private final ClassificacaoServico classificacoes;
    private final MatriculaServico matriculas;
    private final UsuarioServico usuarios;
    private final ConfiguracaoNegocio configuracao;

    public BolsaServico(BolsaRepositorio repositorio, ProcessoSeletivoServico processos,
                        ClassificacaoServico classificacoes, MatriculaServico matriculas,
                        UsuarioServico usuarios, ConfiguracaoNegocio configuracao) {
        this.repositorio = repositorio;
        this.processos = processos;
        this.classificacoes = classificacoes;
        this.matriculas = matriculas;
        this.usuarios = usuarios;
        this.configuracao = configuracao;
    }

    /**
     * Ordem de verificação (contrato, concederBolsas): valor (422), classificação gerada (422),
     * e no modo individual: aluno classificado (422), selecionado (422), bolsa existente (409).
     * No modo lote, sem pendentes: BOLSAS_JA_CONCEDIDAS (409).
     */
    @Transactional
    public List<BolsaResposta> conceder(long processoId, ConcessaoBolsaRequisicao requisicao, long usuarioId,
                                        boolean verDadosPessoais) {
        BigDecimal valorConfigurado = configuracao.bolsa().valorMensal();
        if (requisicao != null && requisicao.valorMensal() != null
                && requisicao.valorMensal().compareTo(valorConfigurado) != 0) {
            throw new NegocioException(CodigoErro.VALOR_BOLSA_INVALIDO,
                    "O valor da bolsa deve ser R$ " + valorConfigurado.toPlainString().replace('.', ',') + ".");
        }
        ProcessoSeletivo processo = processos.bloquearPorId(processoId);
        if (!classificacoes.geradaParaProcesso(processoId)) {
            throw new NegocioException(CodigoErro.CLASSIFICACAO_NAO_GERADA,
                    "A classificação deste processo ainda não foi gerada.");
        }
        Usuario concedente = usuarios.obterEntidade(usuarioId);
        LocalDate hoje = LocalDate.now(FUSO_NEGOCIO);
        Instant agora = Instant.now();

        if (requisicao != null && requisicao.matriculaId() != null) {
            Matricula matricula = matriculas.obterEntidade(requisicao.matriculaId());
            Classificacao linha = classificacoes.buscarPorProcessoEMatricula(processoId, matricula.getId())
                    .orElseThrow(() -> new NegocioException(CodigoErro.ALUNO_NAO_CLASSIFICADO,
                            "O aluno não consta na classificação deste processo."));
            if (!linha.isSelecionado()) {
                throw new NegocioException(CodigoErro.ALUNO_NAO_SELECIONADO,
                        "O aluno não está entre os selecionados do seu ano escolar.");
            }
            if (repositorio.existeParaMatriculaNoProcesso(matricula.getId(), processoId)) {
                throw new NegocioException(CodigoErro.BOLSA_JA_EXISTE, "Aluno já tem bolsa neste processo.");
            }
            Bolsa bolsa = new Bolsa(matricula, processo, valorConfigurado, hoje, concedente, agora);
            repositorio.persist(bolsa);
            return montar(processoId, List.of(bolsa), verDadosPessoais);
        }

        Set<Long> comBolsa = repositorio.matriculasComBolsaNoProcesso(processoId);
        List<Bolsa> criadas = classificacoes.listarEntidades(processoId).stream()
                .filter(Classificacao::isSelecionado)
                .filter(c -> !comBolsa.contains(c.getMatricula().getId()))
                .map(c -> new Bolsa(c.getMatricula(), processo, valorConfigurado, hoje, concedente, agora))
                .toList();
        if (criadas.isEmpty()) {
            throw new NegocioException(CodigoErro.BOLSAS_JA_CONCEDIDAS,
                    "Todos os selecionados já têm bolsa neste processo.");
        }
        criadas.forEach(repositorio::persist);
        return montar(processoId, criadas, verDadosPessoais);
    }

    @Transactional
    public List<BolsaResposta> listar(long processoId, boolean verDadosPessoais) {
        processos.obterEntidade(processoId);
        return montar(processoId, repositorio.listarPorProcesso(processoId), verDadosPessoais);
    }

    /** Ordena por ano escolar e posição (contrato). A posição vem de CLASSIFICACOES. */
    private List<BolsaResposta> montar(long processoId, List<Bolsa> bolsas, boolean verDadosPessoais) {
        Map<Long, Integer> posicoes = new HashMap<>();
        for (Classificacao c : classificacoes.listarEntidades(processoId)) {
            posicoes.put(c.getMatricula().getId(), c.getPosicao());
        }
        return bolsas.stream()
                .sorted(Comparator.comparingInt((Bolsa b) -> b.getMatricula().getAnoEscolar())
                        .thenComparingInt(b -> posicoes.getOrDefault(b.getMatricula().getId(), 0)))
                .map(b -> Mapeador.paraResposta(b, posicoes.getOrDefault(b.getMatricula().getId(), 0),
                        verDadosPessoais))
                .toList();
    }
}
