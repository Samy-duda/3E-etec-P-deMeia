import { HttpErrorResponse } from '@angular/common/http';
import { CODIGOS_ERRO, CampoErro, CodigoErro, ErroResposta } from '../../shared/modelos/erro-api';

/** Único lugar com texto de erro. A tela decide a mensagem pelo `codigo`, nunca pelo texto da API. */
export const MENSAGENS_ERRO: Record<CodigoErro, string> = {
  VALIDACAO_INVALIDA: 'Verifique os campos destacados e tente novamente.',
  CREDENCIAIS_INVALIDAS: 'E-mail ou senha inválidos.',
  NAO_AUTENTICADO: 'Sua sessão expirou. Entre novamente.',
  ACESSO_NEGADO: 'Seu perfil não tem permissão para esta operação. Nenhum dado foi alterado.',
  RECURSO_NAO_ENCONTRADO: 'O registro solicitado não foi encontrado.',
  ALUNO_DUPLICADO: 'Já existe um aluno com esta matrícula escolar. Nenhum cadastro foi feito.',
  EMAIL_DUPLICADO: 'Já existe uma conta com este e-mail.',
  MATRICULA_DUPLICADA: 'Este aluno já está matriculado neste período letivo.',
  NOTA_DUPLICADA: 'Este aluno já tem nota nesta prova. A nota existente foi mantida.',
  PROCESSO_JA_EXISTE_PARA_PROVA: 'Esta prova já tem um processo seletivo.',
  CLASSIFICACAO_JA_GERADA: 'A classificação deste processo já foi gerada. Notas não podem mais ser alteradas.',
  BOLSA_JA_EXISTE: 'Este aluno já tem bolsa neste processo.',
  BOLSAS_JA_CONCEDIDAS: 'Todas as bolsas dos selecionados já foram concedidas.',
  NOTA_FORA_DO_INTERVALO: 'A nota deve estar entre 0 e a nota máxima da prova.',
  ALUNO_NAO_MATRICULADO_NO_PERIODO: 'Este aluno não está matriculado no período da prova.',
  PROVA_NAO_PERTENCE_AO_PERIODO: 'A prova não pertence ao período letivo informado.',
  SEM_NOTAS_PARA_CLASSIFICACAO: 'Não há notas nesta prova. Lance as notas antes de gerar a classificação.',
  CLASSIFICACAO_NAO_GERADA: 'A classificação ainda não foi gerada para este processo.',
  ALUNO_NAO_CLASSIFICADO: 'Este aluno não aparece na classificação do processo.',
  ALUNO_NAO_SELECIONADO: 'Este aluno não foi selecionado, por isso não recebe bolsa.',
  VALOR_BOLSA_INVALIDO: 'O valor da bolsa deve ser R$ 300,00.',
  ERRO_INTERNO: 'Ocorreu um erro inesperado no servidor. Tente novamente.',
};

export const MENSAGEM_SEM_CONEXAO =
  'Não foi possível conectar ao servidor. Verifique a conexão e tente novamente.';

export const MENSAGEM_ERRO_DESCONHECIDO = 'Ocorreu um erro inesperado. Tente novamente.';

/** Erro pronto para exibir: mensagem em português, código do contrato e campos (só em validação). */
export interface ErroExibicao {
  codigo?: CodigoErro;
  mensagem: string;
  campos: CampoErro[];
}

/**
 * Converte qualquer erro de HttpClient no ErroExibicao.
 * Usa o formato único ErroResposta quando a API o devolve; caso contrário, uma mensagem genérica.
 */
export function erroDaApi(erro: unknown): ErroExibicao {
  if (!(erro instanceof HttpErrorResponse)) {
    return { mensagem: MENSAGEM_ERRO_DESCONHECIDO, campos: [] };
  }
  if (erro.status === 0) {
    return { mensagem: MENSAGEM_SEM_CONEXAO, campos: [] };
  }
  const corpo = erro.error as Partial<ErroResposta> | null;
  if (corpo && typeof corpo.codigo === 'string' && (CODIGOS_ERRO as readonly string[]).includes(corpo.codigo)) {
    const codigo = corpo.codigo as CodigoErro;
    return {
      codigo,
      mensagem: MENSAGENS_ERRO[codigo],
      campos: Array.isArray(corpo.campos) ? corpo.campos : [],
    };
  }
  return { mensagem: MENSAGEM_ERRO_DESCONHECIDO, campos: [] };
}
