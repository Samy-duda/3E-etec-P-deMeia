package br.etec.pedemeia.matriculas.entidade;

import br.etec.pedemeia.alunos.entidade.Aluno;
import br.etec.pedemeia.periodos.entidade.PeriodoLetivo;
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

@Entity
@Table(name = "MATRICULAS")
public class Matricula {

    public static final String STATUS_ATIVA = "ATIVA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_letivo_id", nullable = false)
    private PeriodoLetivo periodo;

    /** Ano escolar 1, 2 ou 3 (P05). TINYINT no MySQL. */
    @Column(name = "ano_escolar", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Integer anoEscolar;

    @Column(name = "turma", length = 20)
    private String turma;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    protected Matricula() {
    }

    public Matricula(Aluno aluno, PeriodoLetivo periodo, int anoEscolar, String turma) {
        this.aluno = aluno;
        this.periodo = periodo;
        this.anoEscolar = anoEscolar;
        this.turma = turma;
        this.status = STATUS_ATIVA;
    }

    public Long getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public PeriodoLetivo getPeriodo() {
        return periodo;
    }

    public int getAnoEscolar() {
        return anoEscolar;
    }

    public String getTurma() {
        return turma;
    }

    public String getStatus() {
        return status;
    }
}
