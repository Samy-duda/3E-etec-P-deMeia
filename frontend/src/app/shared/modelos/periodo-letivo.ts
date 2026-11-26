export interface PeriodoLetivoRequisicao {
  nome: string;
  /** Formato yyyy-MM-dd. */
  dataInicio: string;
  /** Formato yyyy-MM-dd. Deve ser igual ou posterior a dataInicio (validado pela API). */
  dataFim: string;
}

export interface PeriodoLetivoResposta {
  id: number;
  nome: string;
  dataInicio: string;
  dataFim: string;
  ativo: boolean;
}
