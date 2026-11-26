export interface AlunoRequisicao {
  nome: string;
  matriculaEscolar: string;
  /** Formato yyyy-MM-dd. */
  dataNascimento: string;
  email?: string | null;
  telefone?: string | null;
}

/** Aluno. Só ADMIN e SECRETARIA acessam /alunos (CONSULTA não recebe dados de aluno). */
export interface AlunoResposta {
  id: number;
  nome: string;
  matriculaEscolar: string;
  dataNascimento: string;
  email?: string | null;
  telefone?: string | null;
  ativo: boolean;
  criadoEm: string;
}

export interface FiltroAlunos {
  matriculaEscolar?: string;
  nome?: string;
}
