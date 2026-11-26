import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { paginaDe } from '../../testing/fabricas';
import { MatriculasService } from './matriculas.service';

describe('MatriculasService', () => {
  let servico: MatriculasService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servico = TestBed.inject(MatriculasService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lista as matrículas do período informado, com paginação', () => {
    servico.listarPorPeriodo(4, { pagina: 2, tamanho: 20 }).subscribe();

    const requisicao = http.expectOne((r) => r.url === '/api/periodos-letivos/4/matriculas');
    expect(requisicao.request.method).toBe('GET');
    expect(requisicao.request.params.get('pagina')).toBe('2');
    requisicao.flush(paginaDe([]));
  });

  it('matricula com ano escolar e envia alunoId, sem turma vazia', () => {
    servico.matricular(4, { alunoId: 9, anoEscolar: 2, turma: null }).subscribe();

    const requisicao = http.expectOne('/api/periodos-letivos/4/matriculas');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual({ alunoId: 9, anoEscolar: 2, turma: null });
    requisicao.flush({}, { status: 201, statusText: 'Created' });
  });
});
