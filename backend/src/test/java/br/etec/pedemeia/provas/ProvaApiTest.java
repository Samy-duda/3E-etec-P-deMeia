package br.etec.pedemeia.provas;

import br.etec.pedemeia.teste.Api;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/** MVP-RF04, MVP-CA08 e MVP-CA09. */
@QuarkusTest
class ProvaApiTest {

    @Test
    void mvpCa08_provaValidaEhCriadaVinculadaAoPeriodo() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        Api.como(Api.SECRETARIA)
                .body(Map.of("periodoLetivoId", periodo, "titulo", "Simulado de Matemática",
                        "dataAplicacao", "2026-11-20", "notaMaxima", 10.00))
                .post("/provas")
                .then().statusCode(201)
                .body("titulo", equalTo("Simulado de Matemática"))
                .body("periodoLetivoId", equalTo((int) periodo))
                .body("notaMaxima", equalTo(10.0f))
                .body("criadaPorId", notNullValue())
                .body("criadaEm", notNullValue());
        Api.como(Api.SECRETARIA).get("/provas?periodoLetivoId=" + periodo)
                .then().statusCode(200).body("totalElementos", equalTo(1));
    }

    @Test
    void mvpCa09_notaMaximaZeroNegativaOuAusenteEhRecusada() {
        long periodo = Api.criarPeriodo("2026-1 " + Api.sufixo());
        Map<String, Object> base = new HashMap<>();
        base.put("periodoLetivoId", periodo);
        base.put("titulo", "Prova inválida");
        base.put("dataAplicacao", "2026-11-20");

        for (Object valor : new Object[]{0, -5, null}) {
            Map<String, Object> corpo = new HashMap<>(base);
            if (valor != null) {
                corpo.put("notaMaxima", valor);
            }
            Api.como(Api.SECRETARIA).body(corpo).post("/provas")
                    .then().statusCode(400)
                    .body("codigo", equalTo("VALIDACAO_INVALIDA"))
                    .body("campos[0].campo", equalTo("notaMaxima"));
        }
        Api.como(Api.SECRETARIA).get("/provas?periodoLetivoId=" + periodo)
                .then().statusCode(200).body("totalElementos", equalTo(0));
    }

    @Test
    void provaComPeriodoInexistenteDevolve404() {
        Api.como(Api.SECRETARIA)
                .body(Map.of("periodoLetivoId", 999999999, "titulo", "X",
                        "dataAplicacao", "2026-11-20", "notaMaxima", 10))
                .post("/provas")
                .then().statusCode(404).body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
    }
}
