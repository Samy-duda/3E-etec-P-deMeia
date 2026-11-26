/** Resposta paginada do contrato (PaginaBase). Paginação base zero. */
export interface Pagina<T> {
  conteudo: T[];
  pagina: number;
  tamanho: number;
  totalElementos: number;
  totalPaginas: number;
}

/** Parâmetros de paginação enviados à API (`pagina` base zero, `tamanho` de 1 a 100). */
export interface ParametrosPagina {
  pagina?: number;
  tamanho?: number;
}
