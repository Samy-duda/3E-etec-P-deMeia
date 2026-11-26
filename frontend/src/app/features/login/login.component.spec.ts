import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { corpoErro } from '../../testing/fabricas';
import { LoginComponent } from './login.component';

function configurar(parametrosRota: Record<string, string> = {}): ComponentFixture<LoginComponent> {
  sessionStorage.clear();
  TestBed.configureTestingModule({
    imports: [LoginComponent],
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      provideRouter([]),
      { provide: ActivatedRoute, useValue: { snapshot: { queryParamMap: convertToParamMap(parametrosRota) } } },
    ],
  });
  const fixture = TestBed.createComponent(LoginComponent);
  fixture.detectChanges();
  return fixture;
}

describe('LoginComponent', () => {
  afterEach(() => {
    sessionStorage.clear();
  });

  describe('envio do formulário', () => {
    let fixture: ComponentFixture<LoginComponent>;
    let http: HttpTestingController;

    beforeEach(() => {
      fixture = configurar();
      http = TestBed.inject(HttpTestingController);
    });

    afterEach(() => http.verify());

    it('não envia requisição com campos vazios', () => {
      fixture.componentInstance.entrar();

      http.expectNone('/api/auth/login');
    });

    it('usa campo de texto para o e-mail, porque contas de teste têm "_" no domínio', () => {
      const campo = fixture.nativeElement.querySelector('#email') as HTMLInputElement;

      expect(campo.type).toBe('text');
    });

    it('mostra a mensagem do código CREDENCIAIS_INVALIDAS, e não o texto da API', () => {
      const componente = fixture.componentInstance;
      componente.form.setValue({ email: 'test@IA_admin', senha: 'errada' });

      componente.entrar();
      http.expectOne('/api/auth/login').flush(corpoErro('CREDENCIAIS_INVALIDAS'), {
        status: 401,
        statusText: 'Unauthorized',
      });
      fixture.detectChanges();

      const alerta = fixture.nativeElement.querySelector('[role="alert"]') as HTMLElement;
      expect(alerta.textContent).toContain('E-mail ou senha inválidos.');
      expect(alerta.textContent).not.toContain('mensagem do servidor');
    });
  });

  it('mostra aviso quando o usuário chega ao login por sessão expirada', () => {
    const fixture = configurar({ motivo: 'sessao-expirada' });

    const aviso = fixture.nativeElement.querySelector('.alerta-info') as HTMLElement;
    expect(aviso.textContent).toContain('Sua sessão expirou.');
  });
});
