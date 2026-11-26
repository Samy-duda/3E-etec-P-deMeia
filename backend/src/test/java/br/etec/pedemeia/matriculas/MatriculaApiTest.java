package br.etec.pedemeia.matriculas;

import br.etec.pedemeia.teste.Api;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

/** MVP-RF03, MVP-CA05 a MVP-CA07. */
@QuarkusTest
class MatriculaApiTest {

    @Test
    void mvpCa05_matriculaComAnoEscolarValidoEhCriada() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long aluno = Api.criarAluno("Ana Souza", "M" + Api.sufixo(), "2008-03-15");
        Api.como(Api.SECRETARIA).body(Map.of("alunoId", aluno, "anoEscolar", 2))
                .post("/periodos-letivos/" + periodo + "/matriculas")
                .then().statusCode(201)
                .body("anoEscolar", equalTo(2))
                .body("periodoLetivoId", equalTo((int) periodo))
                .body("nomeAluno", equalTo("Ana Souza"))
                .body("status", equalTo("ATIVA"));
    }

    @Test
    void mvpCa06_anoEscolarForaDeUmATresEhRecusado() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long aluno = Api.criarAluno("Ana Souza", "M" + Api.sufixo(), "2008-03-15");
        for (int ano : new int[]{4, 0}) {
            Api.como(Api.SECRETARIA).body(Map.of("alunoId", aluno, "anoEscolar", ano))
                    .post("/periodos-letivos/" + periodo + "/matriculas")
                    .then().statusCode(400)
                    .body("codigo", equalTo("VALIDACAO_INVALIDA"))
                    .body("campos[0].campo", equalTo("anoEscolar"));
        }
        Api.como(Api.SECRETARIA).get("/periodos-letivos/" + periodo + "/matriculas")
                .then().statusCode(200).body("totalElementos", equalTo(0));
    }

    @Test
    void mvpCa07_segundaMatriculaNoMesmoPeriodoEhRecusadaSemAlterarOriginal() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long aluno = Api.criarAluno("Ana Souza", "M" + Api.sufixo(), "2008-03-15");
        Api.como(Api.SECRETARIA).body(Map.of("alunoId", aluno, "anoEscolar", 2))
                .post("/periodos-letivos/" + periodo + "/matriculas").then().statusCode(201);
        Api.como(Api.SECRETARIA).body(Map.of("alunoId", aluno, "anoEscolar", 3))
                .post("/periodos-letivos/" + periodo + "/matriculas")
                .then().statusCode(409).body("codigo", equalTo("MATRICULA_DUPLICADA"));
        Api.como(Api.SECRETARIA).get("/periodos-letivos/" + periodo + "/matriculas")
                .then().statusCode(200)
                .body("totalElementos", equalTo(1))
                .body("conteudo[0].anoEscolar", equalTo(2));
    }

    @Test
    void periodoInexistenteDevolve404() {
        long aluno = Api.criarAluno("Ana Souza", "M" + Api.sufixo(), "2008-03-15");
        Api.como(Api.SECRETARIA).body(Map.of("alunoId", aluno, "anoEscolar", 1))
                .post("/periodos-letivos/999999999/matriculas")
                .then().statusCode(404).body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
    }

    @Test
    void periodoComDataFimAnteriorAInicioEhRecusado() {
        Api.como(Api.SECRETARIA)
                .body(Map.of("nome", "Invertido " + Api.sufixo(), "dataInicio", "2026-12-01", "dataFim", "2026-01-01"))
                .post("/periodos-letivos")
                .then().statusCode(400).body("campos[0].campo", equalTo("dataFim"));
    }
}
