package br.etec.pedemeia.comum.erro;

import java.util.List;

/**
 * Erro de negócio ou de validação com código do contrato.
 * Herda de RuntimeException, então a transação sofre rollback (ADR-004).
 */
public class NegocioException extends RuntimeException {

    private final CodigoErro codigo;
    private final List<CampoErro> campos;

    public NegocioException(CodigoErro codigo, String mensagem) {
        this(codigo, mensagem, List.of());
    }

    public NegocioException(CodigoErro codigo, String mensagem, List<CampoErro> campos) {
        super(mensagem);
        this.codigo = codigo;
        this.campos = List.copyOf(campos);
    }

    /** Erro VALIDACAO_INVALIDA (400) apontando um campo. */
    public static NegocioException campo(String campo, String mensagem) {
        return new NegocioException(CodigoErro.VALIDACAO_INVALIDA, "Dados inválidos.",
                List.of(new CampoErro(campo, mensagem)));
    }

    public CodigoErro codigo() {
        return codigo;
    }

    public List<CampoErro> campos() {
        return campos;
    }
}
