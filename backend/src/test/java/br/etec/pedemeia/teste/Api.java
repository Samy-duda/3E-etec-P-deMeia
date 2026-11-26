package br.etec.pedemeia.teste;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.restassured.RestAssured.given;

/**
 * Auxiliar dos testes de API. Usa só as contas de teste test@IA_* (D18).
 * Dados de cada teste recebem sufixo único, então os testes não dependem de ordem.
 */
public final class Api {

    public static final String ADMIN = "test@IA_admin";
    public static final String SECRETARIA = "test@IA_secretaria";
    public static final String CONSULTA = "test@IA_consulta";

    private static final String SENHA = "TesteIA_2026";
    private static final Map<String, String> TOKENS = new ConcurrentHashMap<>();

    private Api() {
    }

    public static String token(String email) {
        return TOKENS.computeIfAbsent(email, e -> given().contentType(ContentType.JSON)
                .body(Map.of("email", e, "senha", SENHA))
                .post("/auth/login")
                .then().statusCode(200).extract().path("token"));
    }

    /** Requisição autenticada como o perfil informado. */
    public static RequestSpecification como(String email) {
        return given().contentType(ContentType.JSON).header("Authorization", "Bearer " + token(email));
    }

    public static RequestSpecification anonimo() {
        return given().contentType(ContentType.JSON);
    }

    public static String sufixo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static long criarAluno(String nome, String matriculaEscolar, String dataNascimento) {
        return como(SECRETARIA).body(Map.of("nome", nome, "matriculaEscolar", matriculaEscolar,
                        "dataNascimento", dataNascimento))
                .post("/alunos").then().statusCode(201).extract().jsonPath().getLong("id");
    }

    public static long criarPeriodo(String nome) {
        return como(SECRETARIA).body(Map.of("nome", nome, "dataInicio", "2026-02-01", "dataFim", "2026-12-15"))
                .post("/periodos-letivos").then().statusCode(201).extract().jsonPath().getLong("id");
    }

    public static long matricular(long periodoId, long alunoId, int anoEscolar) {
        return como(SECRETARIA).body(Map.of("alunoId", alunoId, "anoEscolar", anoEscolar))
                .post("/periodos-letivos/" + periodoId + "/matriculas")
                .then().statusCode(201).extract().jsonPath().getLong("id");
    }

    public static long criarProva(long periodoId, String titulo, String notaMaxima) {
        return como(SECRETARIA).body(Map.of("periodoLetivoId", periodoId, "titulo", titulo,
                        "dataAplicacao", "2026-11-20", "notaMaxima", new java.math.BigDecimal(notaMaxima)))
                .post("/provas").then().statusCode(201).extract().jsonPath().getLong("id");
    }

    public static Response lancarNota(long provaId, long matriculaId, String nota) {
        return como(SECRETARIA).body(Map.of("matriculaId", matriculaId, "nota", new java.math.BigDecimal(nota)))
                .post("/provas/" + provaId + "/notas");
    }

    public static long criarProcesso(long periodoId, long provaId) {
        return como(SECRETARIA).body(Map.of("periodoLetivoId", periodoId, "provaId", provaId))
                .post("/processos-seletivos").then().statusCode(201).extract().jsonPath().getLong("id");
    }

    public static Response gerarClassificacao(long processoId) {
        return como(SECRETARIA).post("/processos-seletivos/" + processoId + "/classificacao");
    }

    /** Aluno já matriculado em um período, com matrícula escolar única. */
    public record Matriculado(long alunoId, long matriculaId, String matriculaEscolar, String nome) {
    }

    public static Matriculado matriculado(long periodoId, String nome, String dataNascimento, int anoEscolar) {
        String matricula = "M" + sufixo();
        long alunoId = criarAluno(nome, matricula, dataNascimento);
        long matriculaId = matricular(periodoId, alunoId, anoEscolar);
        return new Matriculado(alunoId, matriculaId, matricula, nome);
    }

    /** Id do usuário autenticado (claim sub), via GET /auth/me. */
    public static long idDo(String email) {
        return como(email).get("/auth/me").then().statusCode(200).extract().jsonPath().getLong("id");
    }

    public static Response classificacao(long processoId) {
        return como(CONSULTA).get("/processos-seletivos/" + processoId + "/classificacao");
    }

    public static Response bolsas(long processoId) {
        return como(CONSULTA).get("/processos-seletivos/" + processoId + "/bolsas");
    }
}
