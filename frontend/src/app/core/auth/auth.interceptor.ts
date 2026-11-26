import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { API_BASE } from '../api/api-base';
import { SessaoService } from './sessao.service';

/** Adiciona `Authorization: Bearer <token>` nas chamadas à API, quando há sessão. */
export const authInterceptor: HttpInterceptorFn = (requisicao, proximo) => {
  if (!requisicao.url.startsWith(API_BASE)) {
    return proximo(requisicao);
  }
  const sessao = inject(SessaoService).ler();
  if (!sessao) {
    return proximo(requisicao);
  }
  return proximo(
    requisicao.clone({ setHeaders: { Authorization: `Bearer ${sessao.token}` } }),
  );
};
