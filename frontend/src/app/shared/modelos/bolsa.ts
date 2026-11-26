export interface ConcessaoBolsaRequisicao {
  /** Se informado, concede somente a esta matrícula. Sem ele, concessão em lote. */
  matriculaId?: number | null;
  /** Se informado, deve ser igual ao valor de configuração (validado pela API). */
  valorMensal?: number | null;
}

/** Bolsa. Campos marcados como ADMIN/SECRETARIA não são enviados para CONSULTA. */
export interface BolsaResposta {
  id: number;
  processoSeletivoId: number;
  matriculaId: number;
  anoEscolar: number;
  posicao: number;
  nomeAluno: string;
  matriculaEscolar?: string | null;
  valorMensal: number;
  /** No MVP só ATIVA é gravada. */
  situacao: 'ATIVA' | 'CANCELADA';
  /** Formato yyyy-MM-dd. */
  dataInicio: string;
  dataFim?: string | null;
  criadaPorId?: number | null;
  criadaEm?: string | null;
}
