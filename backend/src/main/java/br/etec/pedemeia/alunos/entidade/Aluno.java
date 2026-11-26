package br.etec.pedemeia.alunos.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "ALUNOS")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "matricula_escolar", nullable = false, length = 30)
    private String matriculaEscolar;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected Aluno() {
    }

    public Aluno(String nome, String matriculaEscolar, LocalDate dataNascimento, String email, String telefone,
                 Instant criadoEm) {
        this.nome = nome;
        this.matriculaEscolar = matriculaEscolar;
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.telefone = telefone;
        this.ativo = true;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getMatriculaEscolar() {
        return matriculaEscolar;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
