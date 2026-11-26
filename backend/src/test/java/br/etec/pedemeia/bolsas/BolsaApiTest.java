package br.etec.pedemeia.bolsas;

import br.etec.pedemeia.teste.Api;
import br.etec.pedemeia.teste.Cenario;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** MVP-RF09, MVP-RF10, MVP-CA24 a MVP-CA27, MVP-CA29 e MVP-CA30. */
@QuarkusTest
class BolsaApiTest {

    private record Processo(long periodo, long prova, long processo) {
    }

    /** Processo com notas e classificação gerada: 12 alunos no ano 1 e 12 no ano 2 (10 selecionados em cada). */
    private static Processo processoClassificado() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long prova = Api.criarProva(periodo, "Simulado", "10.00");
        long processo = Api.criarProcesso(periodo, prova);
        Cenario.turma(periodo, prova, 1, 12, "2008-01-01");
        Cenario.turma(periodo, prova, 2, 12, "2008-01-01");
        Api.gerarClassificacao(processo).then().statusCode(201);
        return new Processo(periodo, prova, processo);
    }

    private static Set<Long> selecionados(long processoId) {
        Response r = Api.classificacao(processoId);
        List<Map<String, Object>> itens = r.jsonPath().getList("itens");
        return itens.stream().filter(i -> (Boolean) i.get("selecionado"))
                .map(i -> ((Number) i.get("matriculaId")).longValue()).collect(Collectors.toSet());
    }

    private static long matriculaDaPosicao(long processoId, int ano, int posicao) {
        List<Map<String, Object>> itens = Api.classificacao(processoId).jsonPath().getList("itens");
        return itens.stream()
                .filter(i -> ((Number) i.get("anoEscolar")).intValue() == ano)
                .filter(i -> ((Number) i.get("posicao")).intValue() == posicao)
                .map(i -> ((Number) i.get("matriculaId")).longValue()).findFirst().orElseThrow();
    }

    @Test
    void mvpCa24_concessaoEmLoteDaUmaBolsaAtivaParaCadaSelecionado() {
        Processo p = processoClassificado();
        Response r = Api.como(Api.SECRETARIA).post("/processos-seletivos/" + p.processo() + "/bolsas");
        r.then().statusCode(201).body("size()", equalTo(20))
                .body("valorMensal", everyItem(equalTo(300.0f)))
                .body("situacao", everyItem(equalTo("ATIVA")))
                .body("dataInicio", everyItem(org.hamcrest.Matchers.notNullValue()))
                .body("dataFim", everyItem(nullValue()));
        Set<Long> bolsistas = r.jsonPath().getList("matriculaId").stream()
                .map(o -> ((Number) o).longValue()).collect(Collectors.toSet());
        assertEquals(selecionados(p.processo()), bolsistas, "exatamente os selecionados recebem bolsa");
        Api.bolsas(p.processo()).then().statusCode(200).body("size()", equalTo(20));
    }

    @Test
    void mvpCa25_valorDiferenteDeTrezentosEhRecusado() {
        Processo p = processoClassificado();
        long selecionado = matriculaDaPosicao(p.processo(), 1, 1);
        Api.como(Api.SECRETARIA).body(Map.of("matriculaId", selecionado, "valorMensal", 350))
                .post("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(422).body("codigo", equalTo("VALOR_BOLSA_INVALIDO"));
        Api.como(Api.SECRETARIA).body(Map.of("valorMensal", 350))
                .post("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(422).body("codigo", equalTo("VALOR_BOLSA_INVALIDO"));
        Api.bolsas(p.processo()).then().statusCode(200).body("size()", equalTo(0));
    }

    @Test
    void mvpCa25_complemento_valorIgualAoDeConfiguracaoEhAceito() {
        Processo p = processoClassificado();
        long selecionado = matriculaDaPosicao(p.processo(), 1, 1);
        Api.como(Api.SECRETARIA).body(Map.of("matriculaId", selecionado, "valorMensal", 300.00))
                .post("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(201).body("[0].valorMensal", equalTo(300.0f));
    }

    @Test
    void mvpCa26_alunoNaoSelecionadoNaoRecebeBolsa() {
        Processo p = processoClassificado();
        long onzeDoAnoUm = matriculaDaPosicao(p.processo(), 1, 11);
        Api.como(Api.SECRETARIA).body(Map.of("matriculaId", onzeDoAnoUm))
                .post("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(422).body("codigo", equalTo("ALUNO_NAO_SELECIONADO"));
        Api.bolsas(p.processo()).then().statusCode(200).body("size()", equalTo(0));
    }

    @Test
    void mvpCa27_bolsaNaoEhDuplicadaNoProcesso() {
        Processo p = processoClassificado();
        long ana = matriculaDaPosicao(p.processo(), 2, 1);
        Api.como(Api.SECRETARIA).body(Map.of("matriculaId", ana))
                .post("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(201);
        Api.como(Api.SECRETARIA).body(Map.of("matriculaId", ana))
                .post("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(409).body("codigo", equalTo("BOLSA_JA_EXISTE"));
        Api.bolsas(p.processo()).then().statusCode(200).body("size()", equalTo(1));
    }

    @Test
    void loteRepetidoSemPendentesEhRecusado() {
        Processo p = processoClassificado();
        Api.como(Api.SECRETARIA).post("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(201);
        Api.como(Api.SECRETARIA).post("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(409).body("codigo", equalTo("BOLSAS_JA_CONCEDIDAS"));
        Api.bolsas(p.processo()).then().statusCode(200).body("size()", equalTo(20));
    }

    @Test
    void mvpCa29_consultaDeBolsasTraziaAlunoValorESituacao() {
        Processo p = processoClassificado();
        Api.como(Api.SECRETARIA).post("/processos-seletivos/" + p.processo() + "/bolsas").then().statusCode(201);
        Api.bolsas(p.processo()).then().statusCode(200)
                .body("[0].nomeAluno", org.hamcrest.Matchers.notNullValue())
                .body("valorMensal", everyItem(equalTo(300.0f)))
                .body("situacao", everyItem(equalTo("ATIVA")));
    }

    @Test
    void mvpCa10c_consultaNaoRecebeMatriculaNemAutoriaDaBolsa() {
        Processo p = processoClassificado();
        Api.como(Api.SECRETARIA).post("/processos-seletivos/" + p.processo() + "/bolsas").then().statusCode(201);
        Api.como(Api.CONSULTA).get("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(200)
                .body("[0].matriculaEscolar", nullValue())
                .body("[0].criadaPorId", nullValue())
                .body("[0].criadaEm", nullValue())
                .body("[0].nomeAluno", not(equalTo("")));
        Api.como(Api.ADMIN).get("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(200)
                .body("[0].matriculaEscolar", not(nullValue()))
                .body("[0].criadaPorId", equalTo((int) Api.idDo(Api.SECRETARIA)));
    }

    @Test
    void mvpCa30_cancelamentoEAlteracaoDeSituacaoIndisponiveis() {
        Processo p = processoClassificado();
        Api.como(Api.SECRETARIA).post("/processos-seletivos/" + p.processo() + "/bolsas").then().statusCode(201);
        long bolsaId = Api.bolsas(p.processo()).jsonPath().getLong("[0].id");
        String caminho = "/processos-seletivos/" + p.processo() + "/bolsas/" + bolsaId;
        Api.como(Api.SECRETARIA).body(Map.of("situacao", "CANCELADA")).put(caminho).then().statusCode(404);
        Api.como(Api.SECRETARIA).delete(caminho).then().statusCode(404);
        Api.como(Api.SECRETARIA).body(Map.of("motivo", "teste"))
                .post(caminho + "/cancelamento").then().statusCode(404);
        Api.bolsas(p.processo()).then().statusCode(200)
                .body("[0].situacao", equalTo("ATIVA"));
    }

    @Test
    void consultaNaoPodeConcederBolsa() {
        Processo p = processoClassificado();
        Api.como(Api.CONSULTA).post("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(403).body("codigo", equalTo("ACESSO_NEGADO"));
        Api.bolsas(p.processo()).then().statusCode(200).body("size()", equalTo(0));
    }

    @Test
    void mvpRnf06_concessaoRegistraQuemConcedeu() {
        Processo p = processoClassificado();
        long secretaria = Api.idDo(Api.SECRETARIA);
        Api.como(Api.SECRETARIA).post("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(201).body("[0].criadaPorId", equalTo((int) secretaria));
        Api.como(Api.ADMIN).get("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(200)
                .body("criadaPorId", everyItem(equalTo((int) secretaria)))
                .body("criadaEm", everyItem(org.hamcrest.Matchers.notNullValue()));
    }
}
