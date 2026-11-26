import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { paginaDe } from '../../testing/fabricas';
import { ProvasService } from './provas.service';

describe('ProvasService', () => {
  let servico: ProvasService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servico = TestBed.inject(ProvasService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lista provas filtrando por período quando informado', () => {
    servico.listar({ pagina: 0, tamanho: 20, periodoLetivoId: 3 }).subscribe();

    const requisicao = http.expectOne((r) => r.url === '/api/provas');
    expect(requisicao.request.params.get('periodoLetivoId')).toBe('3');
    requisicao.flush(paginaDe([]));
  });

  it('não envia o filtro de período quando ele é nulo', () => {
    servico.listar({ pagina: 0, tamanho: 20, periodoLetivoId: null }).subscribe();

    const requisicao = http.expectOne((r) => r.url === '/api/provas');
    expect(requisicao.request.params.has('periodoLetivoId')).toBe(false);
    requisicao.flush(paginaDe([]));
  });

  it('cadastra a prova com nota máxima numérica', () => {
    const corpo = {
      periodoLetivoId: 3,
      titulo: 'Simulado de Matemática',
      descricao: null,
      dataAplicacao: '2026-11-20',
      notaMaxima: 10,
    };
    servico.cadastrar(corpo).subscribe();

    const requisicao = http.expectOne('/api/provas');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual(corpo);
    requisicao.flush({}, { status: 201, statusText: 'Created' });
  });

  it('consulta uma prova pelo id', () => {
    servico.consultar(12).subscribe();

    const requisicao = http.expectOne('/api/provas/12');
    expect(requisicao.request.method).toBe('GET');
    requisicao.flush({});
  });
});
