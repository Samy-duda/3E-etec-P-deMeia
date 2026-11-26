export interface NotaRequisicao {
  /** Matrícula do aluno (não o id do aluno). */
  matriculaId: number;
  nota: number;
}

export interface NotaCorrecaoRequisicao {
  nota: number;
}

export interface NotaResposta {
  id: number;
  provaId: number;
  matriculaId: number;
  nomeAluno: string;
  matriculaEscolar: string;
  nota: number;
  lancadaPorId: number;
  lancadaEm: string;
  alteradaPorId?: number | null;
  alteradaEm?: string | null;
}
