export interface ProcessoSeletivoRequisicao {
  periodoLetivoId: number;
  provaId: number;
}

export interface ProcessoSeletivoResposta {
  id: number;
  periodoLetivoId: number;
  provaId: number;
  /** Único estado formal no MVP (Q12). */
  status: 'CRIADO';
  criadoPorId?: number | null;
}
