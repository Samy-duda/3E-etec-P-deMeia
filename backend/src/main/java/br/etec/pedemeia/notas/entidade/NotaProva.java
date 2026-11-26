package br.etec.pedemeia.notas.entidade;

import br.etec.pedemeia.matriculas.entidade.Matricula;
import br.etec.pedemeia.provas.entidade.Prova;
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

@Entity
@Table(name = "NOTAS_PROVAS")
public class NotaProva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prova_id", nullable = false)
    private Prova prova;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(name = "nota", nullable = false, precision = 5, scale = 2)
    private BigDecimal nota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lancada_por", nullable = false)
    private Usuario lancadaPor;

    @Column(name = "lancada_em", nullable = false)
    private Instant lancadaEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alterada_por")
    private Usuario alteradaPor;

    @Column(name = "alterada_em")
    private Instant alteradaEm;

    protected NotaProva() {
    }

    public NotaProva(Prova prova, Matricula matricula, BigDecimal nota, Usuario lancadaPor, Instant lancadaEm) {
        this.prova = prova;
        this.matricula = matricula;
        this.nota = nota;
        this.lancadaPor = lancadaPor;
        this.lancadaEm = lancadaEm;
    }

    /** Correção auditada (P-DER-2, Q07, MVP-RNF06). */
    public void corrigir(BigDecimal novaNota, Usuario corrigidaPor, Instant quando) {
        this.nota = novaNota;
        this.alteradaPor = corrigidaPor;
        this.alteradaEm = quando;
    }

    public Long getId() {
        return id;
    }

    public Prova getProva() {
        return prova;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public Usuario getLancadaPor() {
        return lancadaPor;
    }

    public Instant getLancadaEm() {
        return lancadaEm;
    }

    public Usuario getAlteradaPor() {
        return alteradaPor;
    }

    public Instant getAlteradaEm() {
        return alteradaEm;
    }
}
