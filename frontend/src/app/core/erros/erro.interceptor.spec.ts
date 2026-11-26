import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { CHAVE_SESSAO_TESTE, corpoErro, sessaoDePerfil } from '../../testing/fabricas';
import { erroInterceptor } from './erro.interceptor';

describe('erroInterceptor', () => {
  let http: HttpClient;
  let controle: HttpTestingController;
  let router: Router;
  let navegar: ReturnType<typeof vi.spyOn>;

  function configurar(): void {
    sessionStorage.clear();
    sessionStorage.setItem(CHAVE_SESSAO_TESTE, JSON.stringify(sessaoDePerfil('SECRETARIA')));
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([erroInterceptor])),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    });
    http = TestBed.inject(HttpClient);
    controle = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    navegar = vi.spyOn(router, 'navigate').mockResolvedValue(true);
  }

  afterEach(() => {
    controle.verify();
    sessionStorage.clear();
  });

  it('401 fora do login limpa a sessão e manda para /login com motivo', () => {
    configurar();
    let erroRecebido = false;
    http.get('/api/alunos').subscribe({ error: () => (erroRecebido = true) });

    controle.expectOne('/api/alunos').flush(corpoErro('NAO_AUTENTICADO'), { status: 401, statusText: 'Unauthorized' });

    expect(sessionStorage.getItem(CHAVE_SESSAO_TESTE)).toBeNull();
    expect(navegar).toHaveBeenCalledWith(['/login'], { queryParams: { motivo: 'sessao-expirada' } });
    expect(erroRecebido).toBe(true);
  });

  it('401 do próprio login não limpa nada e não redireciona (CREDENCIAIS_INVALIDAS)', () => {
    configurar();
    http.post('/api/auth/login', {}).subscribe({ error: () => undefined });

    controle
      .expectOne('/api/auth/login')
      .flush(corpoErro('CREDENCIAIS_INVALIDAS'), { status: 401, statusText: 'Unauthorized' });

    expect(sessionStorage.getItem(CHAVE_SESSAO_TESTE)).not.toBeNull();
    expect(navegar).not.toHaveBeenCalled();
  });

  it('403 mantém a sessão e não redireciona (a tela mostra a mensagem)', () => {
    configurar();
    http.post('/api/alunos', {}).subscribe({ error: () => undefined });

    controle.expectOne('/api/alunos').flush(corpoErro('ACESSO_NEGADO'), { status: 403, statusText: 'Forbidden' });

    expect(sessionStorage.getItem(CHAVE_SESSAO_TESTE)).not.toBeNull();
    expect(navegar).not.toHaveBeenCalled();
  });
});
