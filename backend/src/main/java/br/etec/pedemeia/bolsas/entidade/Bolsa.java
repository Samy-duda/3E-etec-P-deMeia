package br.etec.pedemeia.bolsas.entidade;

import br.etec.pedemeia.matriculas.entidade.Matricula;
import br.etec.pedemeia.processos.entidade.ProcessoSeletivo;
import br.etec.pedemeia.usuarios.entidade.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Bolsa do MVP. Só a situação ATIVA é gravada (MVP-CA30; CANCELADA é reservado para P1, D06).
 * Sem campos de cancelamento: fora do escopo do MVP.
 */
@Entity
@Table(name = "BOLSAS")
public class Bolsa {

    public static final String SITUACAO_ATIVA = "ATIVA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "processo_seletivo_id", nullable = false)
    private ProcessoSeletivo processo;

    /** Valor vigente na concessão (D07, Q13). */
    @Column(name = "valor_mensal", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMensal;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "situacao", nullable = false, length = 20)
    private String situacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "criada_por", nullable = false)
    private Usuario criadaPor;

    @Column(name = "criada_em", nullable = false)
    private Instant criadaEm;

    protected Bolsa() {
    }

    public Bolsa(Matricula matricula, ProcessoSeletivo processo, BigDecimal valorMensal, LocalDate dataInicio,
                 Usuario criadaPor, Instant criadaEm) {
        this.matricula = matricula;
        this.processo = processo;
        this.valorMensal = valorMensal;
        this.dataInicio = dataInicio;
        this.situacao = SITUACAO_ATIVA;
        this.criadaPor = criadaPor;
        this.criadaEm = criadaEm;
    }

    public Long getId() {
        return id;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public ProcessoSeletivo getProcesso() {
        return processo;
    }

    public BigDecimal getValorMensal() {
        return valorMensal;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public String getSituacao() {
        return situacao;
    }

    public Usuario getCriadaPor() {
        return criadaPor;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }
}
