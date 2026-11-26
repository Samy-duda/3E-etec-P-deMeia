package br.etec.pedemeia.provas.entidade;

import br.etec.pedemeia.periodos.entidade.PeriodoLetivo;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "PROVAS")
public class Prova {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_letivo_id", nullable = false)
    private PeriodoLetivo periodo;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "descricao")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String descricao;

    @Column(name = "data_aplicacao", nullable = false)
    private LocalDate dataAplicacao;

    @Column(name = "nota_maxima", nullable = false, precision = 5, scale = 2)
    private BigDecimal notaMaxima;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criada_por")
    private Usuario criadaPor;

    @Column(name = "criada_em", nullable = false)
    private Instant criadaEm;

    protected Prova() {
    }

    public Prova(PeriodoLetivo periodo, String titulo, String descricao, LocalDate dataAplicacao,
                 BigDecimal notaMaxima, Usuario criadaPor, Instant criadaEm) {
        this.periodo = periodo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataAplicacao = dataAplicacao;
        this.notaMaxima = notaMaxima;
        this.criadaPor = criadaPor;
        this.criadaEm = criadaEm;
    }

    public Long getId() {
        return id;
    }

    public PeriodoLetivo getPeriodo() {
        return periodo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getDataAplicacao() {
        return dataAplicacao;
    }

    public BigDecimal getNotaMaxima() {
        return notaMaxima;
    }

    public Usuario getCriadaPor() {
        return criadaPor;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }
}
