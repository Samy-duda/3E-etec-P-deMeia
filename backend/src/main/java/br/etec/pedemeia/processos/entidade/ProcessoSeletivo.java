package br.etec.pedemeia.processos.entidade;

import br.etec.pedemeia.periodos.entidade.PeriodoLetivo;
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

@Entity
@Table(name = "PROCESSOS_SELETIVOS")
public class ProcessoSeletivo {

    /** Único estado formal (Q12, P-DER-3). */
    public static final String STATUS_CRIADO = "CRIADO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_letivo_id", nullable = false)
    private PeriodoLetivo periodo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prova_id", nullable = false, unique = true)
    private Prova prova;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por")
    private Usuario criadoPor;

    protected ProcessoSeletivo() {
    }

    public ProcessoSeletivo(PeriodoLetivo periodo, Prova prova, Usuario criadoPor) {
        this.periodo = periodo;
        this.prova = prova;
        this.status = STATUS_CRIADO;
        this.criadoPor = criadoPor;
    }

    public Long getId() {
        return id;
    }

    public PeriodoLetivo getPeriodo() {
        return periodo;
    }

    public Prova getProva() {
        return prova;
    }

    public String getStatus() {
        return status;
    }

    public Usuario getCriadoPor() {
        return criadoPor;
    }
}
