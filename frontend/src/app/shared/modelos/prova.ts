export interface ProvaRequisicao {
  periodoLetivoId: number;
  titulo: string;
  descricao?: string | null;
  /** Formato yyyy-MM-dd. */
  dataAplicacao: string;
  /** Maior que zero, com duas casas decimais (P04). */
  notaMaxima: number;
}

export interface ProvaResposta {
  id: number;
  periodoLetivoId: number;
  titulo: string;
  descricao?: string | null;
  dataAplicacao: string;
  notaMaxima: number;
  criadaPorId?: number | null;
  criadaEm: string;
}
