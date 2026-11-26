import { ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, provideRouter } from '@angular/router';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { CHAVE_SESSAO_TESTE, sessaoDePerfil } from '../../testing/fabricas';
import { authGuard } from './auth.guard';
import { perfilGuard } from './perfil.guard';
import { PERFIS_OPERADORES, PERFIS_ADMINISTRADOR } from './perfil';
import { SessaoArmazenada } from './sessao.service';

function configurarComSessao(sessao: SessaoArmazenada | null): void {
  sessionStorage.clear();
  if (sessao) {
    sessionStorage.setItem(CHAVE_SESSAO_TESTE, JSON.stringify(sessao));
  }
  TestBed.configureTestingModule({ providers: [provideRouter([]), provideHttpClient()] });
}

function rota(perfis?: string[]): ActivatedRouteSnapshot {
  return { data: perfis ? { perfis } : {} } as unknown as ActivatedRouteSnapshot;
}

function estado(url: string): RouterStateSnapshot {
  return { url } as RouterStateSnapshot;
}

function executar<T>(funcao: () => T): T {
  return TestBed.runInInjectionContext(funcao);
}

describe('authGuard', () => {
  afterEach(() => sessionStorage.clear());

  it('permite a rota quando há sessão', () => {
    configurarComSessao(sessaoDePerfil('ADMIN'));

    const resultado = executar(() => authGuard(rota(), estado('/alunos')));

    expect(resultado).toBe(true);
  });

  it('redireciona para /login guardando a rota de retorno quando não há sessão', () => {
    configurarComSessao(null);

    const resultado = executar(() => authGuard(rota(), estado('/alunos')));

    expect(resultado).toBeInstanceOf(UrlTree);
    const arvore = resultado as UrlTree;
    expect(arvore.toString()).toContain('/login');
    expect(arvore.queryParams['retorno']).toBe('/alunos');
  });
});

describe('perfilGuard', () => {
  afterEach(() => sessionStorage.clear());

  it('permite ADMIN em rota de operadores', () => {
    configurarComSessao(sessaoDePerfil('ADMIN'));

    const resultado = executar(() => perfilGuard(rota([...PERFIS_OPERADORES]), estado('/alunos')));

    expect(resultado).toBe(true);
  });

  it('permite SECRETARIA em rota de operadores', () => {
    configurarComSessao(sessaoDePerfil('SECRETARIA'));

    const resultado = executar(() => perfilGuard(rota([...PERFIS_OPERADORES]), estado('/alunos')));

    expect(resultado).toBe(true);
  });

  it('nega CONSULTA em rota de operadores e redireciona para /nao-autorizado', () => {
    configurarComSessao(sessaoDePerfil('CONSULTA'));

    const resultado = executar(() => perfilGuard(rota([...PERFIS_OPERADORES]), estado('/alunos')));

    expect(resultado).toBeInstanceOf(UrlTree);
    expect((resultado as UrlTree).toString()).toContain('/nao-autorizado');
  });

  it('nega SECRETARIA em rota exclusiva de ADMIN (usuários)', () => {
    configurarComSessao(sessaoDePerfil('SECRETARIA'));

    const resultado = executar(() => perfilGuard(rota([...PERFIS_ADMINISTRADOR]), estado('/usuarios')));

    expect((resultado as UrlTree).toString()).toContain('/nao-autorizado');
  });

  it('permite qualquer perfil autenticado quando a rota não lista perfis', () => {
    configurarComSessao(sessaoDePerfil('CONSULTA'));

    const resultado = executar(() => perfilGuard(rota(), estado('/nao-autorizado')));

    expect(resultado).toBe(true);
  });
});
