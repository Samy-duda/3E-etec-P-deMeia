package br.etec.pedemeia.banco;

import br.etec.pedemeia.teste.Api;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Restrições do V1 (CHECK e UNIQUE) são testadas com inserções diretas que devem falhar
 * (03-modelo-fisico.md, seção 8, MVP-RNF03). Não passam pela aplicação.
 */
@QuarkusTest
class RestricoesBancoTest {

    @Inject
    DataSource dataSource;

    private long usuarioAdmin(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("select id from USUARIOS where email = ?")) {
            ps.setString(1, Api.ADMIN);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    private long insert(Connection c, String sql, Object... valores) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < valores.length; i++) {
                ps.setObject(i + 1, valores[i]);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    private long aluno(Connection c, String matricula) throws SQLException {
        return insert(c, "insert into ALUNOS (nome, matricula_escolar, data_nascimento) values (?, ?, ?)",
                "Aluno Banco", matricula, java.sql.Date.valueOf("2008-01-01"));
    }

    private long periodo(Connection c) throws SQLException {
        return insert(c, "insert into PERIODOS_LETIVOS (nome, data_inicio, data_fim) values (?, ?, ?)",
                "Banco " + Api.sufixo(), java.sql.Date.valueOf("2026-02-01"), java.sql.Date.valueOf("2026-12-01"));
    }

    private long prova(Connection c, long periodoId, String notaMaxima) throws SQLException {
        return insert(c, "insert into PROVAS (periodo_letivo_id, titulo, data_aplicacao, nota_maxima) values (?, ?, ?, ?)",
                periodoId, "Prova banco", java.sql.Date.valueOf("2026-11-20"), new java.math.BigDecimal(notaMaxima));
    }

    private long matricula(Connection c, long alunoId, long periodoId, int ano) throws SQLException {
        return insert(c, "insert into MATRICULAS (aluno_id, periodo_letivo_id, ano_escolar) values (?, ?, ?)",
                alunoId, periodoId, ano);
    }

    @Test
    void uniqueMatriculaEscolarDoAluno() throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            String matricula = "U-" + Api.sufixo();
            aluno(c, matricula);
            assertThrows(SQLException.class, () -> aluno(c, matricula));
        }
    }

    @Test
    void checkAnoEscolarEntreUmETres() throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            long a = aluno(c, "A-" + Api.sufixo());
            long p = periodo(c);
            assertThrows(SQLException.class, () -> matricula(c, a, p, 4));
            assertThrows(SQLException.class, () -> matricula(c, a, p, 0));
        }
    }

    @Test
    void uniqueMatriculaPorAlunoEPeriodo() throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            long a = aluno(c, "B-" + Api.sufixo());
            long p = periodo(c);
            matricula(c, a, p, 1);
            assertThrows(SQLException.class, () -> matricula(c, a, p, 2));
        }
    }

    @Test
    void checkNotaMaximaDaProvaMaiorQueZero() throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            long p = periodo(c);
            assertThrows(SQLException.class, () -> prova(c, p, "0"));
            assertThrows(SQLException.class, () -> prova(c, p, "-5"));
        }
    }

    @Test
    void checkNotaNaoNegativaEUniqueNotaPorProvaEMatricula() throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            long a = aluno(c, "N-" + Api.sufixo());
            long p = periodo(c);
            long pr = prova(c, p, "10.00");
            long m = matricula(c, a, p, 1);
            long admin = usuarioAdmin(c);
            assertThrows(SQLException.class, () ->
                    insert(c, "insert into NOTAS_PROVAS (prova_id, matricula_id, nota, lancada_por) values (?, ?, ?, ?)",
                            pr, m, new java.math.BigDecimal("-1.00"), admin));
            insert(c, "insert into NOTAS_PROVAS (prova_id, matricula_id, nota, lancada_por) values (?, ?, ?, ?)",
                    pr, m, new java.math.BigDecimal("7.50"), admin);
            assertThrows(SQLException.class, () ->
                    insert(c, "insert into NOTAS_PROVAS (prova_id, matricula_id, nota, lancada_por) values (?, ?, ?, ?)",
                            pr, m, new java.math.BigDecimal("8.00"), admin));
        }
    }

    @Test
    void uniqueUmProcessoPorProvaECheckDePosicao() throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            long p = periodo(c);
            long pr = prova(c, p, "10.00");
            insert(c, "insert into PROCESSOS_SELETIVOS (periodo_letivo_id, prova_id) values (?, ?)", p, pr);
            assertThrows(SQLException.class, () ->
                    insert(c, "insert into PROCESSOS_SELETIVOS (periodo_letivo_id, prova_id) values (?, ?)", p, pr));

            long a = aluno(c, "P-" + Api.sufixo());
            long m = matricula(c, a, p, 1);
            long processo = insert(c, "insert into PROCESSOS_SELETIVOS (periodo_letivo_id, prova_id) values (?, ?)",
                    p, prova(c, p, "10.00"));
            assertThrows(SQLException.class, () ->
                    insert(c, "insert into CLASSIFICACOES (processo_seletivo_id, matricula_id, nota_final, posicao) values (?, ?, ?, ?)",
                            processo, m, new java.math.BigDecimal("5.00"), 0));
        }
    }

    @Test
    void uniqueUmaBolsaPorAlunoEProcessoEValorPositivo() throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            long a = aluno(c, "S-" + Api.sufixo());
            long p = periodo(c);
            long pr = prova(c, p, "10.00");
            long processo = insert(c, "insert into PROCESSOS_SELETIVOS (periodo_letivo_id, prova_id) values (?, ?)", p, pr);
            long m = matricula(c, a, p, 1);
            long admin = usuarioAdmin(c);
            insert(c, "insert into BOLSAS (matricula_id, processo_seletivo_id, valor_mensal, data_inicio, criada_por) "
                    + "values (?, ?, ?, ?, ?)", m, processo, new java.math.BigDecimal("300.00"),
                    java.sql.Date.valueOf("2026-11-01"), admin);
            assertThrows(SQLException.class, () ->
                    insert(c, "insert into BOLSAS (matricula_id, processo_seletivo_id, valor_mensal, data_inicio, criada_por) "
                            + "values (?, ?, ?, ?, ?)", m, processo, new java.math.BigDecimal("300.00"),
                            java.sql.Date.valueOf("2026-11-01"), admin));
        }
    }

    @Test
    void checkPerfilDeUsuario() throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            assertThrows(SQLException.class, () ->
                    insert(c, "insert into USUARIOS (nome, email, senha_hash, perfil) values (?, ?, ?, ?)",
                            "X", "perfil-" + Api.sufixo() + "@IA_teste", "hash", "ROOT"));
        }
    }
}
