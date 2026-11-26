import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, map, tap } from 'rxjs';
import { API_BASE } from '../api/api-base';
import { LoginResposta, UsuarioResumo } from '../../shared/modelos/usuario';
import { Perfil } from './perfil';
import { SessaoArmazenada, SessaoService } from './sessao.service';

/** Login, logout e usuário atual. Não guarda dados de negócio. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly sessaoService = inject(SessaoService);
  private readonly router = inject(Router);

  private readonly sessaoAtual = signal<SessaoArmazenada | null>(this.sessaoService.ler());

  readonly logado = computed(() => this.sessaoAtual() !== null);
  readonly usuario = computed<UsuarioResumo | null>(() => this.sessaoAtual()?.usuario ?? null);
  readonly perfil = computed<Perfil | null>(() => this.usuario()?.perfil ?? null);

  login(email: string, senha: string): Observable<UsuarioResumo> {
    return this.http
      .post<LoginResposta>(`${API_BASE}/auth/login`, { email: email.trim(), senha })
      .pipe(
        tap((resposta) => this.iniciarSessao(resposta)),
        map((resposta) => resposta.usuario),
      );
  }

  /** Limpa a sessão local sem navegar. Usado pelo interceptor quando a API responde 401. */
  encerrarSessao(): void {
    this.sessaoService.limpar();
    this.sessaoAtual.set(null);
  }

  logout(): void {
    this.encerrarSessao();
    this.router.navigateByUrl('/login');
  }

  token(): string | null {
    return this.sessaoAtual()?.token ?? null;
  }

  temPerfil(perfis: readonly Perfil[]): boolean {
    const perfil = this.perfil();
    return perfil !== null && perfis.includes(perfil);
  }

  private iniciarSessao(resposta: LoginResposta): void {
    const sessao: SessaoArmazenada = {
      token: resposta.token,
      expiraEm: resposta.expiraEm,
      usuario: resposta.usuario,
    };
    this.sessaoService.salvar(sessao);
    this.sessaoAtual.set(sessao);
  }
}
