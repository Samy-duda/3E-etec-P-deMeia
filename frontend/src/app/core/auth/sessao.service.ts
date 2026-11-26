import { Injectable } from '@angular/core';
import { UsuarioResumo } from '../../shared/modelos/usuario';

/** Sessão guardada em sessionStorage (ADR-006). */
export interface SessaoArmazenada {
  token: string;
  expiraEm: string;
  usuario: UsuarioResumo;
}

const CHAVE_SESSAO = 'pedemeia.sessao';

/**
 * Guarda e lê a sessão. Sessão vencida (ou corrompida) é descartada na leitura.
 * A expiração real é decidida pela API; esta checagem só evita chamadas inúteis.
 */
@Injectable({ providedIn: 'root' })
export class SessaoService {
  ler(agora: number = Date.now()): SessaoArmazenada | null {
    const bruto = this.armazenamento()?.getItem(CHAVE_SESSAO) ?? null;
    if (!bruto) {
      return null;
    }
    const sessao = this.interpretar(bruto);
    if (!sessao || !this.estaValida(sessao, agora)) {
      this.limpar();
      return null;
    }
    return sessao;
  }

  salvar(sessao: SessaoArmazenada): void {
    this.armazenamento()?.setItem(CHAVE_SESSAO, JSON.stringify(sessao));
  }

  limpar(): void {
    this.armazenamento()?.removeItem(CHAVE_SESSAO);
  }

  private estaValida(sessao: SessaoArmazenada, agora: number): boolean {
    const expiracao = Date.parse(sessao.expiraEm);
    return sessao.token.length > 0 && !Number.isNaN(expiracao) && expiracao > agora;
  }

  private interpretar(bruto: string): SessaoArmazenada | null {
    try {
      const valor = JSON.parse(bruto) as Partial<SessaoArmazenada> | null;
      if (valor && typeof valor.token === 'string' && typeof valor.expiraEm === 'string' && valor.usuario) {
        return valor as SessaoArmazenada;
      }
      return null;
    } catch {
      return null;
    }
  }

  private armazenamento(): Storage | null {
    try {
      return typeof sessionStorage === 'undefined' ? null : sessionStorage;
    } catch {
      return null;
    }
  }
}
