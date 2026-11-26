package br.etec.pedemeia.notas;

import br.etec.pedemeia.teste.Api;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/** MVP-RF05, MVP-CA11 a MVP-CA14, MVP-RNF06 e correção de nota (Q07). */
@QuarkusTest
class NotaApiTest {

    private static long periodoComProva(String notaMaxima) {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        return Api.criarProva(periodo, "Simulado de Matemática", notaMaxima);
    }

    private static long periodoDaProva(long provaId) {
        return Api.como(Api.SECRETARIA).get("/provas/" + provaId).then().statusCode(200)
                .extract().jsonPath().getLong("periodoLetivoId");
    }

    @Test
    void mvpCa11_notaValidaEhRegistradaComQuemLancou() {
        long prova = periodoComProva("10.00");
        Api.Matriculado ana = Api.matriculado(periodoDaProva(prova), "Ana Souza", "2008-03-15", 2);
        Api.lancarNota(prova, ana.matriculaId(), "7.5")
                .then().statusCode(201)
                .body("nota", equalTo(7.5f))
                .body("nomeAluno", equalTo("Ana Souza"))
                .body("lancadaPorId", equalTo((int) Api.idDo(Api.SECRETARIA)))
                .body("lancadaEm", notNullValue())
                .body("alteradaPorId", nullValue());
        Api.como(Api.SECRETARIA).get("/provas/" + prova + "/notas")
                .then().statusCode(200)
                .body("conteudo[0].nota", equalTo(7.5f))
                .body("totalElementos", equalTo(1));
    }

    @Test
    void mvpCa12_notaNegativaEForaDoIntervaloSaoRecusadas() {
        long prova = periodoComProva("10.00");
        Api.Matriculado ana = Api.matriculado(periodoDaProva(prova), "Ana Souza", "2008-03-15", 2);

        Api.lancarNota(prova, ana.matriculaId(), "-0.5")
                .then().statusCode(400).body("codigo", equalTo("VALIDACAO_INVALIDA"));
        Api.lancarNota(prova, ana.matriculaId(), "10.5")
                .then().statusCode(422).body("codigo", equalTo("NOTA_FORA_DO_INTERVALO"));
        Api.como(Api.SECRETARIA).get("/provas/" + prova + "/notas")
                .then().statusCode(200).body("totalElementos", equalTo(0));
    }

    @Test
    void mvpCa12_complemento_zeroEDezEhAceito() {
        long prova = periodoComProva("10.00");
        long periodo = periodoDaProva(prova);
        Api.lancarNota(prova, Api.matriculado(periodo, "Zero", "2008-01-01", 1).matriculaId(), "0")
                .then().statusCode(201);
        Api.lancarNota(prova, Api.matriculado(periodo, "Dez", "2008-01-01", 1).matriculaId(), "10.00")
                .then().statusCode(201);
    }

    @Test
    void mvpCa13_segundaNotaDaMesmaProvaEhRecusadaEMantemAPrimeira() {
        long prova = periodoComProva("10.00");
        Api.Matriculado ana = Api.matriculado(periodoDaProva(prova), "Ana Souza", "2008-03-15", 2);
        Api.lancarNota(prova, ana.matriculaId(), "7.5").then().statusCode(201);
        Api.lancarNota(prova, ana.matriculaId(), "8.0")
                .then().statusCode(409).body("codigo", equalTo("NOTA_DUPLICADA"));
        Api.como(Api.SECRETARIA).get("/provas/" + prova + "/notas")
                .then().statusCode(200)
                .body("totalElementos", equalTo(1))
                .body("conteudo[0].nota", equalTo(7.5f));
    }

    @Test
    void mvpCa14_alunoNaoMatriculadoNoPeriodoDaProvaEhRecusado() {
        long periodoAntigo = Api.criarPeriodo("2025-2 " + Api.sufixo());
        long periodoNovo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long prova = Api.criarProva(periodoNovo, "Simulado", "10.00");
        Api.Matriculado pedro = Api.matriculado(periodoAntigo, "Pedro Alves", "2007-07-07", 3);
        Api.lancarNota(prova, pedro.matriculaId(), "6.0")
                .then().statusCode(422).body("codigo", equalTo("ALUNO_NAO_MATRICULADO_NO_PERIODO"));
        Api.como(Api.SECRETARIA).get("/provas/" + prova + "/notas")
                .then().statusCode(200).body("totalElementos", equalTo(0));
    }

    @Test
    void matriculaInexistenteDevolve404() {
        long prova = periodoComProva("10.00");
        Api.lancarNota(prova, 999999999L, "5.0")
                .then().statusCode(404).body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
    }

    @Test
    void q07_notaPodeSerCorrigidaAntesDaClassificacaoEFicaAuditada() {
        long prova = periodoComProva("10.00");
        Api.Matriculado ana = Api.matriculado(periodoDaProva(prova), "Ana Souza", "2008-03-15", 2);
        long notaId = Api.lancarNota(prova, ana.matriculaId(), "7.5").then().statusCode(201)
                .extract().jsonPath().getLong("id");
        Api.como(Api.SECRETARIA).body(Map.of("nota", 8.25))
                .put("/provas/" + prova + "/notas/" + notaId)
                .then().statusCode(200)
                .body("nota", equalTo(8.25f))
                .body("lancadaPorId", equalTo((int) Api.idDo(Api.SECRETARIA)))
                .body("alteradaPorId", equalTo((int) Api.idDo(Api.SECRETARIA)))
                .body("alteradaEm", notNullValue());
    }

    @Test
    void q07_correcaoAcimaDaNotaMaximaEhRecusada() {
        long prova = periodoComProva("10.00");
        Api.Matriculado ana = Api.matriculado(periodoDaProva(prova), "Ana Souza", "2008-03-15", 2);
        long notaId = Api.lancarNota(prova, ana.matriculaId(), "7.5").then().statusCode(201)
                .extract().jsonPath().getLong("id");
        Api.como(Api.SECRETARIA).body(Map.of("nota", 11))
                .put("/provas/" + prova + "/notas/" + notaId)
                .then().statusCode(422).body("codigo", equalTo("NOTA_FORA_DO_INTERVALO"));
    }

    @Test
    void notaDeOutraProvaNaoPodeSerCorrigidaPorEssaRota() {
        long prova = periodoComProva("10.00");
        long outra = periodoComProva("10.00");
        Api.Matriculado ana = Api.matriculado(periodoDaProva(prova), "Ana Souza", "2008-03-15", 2);
        long notaId = Api.lancarNota(prova, ana.matriculaId(), "7.5").then().statusCode(201)
                .extract().jsonPath().getLong("id");
        Api.como(Api.SECRETARIA).body(Map.of("nota", 9))
                .put("/provas/" + outra + "/notas/" + notaId)
                .then().statusCode(404).body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
    }
}
