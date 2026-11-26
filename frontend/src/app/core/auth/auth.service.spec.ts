import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { CHAVE_SESSAO_TESTE, EXPIRA_EM_FUTURO, sessaoDePerfil } from '../../testing/fabricas';
import { SessaoArmazenada } from './sessao.service';
import { AuthService } from './auth.service';

/** Monta o AuthService com (ou sem) sessão já guardada, antes da criação do serviço. */
function configurar(sessao: SessaoArmazenada | null = null) {
  sessionStorage.clear();
  if (sessao) {
    sessionStorage.setItem(CHAVE_SESSAO_TESTE, JSON.stringify(sessao));
  }
  TestBed.configureTestingModule({
    providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
  });
  return {
    servico: TestBed.inject(AuthService),
    http: TestBed.inject(HttpTestingController),
    router: TestBed.inject(Router),
  };
}

function respostaDeLogin(perfil: 'ADMIN' | 'SECRETARIA' | 'CONSULTA') {
  return {
    token: `jwt-${perfil.toLowerCase()}`,
    tipo: 'Bearer' as const,
    expiraEm: EXPIRA_EM_FUTURO,
    usuario: sessaoDePerfil(perfil).usuario,
  };
}

describe('AuthService', () => {
  afterEach(() => {
    sessionStorage.clear();
  });

  it('começa deslogado quando não há sessão guardada', () => {
    const { servico, http } = configurar();

    expect(servico.logado()).toBe(false);
    expect(servico.perfil()).toBeNull();
    http.verify();
  });

  it('login envia e-mail aparado e senha ao endpoint do contrato', () => {
    const { servico, http } = configurar();

    servico.login('  test@IA_admin ', 'senha-de-teste').subscribe();

    const requisicao = http.expectOne('/api/auth/login');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual({ email: 'test@IA_admin', senha: 'senha-de-teste' });
    requisicao.flush(respostaDeLogin('ADMIN'));
    http.verify();
  });

  it('após o login, guarda a sessão e expõe usuário e perfil em signals', () => {
    const { servico, http } = configurar();
    let nomeRecebido: string | undefined;
    servico.login('test@IA_admin', 'senha-de-teste').subscribe((usuario) => (nomeRecebido = usuario.nome));

    http.expectOne('/api/auth/login').flush(respostaDeLogin('ADMIN'));

    expect(nomeRecebido).toBe('Conta ADMIN');
    expect(servico.logado()).toBe(true);
    expect(servico.perfil()).toBe('ADMIN');
    expect(servico.token()).toBe('jwt-admin');
    expect(sessionStorage.getItem(CHAVE_SESSAO_TESTE)).toContain('jwt-admin');
    http.verify();
  });

  it('restaura a sessão guardada ao criar o serviço', () => {
    const { servico } = configurar(sessaoDePerfil('CONSULTA'));

    expect(servico.logado()).toBe(true);
    expect(servico.perfil()).toBe('CONSULTA');
  });

  it('temPerfil confere o perfil atual com a lista pedida', () => {
    const { servico } = configurar(sessaoDePerfil('CONSULTA'));

    expect(servico.temPerfil(['CONSULTA'])).toBe(true);
    expect(servico.temPerfil(['ADMIN', 'SECRETARIA'])).toBe(false);
  });

  it('encerrarSessao limpa a sessão local sem navegar', () => {
    const { servico, router } = configurar(sessaoDePerfil('ADMIN'));
    const navegar = vi.spyOn(router, 'navigateByUrl');

    servico.encerrarSessao();

    expect(servico.logado()).toBe(false);
    expect(sessionStorage.getItem(CHAVE_SESSAO_TESTE)).toBeNull();
    expect(navegar).not.toHaveBeenCalled();
  });

  it('logout encerra a sessão e leva para /login', () => {
    const { servico, router } = configurar(sessaoDePerfil('SECRETARIA'));
    const navegar = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);

    servico.logout();

    expect(servico.logado()).toBe(false);
    expect(navegar).toHaveBeenCalledWith('/login');
  });
});
