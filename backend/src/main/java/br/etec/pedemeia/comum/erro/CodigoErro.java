package br.etec.pedemeia.comum.erro;

/**
 * Códigos de erro do contrato (04-contrato-api.yaml, CodigoErro).
 * Cada código carrega o status HTTP da classificação de 02-arquitetura.md, seção 7.
 */
public enum CodigoErro {

    VALIDACAO_INVALIDA(400),
    CREDENCIAIS_INVALIDAS(401),
    NAO_AUTENTICADO(401),
    ACESSO_NEGADO(403),
    RECURSO_NAO_ENCONTRADO(404),
    ALUNO_DUPLICADO(409),
    EMAIL_DUPLICADO(409),
    MATRICULA_DUPLICADA(409),
    NOTA_DUPLICADA(409),
    PROCESSO_JA_EXISTE_PARA_PROVA(409),
    CLASSIFICACAO_JA_GERADA(409),
    BOLSA_JA_EXISTE(409),
    BOLSAS_JA_CONCEDIDAS(409),
    NOTA_FORA_DO_INTERVALO(422),
    ALUNO_NAO_MATRICULADO_NO_PERIODO(422),
    PROVA_NAO_PERTENCE_AO_PERIODO(422),
    SEM_NOTAS_PARA_CLASSIFICACAO(422),
    CLASSIFICACAO_NAO_GERADA(422),
    ALUNO_NAO_CLASSIFICADO(422),
    ALUNO_NAO_SELECIONADO(422),
    VALOR_BOLSA_INVALIDO(422),
    ERRO_INTERNO(500);

    private final int status;

    CodigoErro(int status) {
        this.status = status;
    }

    public int status() {
        return status;
    }
}
