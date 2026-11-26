import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { paginaDe } from '../../testing/fabricas';
import { ProcessosService } from './processos.service';

describe('ProcessosService', () => {
  let servico: ProcessosService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servico = TestBed.inject(ProcessosService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lista processos com filtro opcional por período', () => {
    servico.listar({ pagina: 0, tamanho: 20, periodoLetivoId: 3 }).subscribe();

    const requisicao = http.expectOne((r) => r.url === '/api/processos-seletivos');
    expect(requisicao.request.method).toBe('GET');
    expect(requisicao.request.params.get('periodoLetivoId')).toBe('3');
    requisicao.flush(paginaDe([]));
  });

  it('consulta um processo pelo id', () => {
    servico.consultar(7).subscribe();

    const requisicao = http.expectOne('/api/processos-seletivos/7');
    expect(requisicao.request.method).toBe('GET');
    requisicao.flush({});
  });

  it('cria o processo ligando período e prova', () => {
    servico.criar({ periodoLetivoId: 3, provaId: 12 }).subscribe();

    const requisicao = http.expectOne('/api/processos-seletivos');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual({ periodoLetivoId: 3, provaId: 12 });
    requisicao.flush({}, { status: 201, statusText: 'Created' });
  });
});
