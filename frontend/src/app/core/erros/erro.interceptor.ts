import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../auth/auth.service';

/**
 * 401 fora do login: encerra a sessão local e manda para /login.
 * Demais erros seguem para o componente, que exibe o ErroResposta (mensagens-erro.ts).
 * 403 não redireciona: a tela mostra a mensagem e nenhum dado muda.
 */
export const erroInterceptor: HttpInterceptorFn = (requisicao, proximo) => {
  // inject() só funciona no contexto de criação do interceptor; o catchError roda depois, de forma assíncrona.
  const auth = inject(AuthService);
  const router = inject(Router);
  return proximo(requisicao).pipe(
    catchError((erro: unknown) => {
      const sessaoExpirada =
        erro instanceof HttpErrorResponse &&
        erro.status === 401 &&
        !requisicao.url.includes('/auth/login');
      if (sessaoExpirada) {
        auth.encerrarSessao();
        router.navigate(['/login'], { queryParams: { motivo: 'sessao-expirada' } });
      }
      return throwError(() => erro);
    }),
  );
};
