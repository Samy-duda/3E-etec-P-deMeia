export interface MatriculaRequisicao {
  alunoId: number;
  /** Ano escolar 1, 2 ou 3 (P05). */
  anoEscolar: number;
  turma?: string | null;
}

export interface MatriculaResposta {
  id: number;
  alunoId: number;
  nomeAluno: string;
  matriculaEscolar: string;
  periodoLetivoId: number;
  anoEscolar: number;
  turma?: string | null;
  status: 'ATIVA';
}
