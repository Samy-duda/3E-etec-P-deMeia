import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { PeriodosService } from './periodos.service';

describe('PeriodosService', () => {
  let servico: PeriodosService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servico = TestBed.inject(PeriodosService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lista os períodos sem paginação', () => {
    let total = -1;
    servico.listar().subscribe((lista) => (total = lista.length));

    const requisicao = http.expectOne('/api/periodos-letivos');
    expect(requisicao.request.method).toBe('GET');
    expect(requisicao.request.params.keys()).toEqual([]);
    requisicao.flush([{ id: 1, nome: '2026-1', dataInicio: '2026-02-01', dataFim: '2026-06-30', ativo: true }]);
    expect(total).toBe(1);
  });

  it('cadastra um período com as datas no formato do contrato', () => {
    const corpo = { nome: '2026-1', dataInicio: '2026-02-01', dataFim: '2026-06-30' };
    servico.cadastrar(corpo).subscribe();

    const requisicao = http.expectOne('/api/periodos-letivos');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual(corpo);
    requisicao.flush({}, { status: 201, statusText: 'Created' });
  });
});
