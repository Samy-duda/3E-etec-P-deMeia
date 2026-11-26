/** Valores de CodigoErro do contrato (04-contrato-api.yaml). */
export const CODIGOS_ERRO = [
  'VALIDACAO_INVALIDA',
  'CREDENCIAIS_INVALIDAS',
  'NAO_AUTENTICADO',
  'ACESSO_NEGADO',
  'RECURSO_NAO_ENCONTRADO',
  'ALUNO_DUPLICADO',
  'EMAIL_DUPLICADO',
  'MATRICULA_DUPLICADA',
  'NOTA_DUPLICADA',
  'PROCESSO_JA_EXISTE_PARA_PROVA',
  'CLASSIFICACAO_JA_GERADA',
  'BOLSA_JA_EXISTE',
  'BOLSAS_JA_CONCEDIDAS',
  'NOTA_FORA_DO_INTERVALO',
  'ALUNO_NAO_MATRICULADO_NO_PERIODO',
  'PROVA_NAO_PERTENCE_AO_PERIODO',
  'SEM_NOTAS_PARA_CLASSIFICACAO',
  'CLASSIFICACAO_NAO_GERADA',
  'ALUNO_NAO_CLASSIFICADO',
  'ALUNO_NAO_SELECIONADO',
  'VALOR_BOLSA_INVALIDO',
  'ERRO_INTERNO',
] as const;

export type CodigoErro = (typeof CODIGOS_ERRO)[number];

/** Campo com erro, presente somente em VALIDACAO_INVALIDA (400). */
export interface CampoErro {
  campo: string;
  mensagem: string;
}

/** Formato único de erro da API (ErroResposta). */
export interface ErroResposta {
  timestamp: string;
  status: number;
  codigo: CodigoErro;
  mensagem: string;
  caminho: string;
  campos: CampoErro[];
}
