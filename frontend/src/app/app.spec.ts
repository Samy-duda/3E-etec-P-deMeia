import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { App } from './app';
import { CHAVE_SESSAO_TESTE, sessaoDePerfil } from './testing/fabricas';

describe('App', () => {
  afterEach(() => {
    sessionStorage.clear();
  });

  function configurar(comSessao: boolean): void {
    sessionStorage.clear();
    if (comSessao) {
      sessionStorage.setItem(CHAVE_SESSAO_TESTE, JSON.stringify(sessaoDePerfil('ADMIN')));
    }
    TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    });
  }

  it('não mostra o menu quando não há sessão', () => {
    configurar(false);
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('app-layout')).toBeNull();
  });

  it('mostra o menu quando há sessão', () => {
    configurar(true);
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('app-layout')).not.toBeNull();
  });
});
