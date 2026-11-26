package br.etec.pedemeia.comum.paginacao;

import java.util.List;

/** Resposta paginada do contrato: conteudo, pagina, tamanho, totalElementos, totalPaginas. */
public record Pagina<T>(List<T> conteudo, int pagina, int tamanho, long totalElementos, int totalPaginas) {

    public static <T> Pagina<T> de(List<T> conteudo, PaginaRequisicao requisicao, long totalElementos) {
        int totalPaginas = (int) Math.ceil((double) totalElementos / requisicao.tamanho());
        return new Pagina<>(conteudo, requisicao.pagina(), requisicao.tamanho(), totalElementos, totalPaginas);
    }
}
