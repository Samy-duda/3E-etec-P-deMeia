import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BolsasService } from './bolsas.service';

describe('BolsasService', () => {
  let servico: BolsasService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servico = TestBed.inject(BolsasService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lista as bolsas do processo (consulta permitida a CONSULTA)', () => {
    servico.listarPorProcesso(7).subscribe();

    const requisicao = http.expectOne('/api/processos-seletivos/7/bolsas');
    expect(requisicao.request.method).toBe('GET');
    requisicao.flush([]);
  });

  it('concede em lote quando não há matrícula no corpo', () => {
    servico.conceder(7).subscribe();

    const requisicao = http.expectOne('/api/processos-seletivos/7/bolsas');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual({});
    requisicao.flush([], { status: 201, statusText: 'Created' });
  });

  it('concede a uma matrícula específica', () => {
    servico.conceder(7, { matriculaId: 40 }).subscribe();

    const requisicao = http.expectOne('/api/processos-seletivos/7/bolsas');
    expect(requisicao.request.body).toEqual({ matriculaId: 40 });
    requisicao.flush([], { status: 201, statusText: 'Created' });
  });

  it('não expõe cancelamento ou alteração de situação (MVP-CA30)', () => {
    const metodos = Object.getOwnPropertyNames(Object.getPrototypeOf(servico));

    expect(metodos.some((nome) => /cancel|situac/i.test(nome))).toBe(false);
  });
});
