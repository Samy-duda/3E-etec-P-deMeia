export interface ClassificacaoItem {
  matriculaId: number;
  anoEscolar: number;
  posicao: number;
  notaFinal: number;
  selecionado: boolean;
  nomeAluno: string;
  /** Presente somente para ADMIN e SECRETARIA. Ausente para CONSULTA. */
  matriculaEscolar?: string | null;
}

export interface ClassificacaoResposta {
  processoSeletivoId: number;
  /** Nulo se a classificação ainda não foi gerada. */
  calculadaEm?: string | null;
  vagasPorAno: number;
  itens: ClassificacaoItem[];
}
