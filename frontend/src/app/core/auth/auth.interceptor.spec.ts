import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CHAVE_SESSAO_TESTE, sessaoDePerfil } from '../../testing/fabricas';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  let http: HttpClient;
  let controle: HttpTestingController;

  function configurar(sessaoGuardada: boolean): void {
    sessionStorage.clear();
    if (sessaoGuardada) {
      sessionStorage.setItem(CHAVE_SESSAO_TESTE, JSON.stringify(sessaoDePerfil('ADMIN')));
    }
    TestBed.configureTestingModule({
      providers: [provideHttpClient(withInterceptors([authInterceptor])), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpClient);
    controle = TestBed.inject(HttpTestingController);
  }

  afterEach(() => {
    controle.verify();
    sessionStorage.clear();
  });

  it('adiciona o cabeçalho Bearer nas chamadas à API quando há sessão', () => {
    configurar(true);

    http.get('/api/alunos').subscribe();

    const requisicao = controle.expectOne('/api/alunos');
    expect(requisicao.request.headers.get('Authorization')).toBe('Bearer token-admin');
    requisicao.flush([]);
  });

  it('não adiciona cabeçalho quando não há sessão', () => {
    configurar(false);

    http.get('/api/alunos').subscribe();

    const requisicao = controle.expectOne('/api/alunos');
    expect(requisicao.request.headers.has('Authorization')).toBe(false);
    requisicao.flush([]);
  });

  it('não envia o token para fora da API', () => {
    configurar(true);

    http.get('https://exemplo.invalido/recurso').subscribe({ error: () => undefined });

    const requisicao = controle.expectOne('https://exemplo.invalido/recurso');
    expect(requisicao.request.headers.has('Authorization')).toBe(false);
    requisicao.flush('', { status: 500, statusText: 'erro' });
  });
});
