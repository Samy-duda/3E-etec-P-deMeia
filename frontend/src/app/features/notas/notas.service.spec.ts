import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { paginaDe } from '../../testing/fabricas';
import { NotasService } from './notas.service';

describe('NotasService', () => {
  let servico: NotasService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servico = TestBed.inject(NotasService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lista as notas da prova com paginação', () => {
    servico.listarPorProva(5, { pagina: 0, tamanho: 20 }).subscribe();

    const requisicao = http.expectOne((r) => r.url === '/api/provas/5/notas');
    expect(requisicao.request.method).toBe('GET');
    expect(requisicao.request.params.get('tamanho')).toBe('20');
    requisicao.flush(paginaDe([]));
  });

  it('lança a nota usando a matrícula (não o id do aluno)', () => {
    servico.lancar(5, { matriculaId: 40, nota: 7.5 }).subscribe();

    const requisicao = http.expectOne('/api/provas/5/notas');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual({ matriculaId: 40, nota: 7.5 });
    requisicao.flush({}, { status: 201, statusText: 'Created' });
  });

  it('corrige a nota pelo id da nota, enviando só o valor', () => {
    servico.corrigir(5, 77, { nota: 8 }).subscribe();

    const requisicao = http.expectOne('/api/provas/5/notas/77');
    expect(requisicao.request.method).toBe('PUT');
    expect(requisicao.request.body).toEqual({ nota: 8 });
    requisicao.flush({});
  });
});
