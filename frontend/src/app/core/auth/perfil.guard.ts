import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Perfil } from './perfil';

/**
 * Lê `data: { perfis: [...] }` da rota. Sem perfil na lista, redireciona para /nao-autorizado.
 * Rota sem `perfis` vale para qualquer usuário autenticado.
 */
export const perfilGuard: CanActivateFn = (rota: ActivatedRouteSnapshot) => {
  const perfisPermitidos = (rota.data['perfis'] as Perfil[] | undefined) ?? [];
  if (perfisPermitidos.length === 0) {
    return true;
  }
  const auth = inject(AuthService);
  if (auth.temPerfil(perfisPermitidos)) {
    return true;
  }
  return inject(Router).createUrlTree(['/nao-autorizado']);
};
