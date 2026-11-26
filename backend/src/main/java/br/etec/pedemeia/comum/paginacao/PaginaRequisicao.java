package br.etec.pedemeia.comum.paginacao;

import br.etec.pedemeia.comum.erro.CampoErro;
import br.etec.pedemeia.comum.erro.CodigoErro;
import br.etec.pedemeia.comum.erro.NegocioException;

import java.util.ArrayList;
import java.util.List;

/** Parâmetros pagina (base zero, padrão 0) e tamanho (1 a 100, padrão 20) do contrato. */
public record PaginaRequisicao(int pagina, int tamanho) {

    public static final int TAMANHO_PADRAO = 20;
    public static final int TAMANHO_MAXIMO = 100;

    public static PaginaRequisicao de(Integer pagina, Integer tamanho) {
        int p = pagina == null ? 0 : pagina;
        int t = tamanho == null ? TAMANHO_PADRAO : tamanho;
        List<CampoErro> erros = new ArrayList<>();
        if (p < 0) {
            erros.add(new CampoErro("pagina", "deve ser maior ou igual a zero"));
        }
        if (t < 1 || t > TAMANHO_MAXIMO) {
            erros.add(new CampoErro("tamanho", "deve estar entre 1 e " + TAMANHO_MAXIMO));
        }
        if (!erros.isEmpty()) {
            throw new NegocioException(CodigoErro.VALIDACAO_INVALIDA, "Parâmetros de paginação inválidos.", erros);
        }
        return new PaginaRequisicao(p, t);
    }
}
