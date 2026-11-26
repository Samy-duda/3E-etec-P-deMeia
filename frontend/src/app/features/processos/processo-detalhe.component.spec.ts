import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { Perfil } from '../../core/auth/perfil';
import { CHAVE_SESSAO_TESTE, paginaDe, sessaoDePerfil } from '../../testing/fabricas';
import { ProcessoDetalheComponent } from './processo-detalhe.component';

const PROCESSO = { id: 7, periodoLetivoId: 3, provaId: 12, status: 'CRIADO' as const };
const CLASSIFICACAO_NAO_GERADA = { processoSeletivoId: 7, calculadaEm: null, vagasPorAno: 10, itens: [] };

/** Abre a tela com a sessão do perfil e responde às três consultas iniciais. */
function abrirTela(perfil: Perfil): { fixture: ComponentFixture<ProcessoDetalheComponent>; http: HttpTestingController } {
  sessionStorage.clear();
  sessionStorage.setItem(CHAVE_SESSAO_TESTE, JSON.stringify(sessaoDePerfil(perfil)));
  TestBed.configureTestingModule({
    imports: [ProcessoDetalheComponent],
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      provideRouter([]),
      { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ processoId: '7' }) } } },
    ],
  });
  const http = TestBed.inject(HttpTestingController);
  const fixture = TestBed.createComponent(ProcessoDetalheComponent);
  fixture.detectChanges();
  http.expectOne('/api/processos-seletivos/7').flush(PROCESSO);
  http.expectOne('/api/processos-seletivos/7/classificacao').flush(CLASSIFICACAO_NAO_GERADA);
  http.expectOne('/api/processos-seletivos/7/bolsas').flush(paginaDe([]).conteudo);
  fixture.detectChanges();
  return { fixture, http };
}

function botaoComTexto(elemento: HTMLElement, texto: string): HTMLButtonElement | undefined {
  return Array.from(elemento.querySelectorAll('button')).find((botao) => botao.textContent?.includes(texto));
}

describe('ProcessoDetalheComponent (ações por perfil)', () => {
  afterEach(() => {
    sessionStorage.clear();
  });

  it('CONSULTA não vê o botão de gerar classificação (MVP-CA10)', () => {
    const { fixture, http } = abrirTela('CONSULTA');

    expect(botaoComTexto(fixture.nativeElement, 'Gerar classificação')).toBeUndefined();
    expect(fixture.nativeElement.textContent).toContain('A classificação ainda não foi gerada.');
    http.verify();
  });

  it('SECRETARIA vê o botão e gera a classificação com POST sem corpo', () => {
    const { fixture, http } = abrirTela('SECRETARIA');

    const botao = botaoComTexto(fixture.nativeElement, 'Gerar classificação');
    expect(botao).toBeDefined();
    botao?.click();

    const requisicao = http.expectOne('/api/processos-seletivos/7/classificacao');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toBeNull();
    requisicao.flush({ processoSeletivoId: 7, calculadaEm: null, vagasPorAno: 10, itens: [] }, { status: 201, statusText: 'Created' });
    http.verify();
  });
});
