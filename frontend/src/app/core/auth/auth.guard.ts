import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/** Exige sessão. Sem sessão, redireciona para /login guardando a rota de retorno. */
export const authGuard: CanActivateFn = (_rota, estado) => {
  const auth = inject(AuthService);
  if (auth.logado()) {
    return true;
  }
  return inject(Router).createUrlTree(['/login'], { queryParams: { retorno: estado.url } });
};
