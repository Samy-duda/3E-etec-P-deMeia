import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { paginaDe } from '../../testing/fabricas';
import { UsuariosService } from './usuarios.service';

describe('UsuariosService', () => {
  let servico: UsuariosService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servico = TestBed.inject(UsuariosService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lista contas com paginação', () => {
    servico.listar({ pagina: 0, tamanho: 20 }).subscribe();

    const requisicao = http.expectOne((r) => r.url === '/api/usuarios');
    expect(requisicao.request.method).toBe('GET');
    requisicao.flush(paginaDe([]));
  });

  it('cria conta enviando senha somente no corpo da requisição', () => {
    const corpo = { nome: 'Equipe', email: 'test@IA_consulta', senha: 'senha-de-teste', perfil: 'CONSULTA' as const };
    servico.criar(corpo).subscribe();

    const requisicao = http.expectOne('/api/usuarios');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual(corpo);
    requisicao.flush({}, { status: 201, statusText: 'Created' });
  });
});
