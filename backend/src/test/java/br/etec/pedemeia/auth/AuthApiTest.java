package br.etec.pedemeia.auth;

import br.etec.pedemeia.teste.Api;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/** MVP-RNF01 e MVP-CA10 (cenário b): autenticação por JWT. */
@QuarkusTest
class AuthApiTest {

    @Test
    void loginValidoDevolveTokenBearerComUsuario() {
        Api.anonimo().body(Map.of("email", Api.ADMIN, "senha", "TesteIA_2026"))
                .post("/auth/login")
                .then().statusCode(200)
                .body("token", notNullValue())
                .body("tipo", equalTo("Bearer"))
                .body("expiraEm", notNullValue())
                .body("usuario.email", equalTo(Api.ADMIN))
                .body("usuario.perfil", equalTo("ADMIN"));
    }

    @Test
    void senhaErradaDevolveCredenciaisInvalidas() {
        Api.anonimo().body(Map.of("email", Api.ADMIN, "senha", "senha-errada"))
                .post("/auth/login")
                .then().statusCode(401)
                .body("codigo", equalTo("CREDENCIAIS_INVALIDAS"))
                .body("caminho", equalTo("/api/auth/login"));
    }

    @Test
    void emailInexistenteDevolveMesmaMensagemDeCredenciais() {
        Api.anonimo().body(Map.of("email", "test@IA_inexistente", "senha", "TesteIA_2026"))
                .post("/auth/login")
                .then().statusCode(401).body("codigo", equalTo("CREDENCIAIS_INVALIDAS"));
    }

    @Test
    void loginSemCampoSenhaDevolveValidacaoComCampo() {
        Api.anonimo().body(Map.of("email", Api.ADMIN))
                .post("/auth/login")
                .then().statusCode(400)
                .body("codigo", equalTo("VALIDACAO_INVALIDA"))
                .body("campos[0].campo", equalTo("senha"));
    }

    @Test
    void mvpCa10b_requisicaoSemTokenNaoRecebeDados() {
        Api.anonimo().get("/alunos").then().statusCode(401)
                .body("codigo", equalTo("NAO_AUTENTICADO"));
    }

    @Test
    void tokenInvalidoEhRecusado() {
        Api.anonimo().header("Authorization", "Bearer token.invalido.aqui").get("/auth/me")
                .then().statusCode(401).body("codigo", equalTo("NAO_AUTENTICADO"));
    }

    @Test
    void authMeDevolveUsuarioDoToken() {
        Api.como(Api.CONSULTA).get("/auth/me").then().statusCode(200)
                .body("email", equalTo(Api.CONSULTA))
                .body("perfil", equalTo("CONSULTA"));
    }
}
