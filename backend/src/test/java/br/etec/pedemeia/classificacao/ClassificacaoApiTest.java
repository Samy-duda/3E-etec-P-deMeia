package br.etec.pedemeia.classificacao;

import br.etec.pedemeia.teste.Api;
import br.etec.pedemeia.teste.Cenario;
import io.restassured.response.Response;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/** MVP-RF07, MVP-RF08, MVP-CA16 a MVP-CA23 e MVP-CA28. */
@QuarkusTest
class ClassificacaoApiTest {

    private record Processo(long periodo, long prova, long processo) {
    }

    private static Processo novoProcesso() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long prova = Api.criarProva(periodo, "Simulado de Matemática", "10.00");
        long processo = Api.criarProcesso(periodo, prova);
        return new Processo(periodo, prova, processo);
    }

    private static List<Map<String, Object>> itens(Response resposta) {
        return resposta.jsonPath().getList("itens");
    }

    private static List<Map<String, Object>> itensDoAno(Response resposta, int ano) {
        return itens(resposta).stream().filter(i -> ((Number) i.get("anoEscolar")).intValue() == ano).toList();
    }

    private static List<Integer> posicoes(List<Map<String, Object>> lista) {
        return lista.stream().map(i -> ((Number) i.get("posicao")).intValue()).sorted().toList();
    }

    private static int posicaoDe(Response resposta, long matriculaId) {
        return itens(resposta).stream()
                .filter(i -> ((Number) i.get("matriculaId")).longValue() == matriculaId)
                .map(i -> ((Number) i.get("posicao")).intValue()).findFirst().orElseThrow();
    }

    private static boolean selecionadoDe(Response resposta, long matriculaId) {
        return itens(resposta).stream()
                .filter(i -> ((Number) i.get("matriculaId")).longValue() == matriculaId)
                .map(i -> (Boolean) i.get("selecionado")).findFirst().orElseThrow();
    }

    @Test
    void mvpCa16_provaSemNotasBloqueiaAClassificacaoESemBolsas() {
        Processo p = novoProcesso();
        Api.gerarClassificacao(p.processo()).then().statusCode(422)
                .body("codigo", equalTo("SEM_NOTAS_PARA_CLASSIFICACAO"));
        Api.classificacao(p.processo()).then().statusCode(200)
                .body("calculadaEm", nullValue())
                .body("itens", org.hamcrest.Matchers.empty());
        Api.como(Api.ADMIN).body(Map.of()).post("/processos-seletivos/" + p.processo() + "/bolsas")
                .then().statusCode(422).body("codigo", equalTo("CLASSIFICACAO_NAO_GERADA"));
        Api.bolsas(p.processo()).then().statusCode(200).body("size()", equalTo(0));
    }

    @Test
    void mvpCa17_classificacaoSeparadaPorAnoComPosicoesDeUmASeis() {
        Processo p = novoProcesso();
        for (int ano = 1; ano <= 3; ano++) {
            Cenario.turma(p.periodo(), p.prova(), ano, 6, "2008-01-01");
        }
        Response r = Api.gerarClassificacao(p.processo());
        r.then().statusCode(201).body("itens.size()", equalTo(18));
        for (int ano = 1; ano <= 3; ano++) {
            assertEquals(List.of(1, 2, 3, 4, 5, 6), posicoes(itensDoAno(r, ano)));
        }
    }

    @Test
    void mvpCa18_posicoesSequenciaisEFinalEhANotaDaProva() {
        Processo p = novoProcesso();
        Api.Matriculado ana = Api.matriculado(p.periodo(), "Ana Souza", "2008-03-15", 2);
        Api.Matriculado bruno = Api.matriculado(p.periodo(), "Bruno Lima", "2008-05-10", 2);
        Api.Matriculado carla = Api.matriculado(p.periodo(), "Carla Mendes", "2008-01-01", 2);
        Api.lancarNota(p.prova(), ana.matriculaId(), "9.0").then().statusCode(201);
        Api.lancarNota(p.prova(), bruno.matriculaId(), "9.0").then().statusCode(201);
        Api.lancarNota(p.prova(), carla.matriculaId(), "7.0").then().statusCode(201);

        Response r = Api.gerarClassificacao(p.processo());
        r.then().statusCode(201).body("vagasPorAno", equalTo(10));
        assertEquals(1, posicaoDe(r, ana.matriculaId()));
        assertEquals(2, posicaoDe(r, bruno.matriculaId()));
        assertEquals(3, posicaoDe(r, carla.matriculaId()));
        assertEquals(List.of(1, 2, 3), posicoes(itensDoAno(r, 2)));
        itensDoAno(r, 2).forEach(i -> assertTrue(((Number) i.get("notaFinal")).doubleValue() > 6.9));
    }

    @Test
    void mvpCa19_empateDeNotaVenceQuemNasceuPrimeiro() {
        Processo p = novoProcesso();
        Api.Matriculado ana = Api.matriculado(p.periodo(), "Ana Souza", "2008-03-15", 2);
        Api.Matriculado bruno = Api.matriculado(p.periodo(), "Bruno Lima", "2007-05-10", 2);
        Api.lancarNota(p.prova(), ana.matriculaId(), "9.0").then().statusCode(201);
        Api.lancarNota(p.prova(), bruno.matriculaId(), "9.0").then().statusCode(201);
        Response r = Api.gerarClassificacao(p.processo());
        assertEquals(1, posicaoDe(r, bruno.matriculaId()));
        assertEquals(2, posicaoDe(r, ana.matriculaId()));
    }

    @Test
    void mvpCa20_empateDeNascimentoEMesmaNotaVenceOrdemAlfabetica() {
        Processo p = novoProcesso();
        Api.Matriculado diego = Api.matriculado(p.periodo(), "Diego Rocha", "2008-01-01", 1);
        Api.Matriculado carla = Api.matriculado(p.periodo(), "Carla Mendes", "2008-01-01", 1);
        Api.lancarNota(p.prova(), diego.matriculaId(), "8.0").then().statusCode(201);
        Api.lancarNota(p.prova(), carla.matriculaId(), "8.0").then().statusCode(201);
        Response r = Api.gerarClassificacao(p.processo());
        assertEquals(1, posicaoDe(r, carla.matriculaId()));
        assertEquals(2, posicaoDe(r, diego.matriculaId()));
    }

    @Test
    void mvpCa21_selecionaExatamenteOsDezPrimeirosDoAno() {
        Processo p = novoProcesso();
        List<Api.Matriculado> turma = Cenario.turma(p.periodo(), p.prova(), 2, 12, "2008-01-01");
        Response r = Api.gerarClassificacao(p.processo());
        for (int i = 0; i < 10; i++) {
            assertTrue(selecionadoDe(r, turma.get(i).matriculaId()), "posição " + (i + 1) + " deve ser selecionada");
        }
        assertFalse(selecionadoDe(r, turma.get(10).matriculaId()), "11º não é selecionado");
        assertFalse(selecionadoDe(r, turma.get(11).matriculaId()), "12º não é selecionado");
    }

    @Test
    void mvpCa22_empateNaFronteiraDoTop10() {
        Processo p = novoProcesso();
        List<Api.Matriculado> outros = new java.util.ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Api.Matriculado m = Api.matriculado(p.periodo(), "Colega " + i, "2008-06-01", 1);
            Api.lancarNota(p.prova(), m.matriculaId(), String.format(java.util.Locale.ROOT, "%.2f", 9.0 - i * 0.1))
                    .then().statusCode(201);
            outros.add(m);
        }
        Api.Matriculado ana = Api.matriculado(p.periodo(), "Ana Souza", "2008-03-15", 1);
        Api.Matriculado bruno = Api.matriculado(p.periodo(), "Bruno Lima", "2008-05-10", 1);
        Api.lancarNota(p.prova(), ana.matriculaId(), "7.0").then().statusCode(201);
        Api.lancarNota(p.prova(), bruno.matriculaId(), "7.0").then().statusCode(201);

        Response r = Api.gerarClassificacao(p.processo());
        assertEquals(10, posicaoDe(r, ana.matriculaId()));
        assertEquals(11, posicaoDe(r, bruno.matriculaId()));
        assertTrue(selecionadoDe(r, ana.matriculaId()));
        assertFalse(selecionadoDe(r, bruno.matriculaId()));
        assertEquals(10, itensDoAno(r, 1).stream().filter(i -> (Boolean) i.get("selecionado")).count());
    }

    @Test
    void mvpCa23_anoComMenosDeDezAlunosSelecionaTodos() {
        Processo p = novoProcesso();
        Cenario.turma(p.periodo(), p.prova(), 3, 7, "2008-01-01");
        Response r = Api.gerarClassificacao(p.processo());
        r.then().statusCode(201);
        assertEquals(7, itensDoAno(r, 3).stream().filter(i -> (Boolean) i.get("selecionado")).count());
    }

    @Test
    void geracaoRepetidaEhRecusadaEEClassificacaoNaoMuda() {
        Processo p = novoProcesso();
        Cenario.turma(p.periodo(), p.prova(), 1, 3, "2008-01-01");
        Api.gerarClassificacao(p.processo()).then().statusCode(201);
        Api.gerarClassificacao(p.processo()).then().statusCode(409)
                .body("codigo", equalTo("CLASSIFICACAO_JA_GERADA"));
        Api.classificacao(p.processo()).then().statusCode(200).body("itens.size()", equalTo(3));
    }

    @Test
    void mvpCa28_consultaPorAnoEmOrdemDePosicaoComPosicaoNotaESituacao() {
        Processo p = novoProcesso();
        Cenario.turma(p.periodo(), p.prova(), 2, 4, "2008-01-01");
        Api.gerarClassificacao(p.processo()).then().statusCode(201);
        Response r = Api.como(Api.SECRETARIA).get("/processos-seletivos/" + p.processo() + "/classificacao?anoEscolar=2");
        r.then().statusCode(200);
        List<Map<String, Object>> lista = itens(r);
        assertEquals(List.of(1, 2, 3, 4), lista.stream().map(i -> ((Number) i.get("posicao")).intValue()).toList());
        assertEquals(9.5, ((Number) lista.get(0).get("notaFinal")).doubleValue(), 0.001);
        assertTrue(lista.stream().allMatch(i -> i.get("selecionado") != null && i.get("nomeAluno") != null));
        assertTrue(lista.stream().allMatch(i -> i.get("matriculaEscolar") != null), "ADMIN e SECRETARIA veem a matrícula");
    }

    @Test
    void consultaComAnoForaDeUmATresEhRecusada() {
        Processo p = novoProcesso();
        Api.como(Api.SECRETARIA).get("/processos-seletivos/" + p.processo() + "/classificacao?anoEscolar=4")
                .then().statusCode(400).body("campos[0].campo", equalTo("anoEscolar"));
    }

    @Test
    void notaNaoPodeSerLancadaDepoisDaClassificacao() {
        Processo p = novoProcesso();
        Cenario.turma(p.periodo(), p.prova(), 1, 2, "2008-01-01");
        Api.gerarClassificacao(p.processo()).then().statusCode(201);
        Api.Matriculado tardio = Api.matriculado(p.periodo(), "Tardio", "2008-01-01", 1);
        Api.lancarNota(p.prova(), tardio.matriculaId(), "5.0")
                .then().statusCode(409).body("codigo", equalTo("CLASSIFICACAO_JA_GERADA"));
        Api.como(Api.SECRETARIA).get("/provas/" + p.prova() + "/notas")
                .then().statusCode(200).body("totalElementos", equalTo(2));
    }

    @Test
    void consultaAntesDaGeracaoDevolveVazio() {
        Processo p = novoProcesso();
        Api.classificacao(p.processo()).then().statusCode(200)
                .body("calculadaEm", nullValue())
                .body("vagasPorAno", equalTo(10))
                .body("itens.size()", equalTo(0));
    }
}
