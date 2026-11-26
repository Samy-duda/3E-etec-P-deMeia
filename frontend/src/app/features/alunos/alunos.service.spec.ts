import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { erroDaApi } from '../../core/erros/mensagens-erro';
import { paginaDe } from '../../testing/fabricas';
import { AlunosService } from './alunos.service';

describe('AlunosService', () => {
  let servico: AlunosService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servico = TestBed.inject(AlunosService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lista com paginação e filtros, ignorando filtros vazios', () => {
    servico.listar({ pagina: 1, tamanho: 20, matriculaEscolar: '2026-0001', nome: '   ' }).subscribe();

    const requisicao = http.expectOne((r) => r.url === '/api/alunos');
    expect(requisicao.request.method).toBe('GET');
    expect(requisicao.request.params.get('pagina')).toBe('1');
    expect(requisicao.request.params.get('tamanho')).toBe('20');
    expect(requisicao.request.params.get('matriculaEscolar')).toBe('2026-0001');
    expect(requisicao.request.params.has('nome')).toBe(false);
    requisicao.flush(paginaDe([]));
  });

  it('consulta um aluno pelo id', () => {
    let nome: string | undefined;
    servico.consultar(9).subscribe((aluno) => (nome = aluno.nome));

    const requisicao = http.expectOne('/api/alunos/9');
    expect(requisicao.request.method).toBe('GET');
    requisicao.flush({ id: 9, nome: 'Ana Souza', matriculaEscolar: '2026-0001', dataNascimento: '2008-03-15', ativo: true, criadoEm: '2026-10-16T12:00:00Z' });
    expect(nome).toBe('Ana Souza');
  });

  it('cadastra enviando o corpo do contrato', () => {
    const requisicao0 = { nome: 'Ana Souza', matriculaEscolar: '2026-0001', dataNascimento: '2008-03-15', email: null, telefone: null };
    servico.cadastrar(requisicao0).subscribe();

    const requisicao = http.expectOne('/api/alunos');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual(requisicao0);
    requisicao.flush({}, { status: 201, statusText: 'Created' });
  });

  it('propaga o erro de duplicidade da API para quem chamou, com o código do contrato', () => {
    let erroRecebido: HttpErrorResponse | undefined;
    servico
      .cadastrar({ nome: 'Ana S. Lima', matriculaEscolar: '2026-0001', dataNascimento: '2008-05-01' })
      .subscribe({ error: (erro: HttpErrorResponse) => (erroRecebido = erro) });

    http.expectOne('/api/alunos').flush(
      { timestamp: '', status: 409, codigo: 'ALUNO_DUPLICADO', mensagem: '', caminho: '', campos: [] },
      { status: 409, statusText: 'Conflict' },
    );

    expect(erroRecebido?.status).toBe(409);
    expect(erroDaApi(erroRecebido).codigo).toBe('ALUNO_DUPLICADO');
  });
});
