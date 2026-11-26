import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ClassificacaoService } from './classificacao.service';

describe('ClassificacaoService', () => {
  let servico: ClassificacaoService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servico = TestBed.inject(ClassificacaoService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('consulta a classificação completa quando não há filtro de ano', () => {
    servico.consultar(7).subscribe();

    const requisicao = http.expectOne((r) => r.url === '/api/processos-seletivos/7/classificacao');
    expect(requisicao.request.method).toBe('GET');
    expect(requisicao.request.params.has('anoEscolar')).toBe(false);
    requisicao.flush({ processoSeletivoId: 7, calculadaEm: null, vagasPorAno: 10, itens: [] });
  });

  it('filtra por ano escolar quando informado', () => {
    servico.consultar(7, 2).subscribe();

    const requisicao = http.expectOne((r) => r.url === '/api/processos-seletivos/7/classificacao');
    expect(requisicao.request.params.get('anoEscolar')).toBe('2');
    requisicao.flush({ processoSeletivoId: 7, calculadaEm: null, vagasPorAno: 10, itens: [] });
  });

  it('gera a classificação com POST sem corpo', () => {
    servico.gerar(7).subscribe();

    const requisicao = http.expectOne('/api/processos-seletivos/7/classificacao');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toBeNull();
    requisicao.flush({ processoSeletivoId: 7, calculadaEm: '2026-11-21T12:00:00Z', vagasPorAno: 10, itens: [] }, { status: 201, statusText: 'Created' });
  });
});
