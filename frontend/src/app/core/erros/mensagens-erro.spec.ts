import { HttpErrorResponse } from '@angular/common/http';
import { CODIGOS_ERRO } from '../../shared/modelos/erro-api';
import { erroHttp } from '../../testing/fabricas';
import {
  MENSAGEM_ERRO_DESCONHECIDO,
  MENSAGEM_SEM_CONEXAO,
  MENSAGENS_ERRO,
  erroDaApi,
} from './mensagens-erro';

describe('mensagens de erro', () => {
  it('há mensagem em português para cada código do contrato', () => {
    for (const codigo of CODIGOS_ERRO) {
      expect(MENSAGENS_ERRO[codigo], codigo).toBeTruthy();
    }
  });

  it('traduz o código do ErroResposta em mensagem da tela (ignora o texto da API)', () => {
    const erro = erroDaApi(erroHttp(409, 'ALUNO_DUPLICADO'));

    expect(erro.codigo).toBe('ALUNO_DUPLICADO');
    expect(erro.mensagem).toBe(MENSAGENS_ERRO.ALUNO_DUPLICADO);
    expect(erro.campos).toEqual([]);
  });

  it('repassa os campos apenas em VALIDACAO_INVALIDA', () => {
    const erro = erroDaApi(
      erroHttp(400, 'VALIDACAO_INVALIDA', [{ campo: 'dataNascimento', mensagem: 'é obrigatório' }]),
    );

    expect(erro.codigo).toBe('VALIDACAO_INVALIDA');
    expect(erro.campos).toEqual([{ campo: 'dataNascimento', mensagem: 'é obrigatório' }]);
  });

  it('sem conexão (status 0) mostra mensagem de rede', () => {
    const erro = erroDaApi(new HttpErrorResponse({ status: 0, error: null }));

    expect(erro.mensagem).toBe(MENSAGEM_SEM_CONEXAO);
    expect(erro.codigo).toBeUndefined();
  });

  it('resposta sem formato de erro ou código desconhecido cai na mensagem genérica', () => {
    const semCorpo = erroDaApi(new HttpErrorResponse({ status: 502, error: '<html>' }));
    const codigoEstranho = erroDaApi(new HttpErrorResponse({ status: 418, error: { codigo: 'OUTRO' } }));

    expect(semCorpo.mensagem).toBe(MENSAGEM_ERRO_DESCONHECIDO);
    expect(codigoEstranho.mensagem).toBe(MENSAGEM_ERRO_DESCONHECIDO);
  });

  it('erro que não é HttpErrorResponse cai na mensagem genérica', () => {
    expect(erroDaApi(new Error('falha local')).mensagem).toBe(MENSAGEM_ERRO_DESCONHECIDO);
  });
});
