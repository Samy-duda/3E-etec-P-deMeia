import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { perfilGuard } from './core/auth/perfil.guard';
import { PERFIS_ADMINISTRADOR, PERFIS_OPERADORES, PERFIS_TODOS } from './core/auth/perfil';

/** Rotas da seção 5 de docs/agentes/05-estrutura-pastas.md. Todas, exceto /login, exigem sessão e perfil. */
export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'processos-seletivos',
  },
  {
    path: 'alunos',
    canActivate: [authGuard, perfilGuard],
    data: { perfis: PERFIS_OPERADORES },
    loadComponent: () => import('./features/alunos/alunos-lista.component').then((m) => m.AlunosListaComponent),
  },
  {
    path: 'alunos/novo',
    canActivate: [authGuard, perfilGuard],
    data: { perfis: PERFIS_OPERADORES },
    loadComponent: () => import('./features/alunos/aluno-form.component').then((m) => m.AlunoFormComponent),
  },
  {
    path: 'periodos-letivos',
    canActivate: [authGuard, perfilGuard],
    data: { perfis: PERFIS_OPERADORES },
    loadComponent: () =>
      import('./features/periodos/periodos-letivos.component').then((m) => m.PeriodosLetivosComponent),
  },
  {
    path: 'periodos-letivos/:periodoId/matriculas',
    canActivate: [authGuard, perfilGuard],
    data: { perfis: PERFIS_OPERADORES },
    loadComponent: () => import('./features/matriculas/matriculas.component').then((m) => m.MatriculasComponent),
  },
  {
    path: 'provas',
    canActivate: [authGuard, perfilGuard],
    data: { perfis: PERFIS_OPERADORES },
    loadComponent: () => import('./features/provas/provas-lista.component').then((m) => m.ProvasListaComponent),
  },
  {
    path: 'provas/nova',
    canActivate: [authGuard, perfilGuard],
    data: { perfis: PERFIS_OPERADORES },
    loadComponent: () => import('./features/provas/prova-form.component').then((m) => m.ProvaFormComponent),
  },
  {
    path: 'provas/:provaId/notas',
    canActivate: [authGuard, perfilGuard],
    data: { perfis: PERFIS_OPERADORES },
    loadComponent: () => import('./features/notas/notas-prova.component').then((m) => m.NotasProvaComponent),
  },
  {
    path: 'processos-seletivos',
    canActivate: [authGuard, perfilGuard],
    data: { perfis: PERFIS_TODOS },
    loadComponent: () =>
      import('./features/processos/processos-lista.component').then((m) => m.ProcessosListaComponent),
  },
  {
    path: 'processos-seletivos/novo',
    canActivate: [authGuard, perfilGuard],
    data: { perfis: PERFIS_OPERADORES },
    loadComponent: () =>
      import('./features/processos/processo-form.component').then((m) => m.ProcessoFormComponent),
  },
  {
    path: 'processos-seletivos/:processoId',
    canActivate: [authGuard, perfilGuard],
    data: { perfis: PERFIS_TODOS },
    loadComponent: () =>
      import('./features/processos/processo-detalhe.component').then((m) => m.ProcessoDetalheComponent),
  },
  {
    path: 'usuarios',
    canActivate: [authGuard, perfilGuard],
    data: { perfis: PERFIS_ADMINISTRADOR },
    loadComponent: () =>
      import('./features/usuarios/usuarios-lista.component').then((m) => m.UsuariosListaComponent),
  },
  {
    path: 'nao-autorizado',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/nao-autorizado/nao-autorizado.component').then((m) => m.NaoAutorizadoComponent),
  },
  {
    path: '**',
    redirectTo: '',
  },
];
