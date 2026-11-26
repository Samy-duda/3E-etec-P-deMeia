package br.etec.pedemeia.seguranca;

import br.etec.pedemeia.teste.Api;
import br.etec.pedemeia.teste.Cenario;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** MVP-CA10 (cenários a, b e c), MVP-RNF01 e MVP-RNF02. Usa só as contas test@IA_*. */
@QuarkusTest
class PerfisApiTest {

    @Test
    void mvpCa10a_consultaNaoCadastraAlunoERegistroNaoMuda() {
        String matricula = "CA10-" + Api.sufixo();
        Api.como(Api.CONSULTA)
                .body(Map.of("nome", "Ana Souza", "matriculaEscolar", matricula, "dataNascimento", "2008-03-15"))
                .post("/alunos")
                .then().statusCode(403).body("codigo", equalTo("ACESSO_NEGADO"));
        Api.como(Api.ADMIN).get("/alunos?matriculaEscolar=" + matricula)
                .then().statusCode(200).body("totalElementos", equalTo(0));
    }

    @Test
    void mvpCa10a_consultaNaoRegistraNotaNemCorrigeNota() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long prova = Api.criarProva(periodo, "Simulado", "10.00");
        Api.Matriculado ana = Api.matriculado(periodo, "Ana Souza", "2008-03-15", 2);
        Api.como(Api.CONSULTA).body(Map.of("matriculaId", ana.matriculaId(), "nota", 7.5))
                .post("/provas/" + prova + "/notas")
                .then().statusCode(403).body("codigo", equalTo("ACESSO_NEGADO"));
        Api.como(Api.CONSULTA).get("/provas/" + prova + "/notas")
                .then().statusCode(403);
        Api.como(Api.ADMIN).get("/provas/" + prova + "/notas")
                .then().statusCode(200).body("totalElementos", equalTo(0));
    }

    @Test
    void mvpCa10a_consultaNaoCriaPeriodoProvaNemProcesso() {
        Api.como(Api.CONSULTA).body(Map.of("nome", "2026-X", "dataInicio", "2026-01-01", "dataFim", "2026-06-01"))
                .post("/periodos-letivos").then().statusCode(403);
        Api.como(Api.CONSULTA).body(Map.of("periodoLetivoId", 1, "titulo", "x", "dataAplicacao", "2026-01-01", "notaMaxima", 10))
                .post("/provas").then().statusCode(403);
        Api.como(Api.CONSULTA).body(Map.of("periodoLetivoId", 1, "provaId", 1))
                .post("/processos-seletivos").then().statusCode(403);
        Api.como(Api.CONSULTA).post("/processos-seletivos/1/classificacao").then().statusCode(403);
    }

    @Test
    void mvpCa10a_secretariaNaoAcessaGestaoDeUsuarios() {
        Api.como(Api.SECRETARIA).get("/usuarios").then().statusCode(403).body("codigo", equalTo("ACESSO_NEGADO"));
        Api.como(Api.ADMIN).get("/usuarios").then().statusCode(200).body("conteudo", notNullValue());
    }

    @Test
    void mvpCa10b_semAutenticacaoNaoHaDadosDeAlunosNotasOuClassificacoes() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long prova = Api.criarProva(periodo, "Simulado", "10.00");
        long processo = Api.criarProcesso(periodo, prova);
        Cenario.turma(periodo, prova, 1, 2, "2008-01-01");
        Api.gerarClassificacao(processo).then().statusCode(201);

        for (String caminho : List.of("/alunos", "/provas/" + prova + "/notas", "/processos-seletivos/" + processo + "/classificacao",
                "/processos-seletivos/" + processo + "/bolsas", "/periodos-letivos", "/auth/me")) {
            Response r = Api.anonimo().get(caminho);
            r.then().statusCode(401).body("codigo", equalTo("NAO_AUTENTICADO"));
            org.junit.jupiter.api.Assertions.assertFalse(r.asString().contains("nomeAluno"),
                    "sem token, nenhum dado de aluno é devolvido: " + caminho);
        }
    }

    @Test
    void mvpCa10c_classificacaoParaConsultaNaoTraziaMatriculaNemDataDeNascimento() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long prova = Api.criarProva(periodo, "Simulado", "10.00");
        long processo = Api.criarProcesso(periodo, prova);
        Cenario.turma(periodo, prova, 2, 3, "2008-01-01");
        Api.gerarClassificacao(processo).then().statusCode(201);

        Response consulta = Api.classificacao(processo);
        consulta.then().statusCode(200)
                .body("itens.size()", equalTo(3))
                .body("itens.nomeAluno", everyItem(notNullValue()))
                .body("itens.posicao", everyItem(notNullValue()))
                .body("itens.notaFinal", everyItem(notNullValue()))
                .body("itens.selecionado", everyItem(notNullValue()))
                .body("itens.matriculaEscolar", everyItem(nullValue()))
                .body("itens.dataNascimento", everyItem(nullValue()));
        assertFalse(consulta.asString().contains("dataNascimento"), "dataNascimento nunca sai em classificação");
        assertFalse(consulta.asString().contains("matriculaEscolar"), "matriculaEscolar não sai para CONSULTA");

        Api.como(Api.ADMIN).get("/processos-seletivos/" + processo + "/classificacao")
                .then().statusCode(200)
                .body("itens.matriculaEscolar", everyItem(not(nullValue())));
    }

    @Test
    void consultaVeProcessosEClassificacaoMasNaoAlunos() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long prova = Api.criarProva(periodo, "Simulado", "10.00");
        long processo = Api.criarProcesso(periodo, prova);
        Api.como(Api.CONSULTA).get("/processos-seletivos/" + processo).then().statusCode(200);
        Api.classificacao(processo).then().statusCode(200);
        Api.como(Api.CONSULTA).get("/alunos").then().statusCode(403);
        Api.como(Api.CONSULTA).get("/periodos-letivos/" + periodo + "/matriculas").then().statusCode(403);
    }

    @Test
    void mvpRnf02_consultaNaoRecebeDadosPessoaisNaBolsa() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long prova = Api.criarProva(periodo, "Simulado", "10.00");
        long processo = Api.criarProcesso(periodo, prova);
        Cenario.turma(periodo, prova, 1, 2, "2008-01-01");
        Api.gerarClassificacao(processo).then().statusCode(201);
        Api.como(Api.SECRETARIA).post("/processos-seletivos/" + processo + "/bolsas").then().statusCode(201);
        Response bolsas = Api.bolsas(processo);
        bolsas.then().statusCode(200).body("matriculaEscolar", everyItem(nullValue()));
        assertFalse(bolsas.asString().contains("criadaPorId"));
        assertFalse(bolsas.asString().contains("criadaEm"));
    }
}
