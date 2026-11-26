import { TestBed } from '@angular/core/testing';
import { CHAVE_SESSAO_TESTE, EXPIRA_EM_FUTURO, sessaoDePerfil } from '../../testing/fabricas';
import { SessaoService } from './sessao.service';

describe('SessaoService', () => {
  let servico: SessaoService;

  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({});
    servico = TestBed.inject(SessaoService);
  });

  it('salva a sessão em sessionStorage e a lê de volta', () => {
    servico.salvar(sessaoDePerfil('ADMIN'));

    expect(servico.ler()?.usuario.perfil).toBe('ADMIN');
    expect(sessionStorage.getItem(CHAVE_SESSAO_TESTE)).toContain('token-admin');
  });

  it('devolve null e limpa quando não há sessão', () => {
    expect(servico.ler()).toBeNull();
  });

  it('descarta sessão vencida na leitura', () => {
    servico.salvar({ ...sessaoDePerfil('CONSULTA'), expiraEm: '2020-01-01T00:00:00Z' });

    expect(servico.ler()).toBeNull();
    expect(sessionStorage.getItem(CHAVE_SESSAO_TESTE)).toBeNull();
  });

  it('considera a expiração em relação ao instante informado', () => {
    servico.salvar({ ...sessaoDePerfil('SECRETARIA'), expiraEm: '2026-10-16T12:00:00Z' });

    expect(servico.ler(Date.parse('2026-10-16T11:59:00Z'))).not.toBeNull();
    expect(servico.ler(Date.parse('2026-10-16T12:01:00Z'))).toBeNull();
  });

  it('descarta conteúdo corrompido sem lançar erro', () => {
    sessionStorage.setItem(CHAVE_SESSAO_TESTE, '{não é json');

    expect(servico.ler()).toBeNull();
    expect(sessionStorage.getItem(CHAVE_SESSAO_TESTE)).toBeNull();
  });

  it('descarta sessão sem token ou sem data de expiração válida', () => {
    sessionStorage.setItem(CHAVE_SESSAO_TESTE, JSON.stringify({ token: '', expiraEm: EXPIRA_EM_FUTURO, usuario: {} }));
    expect(servico.ler()).toBeNull();

    sessionStorage.setItem(CHAVE_SESSAO_TESTE, JSON.stringify({ token: 'x', expiraEm: 'nao-e-data', usuario: {} }));
    expect(servico.ler()).toBeNull();
  });

  it('limpar remove a sessão', () => {
    servico.salvar(sessaoDePerfil('ADMIN'));
    servico.limpar();

    expect(sessionStorage.getItem(CHAVE_SESSAO_TESTE)).toBeNull();
  });
});
