import { HttpErrorResponse } from '@angular/common/http';
import { Perfil } from '../core/auth/perfil';
import { SessaoArmazenada } from '../core/auth/sessao.service';
import { Pagina } from '../shared/modelos/pagina';
import { CampoErro, CodigoErro, ErroResposta } from '../shared/modelos/erro-api';

/** Chave usada por SessaoService em sessionStorage. */
export const CHAVE_SESSAO_TESTE = 'pedemeia.sessao';

/** Data de expiração bem à frente, para o token ser considerado válido nos testes. */
export const EXPIRA_EM_FUTURO = '2099-01-01T00:00:00Z';

/** Sessão fictícia de um perfil. Usuários de teste seguem o padrão test@IA_<finalidade> (D18). */
export function sessaoDePerfil(perfil: Perfil): SessaoArmazenada {
  return {
    token: `token-${perfil.toLowerCase()}`,
    expiraEm: EXPIRA_EM_FUTURO,
    usuario: {
      id: 1,
      nome: `Conta ${perfil}`,
      email: `test@IA_${perfil.toLowerCase()}`,
      perfil,
    },
  };
}

/** Corpo do formato único de erro (ADR-002). */
export function corpoErro(codigo: CodigoErro, campos: CampoErro[] = []): ErroResposta {
  return {
    timestamp: '2026-10-16T12:00:00Z',
    status: 422,
    codigo,
    mensagem: 'mensagem do servidor, ignorada pela tela',
    caminho: '/api/teste',
    campos,
  };
}

/** Erro HTTP com o corpo de ErroResposta, como o HttpClient entrega à aplicação. */
export function erroHttp(status: number, codigo: CodigoErro, campos: CampoErro[] = []): HttpErrorResponse {
  return new HttpErrorResponse({ status, error: corpoErro(codigo, campos), url: '/api/teste' });
}

export function paginaDe<T>(conteudo: T[], pagina = 0, totalPaginas = 1): Pagina<T> {
  return {
    conteudo,
    pagina,
    tamanho: 20,
    totalElementos: conteudo.length,
    totalPaginas,
  };
}
