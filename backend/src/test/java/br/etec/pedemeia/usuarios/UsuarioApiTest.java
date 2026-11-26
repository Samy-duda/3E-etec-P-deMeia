package br.etec.pedemeia.usuarios;

import br.etec.pedemeia.teste.Api;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/** MVP-RNF01: gestão de contas (ADMIN). A senha nunca é devolvida. */
@QuarkusTest
class UsuarioApiTest {

    @Test
    void adminCriaContaSemDevolverSenha() {
        String email = "test@IA_troca" + Api.sufixo();
        Api.como(Api.ADMIN)
                .body(Map.of("nome", "Teste IA Troca", "email", email, "senha", "SenhaForte123", "perfil", "SECRETARIA"))
                .post("/usuarios")
                .then().statusCode(201)
                .body("email", equalTo(email))
                .body("perfil", equalTo("SECRETARIA"))
                .body("ativo", equalTo(true))
                .body("senha", nullValue())
                .body("senhaHash", nullValue())
                .body("criadoEm", notNullValue());
        Api.anonimo().body(Map.of("email", email, "senha", "SenhaForte123"))
                .post("/auth/login").then().statusCode(200).body("usuario.perfil", equalTo("SECRETARIA"));
    }

    @Test
    void emailDuplicadoEhRecusado() {
        Api.como(Api.ADMIN)
                .body(Map.of("nome", "Outra", "email", Api.ADMIN, "senha", "SenhaForte123", "perfil", "ADMIN"))
                .post("/usuarios")
                .then().statusCode(409).body("codigo", equalTo("EMAIL_DUPLICADO"));
    }

    @Test
    void perfilInvalidoOuSenhaCurtaSaoRecusados() {
        Api.como(Api.ADMIN)
                .body(Map.of("nome", "X", "email", "test@IA_x" + Api.sufixo(), "senha", "curta", "perfil", "ROOT"))
                .post("/usuarios")
                .then().statusCode(400).body("codigo", equalTo("VALIDACAO_INVALIDA"));
    }

    @Test
    void listagemDeContasEhPaginadaParaAdmin() {
        Api.como(Api.ADMIN).get("/usuarios?pagina=0&tamanho=2")
                .then().statusCode(200)
                .body("pagina", equalTo(0))
                .body("tamanho", equalTo(2))
                .body("conteudo.size()", equalTo(2))
                .body("senhaHash", nullValue());
    }
}
