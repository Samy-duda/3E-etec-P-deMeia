package br.etec.pedemeia.processos;

import br.etec.pedemeia.teste.Api;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/** MVP-RF06, MVP-CA15 e a regra de um processo por prova (P-DER-4, Q16). */
@QuarkusTest
class ProcessoApiTest {

    @Test
    void mvpCa15_processoLigaPeriodoEProvaEFicaCriado() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long prova = Api.criarProva(periodo, "Simulado de Matemática", "10.00");
        Api.como(Api.SECRETARIA).body(Map.of("periodoLetivoId", periodo, "provaId", prova))
                .post("/processos-seletivos")
                .then().statusCode(201)
                .body("periodoLetivoId", equalTo((int) periodo))
                .body("provaId", equalTo((int) prova))
                .body("status", equalTo("CRIADO"))
                .body("criadoPorId", notNullValue());
    }

    @Test
    void mvpCa15_consultaDoProcessoEPermitidaParaConsulta() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long prova = Api.criarProva(periodo, "Simulado", "10.00");
        long processo = Api.criarProcesso(periodo, prova);
        Api.como(Api.CONSULTA).get("/processos-seletivos/" + processo)
                .then().statusCode(200).body("provaId", equalTo((int) prova));
        Api.como(Api.CONSULTA).get("/processos-seletivos?periodoLetivoId=" + periodo)
                .then().statusCode(200).body("totalElementos", equalTo(1));
    }

    @Test
    void provaDeOutroPeriodoEhRecusada() {
        long periodoA = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long periodoB = Api.criarPeriodo("2026-2 " + Api.sufixo());
        long provaDeA = Api.criarProva(periodoA, "Simulado", "10.00");
        Api.como(Api.SECRETARIA).body(Map.of("periodoLetivoId", periodoB, "provaId", provaDeA))
                .post("/processos-seletivos")
                .then().statusCode(422).body("codigo", equalTo("PROVA_NAO_PERTENCE_AO_PERIODO"));
    }

    @Test
    void umaProvaTemNoMaximoUmProcesso() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        long prova = Api.criarProva(periodo, "Simulado", "10.00");
        Api.criarProcesso(periodo, prova);
        Api.como(Api.SECRETARIA).body(Map.of("periodoLetivoId", periodo, "provaId", prova))
                .post("/processos-seletivos")
                .then().statusCode(409).body("codigo", equalTo("PROCESSO_JA_EXISTE_PARA_PROVA"));
    }

    @Test
    void processoParaProvaInexistenteDevolve404() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        Api.como(Api.SECRETARIA).body(Map.of("periodoLetivoId", periodo, "provaId", 999999999))
                .post("/processos-seletivos")
                .then().statusCode(404).body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
    }
}
