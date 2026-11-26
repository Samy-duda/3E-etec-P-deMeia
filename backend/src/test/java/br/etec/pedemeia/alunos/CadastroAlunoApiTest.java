package br.etec.pedemeia.alunos;

import br.etec.pedemeia.teste.Api;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/** MVP-RF01, MVP-RF02, MVP-CA01 a MVP-CA04. */
@QuarkusTest
class CadastroAlunoApiTest {

    @Test
    void mvpCa01_cadastroDeAlunoValidoAparecenaConsulta() {
        String matricula = "2026-" + Api.sufixo();
        Api.como(Api.SECRETARIA)
                .body(Map.of("nome", "Ana Souza", "matriculaEscolar", matricula, "dataNascimento", "2008-03-15"))
                .post("/alunos")
                .then().statusCode(201)
                .header("Location", notNullValue())
                .body("nome", equalTo("Ana Souza"))
                .body("matriculaEscolar", equalTo(matricula))
                .body("dataNascimento", equalTo("2008-03-15"))
                .body("ativo", equalTo(true))
                .body("criadoEm", notNullValue());
        Api.como(Api.SECRETARIA).get("/alunos?matriculaEscolar=" + matricula)
                .then().statusCode(200)
                .body("totalElementos", equalTo(1))
                .body("conteudo[0].nome", equalTo("Ana Souza"));
    }

    @Test
    void mvpCa02_consultaPorMatriculaRetornaDadosDoAluno() {
        String matricula = "M" + Api.sufixo();
        long id = Api.criarAluno("Ana Souza", matricula, "2008-03-15");
        Api.como(Api.SECRETARIA).get("/alunos?matriculaEscolar=" + matricula)
                .then().statusCode(200)
                .body("conteudo[0].id", equalTo((int) id))
                .body("conteudo[0].nome", equalTo("Ana Souza"));
        Api.como(Api.SECRETARIA).get("/alunos/" + id)
                .then().statusCode(200).body("matriculaEscolar", equalTo(matricula));
    }

    @Test
    void mvpCa02_buscaPorNomeContemSemDiferenciarCaixa() {
        String nome = "Beatriz Nome" + Api.sufixo();
        Api.criarAluno(nome, "M" + Api.sufixo(), "2008-03-15");
        Api.como(Api.SECRETARIA).get("/alunos?nome=" + nome.substring(0, 12).toUpperCase())
                .then().statusCode(200)
                .body("conteudo.nome", hasItem(nome));
    }

    @Test
    void mvpCa03_matriculaEscolarDuplicadaEhRecusadaSemAlterarOriginal() {
        String matricula = "DUP-" + Api.sufixo();
        long original = Api.criarAluno("Ana Souza", matricula, "2008-03-15");
        Api.como(Api.SECRETARIA)
                .body(Map.of("nome", "Ana S. Lima", "matriculaEscolar", matricula, "dataNascimento", "2007-01-01"))
                .post("/alunos")
                .then().statusCode(409)
                .body("codigo", equalTo("ALUNO_DUPLICADO"))
                .body("status", equalTo(409));
        Api.como(Api.SECRETARIA).get("/alunos/" + original)
                .then().statusCode(200)
                .body("nome", equalTo("Ana Souza"))
                .body("dataNascimento", equalTo("2008-03-15"));
    }

    @Test
    void mvpCa04_dataNascimentoAusenteEhRecusadaComCampoIndicado() {
        String matricula = "SEMDATA-" + Api.sufixo();
        Api.como(Api.SECRETARIA)
                .body(Map.of("nome", "Ana Souza", "matriculaEscolar", matricula))
                .post("/alunos")
                .then().statusCode(400)
                .body("codigo", equalTo("VALIDACAO_INVALIDA"))
                .body("campos[0].campo", equalTo("dataNascimento"));
        Api.como(Api.SECRETARIA).get("/alunos?matriculaEscolar=" + matricula)
                .then().statusCode(200).body("totalElementos", equalTo(0));
    }

    @Test
    void erroTemFormatoUnicoComCaminhoETimestamp() {
        Api.como(Api.SECRETARIA).get("/alunos/999999999")
                .then().statusCode(404)
                .body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"))
                .body("timestamp", notNullValue())
                .body("mensagem", notNullValue())
                .body("caminho", equalTo("/api/alunos/999999999"))
                .body("campos", equalTo(java.util.List.of()));
    }

    @Test
    void paginacaoInvalidaEhRecusada() {
        Api.como(Api.SECRETARIA).get("/alunos?tamanho=500")
                .then().statusCode(400).body("codigo", equalTo("VALIDACAO_INVALIDA"))
                .body("campos[0].campo", equalTo("tamanho"));
    }

    @Test
    void contatosSaoOpcionais() {
        Api.como(Api.SECRETARIA)
                .body(Map.of("nome", "Caio Ramos", "matriculaEscolar", "C" + Api.sufixo(), "dataNascimento", "2008-02-02"))
                .post("/alunos")
                .then().statusCode(201).body("email", nullValue()).body("telefone", nullValue());
    }
}
