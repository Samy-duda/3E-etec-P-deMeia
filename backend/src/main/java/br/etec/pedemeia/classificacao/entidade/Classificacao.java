package br.etec.pedemeia.classificacao.entidade;

import br.etec.pedemeia.matriculas.entidade.Matricula;
import br.etec.pedemeia.processos.entidade.ProcessoSeletivo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;

@Entity(name = "Classificacao")
@Table(name = "CLASSIFICACOES")
public class Classificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "processo_seletivo_id", nullable = false)
    private ProcessoSeletivo processo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(name = "nota_final", nullable = false, precision = 5, scale = 2)
    private BigDecimal notaFinal;

    /** Posição sequencial dentro do ano escolar (SMALLINT). */
    @Column(name = "posicao", nullable = false)
    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer posicao;

    @Column(name = "selecionado", nullable = false)
    private boolean selecionado;

    @Column(name = "calculada_em", nullable = false)
    private Instant calculadaEm;

    protected Classificacao() {
    }

    public Classificacao(ProcessoSeletivo processo, Matricula matricula, BigDecimal notaFinal, int posicao,
                         boolean selecionado, Instant calculadaEm) {
        this.processo = processo;
        this.matricula = matricula;
        this.notaFinal = notaFinal;
        this.posicao = posicao;
        this.selecionado = selecionado;
        this.calculadaEm = calculadaEm;
    }

    public Long getId() {
        return id;
    }

    public ProcessoSeletivo getProcesso() {
        return processo;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public BigDecimal getNotaFinal() {
        return notaFinal;
    }

    public int getPosicao() {
        return posicao;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public Instant getCalculadaEm() {
        return calculadaEm;
    }
}
