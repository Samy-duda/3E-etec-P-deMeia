import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PERFIS, Perfil } from '../../core/auth/perfil';
import { ErroExibicao, erroDaApi } from '../../core/erros/mensagens-erro';
import { MensagemErroComponent } from '../../shared/componentes/mensagem-erro/mensagem-erro.component';
import { PaginacaoComponent } from '../../shared/componentes/paginacao/paginacao.component';
import { formatarInstante } from '../../shared/formatacao/formatacao';
import { Pagina } from '../../shared/modelos/pagina';
import { UsuarioResposta } from '../../shared/modelos/usuario';
import { UsuariosService } from './usuarios.service';

/** Contas de acesso. Restrita a ADMIN (rota e API). */
@Component({
  selector: 'app-usuarios-lista',
  imports: [ReactiveFormsModule, MensagemErroComponent, PaginacaoComponent],
  templateUrl: './usuarios-lista.component.html',
  styleUrl: './usuarios-lista.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UsuariosListaComponent {
  private readonly fb = inject(FormBuilder);
  private readonly usuarios = inject(UsuariosService);

  readonly tamanho = 20;
  readonly perfis: readonly Perfil[] = PERFIS;
  readonly formatarInstante = formatarInstante;

  readonly carregando = signal(false);
  readonly erro = signal<ErroExibicao | null>(null);
  readonly resultado = signal<Pagina<UsuarioResposta> | null>(null);

  readonly form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.maxLength(255)]],
    senha: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
    perfil: ['CONSULTA' as Perfil, [Validators.required]],
  });
  readonly enviando = signal(false);
  readonly erroCadastro = signal<ErroExibicao | null>(null);
  readonly sucesso = signal<string | null>(null);

  constructor() {
    this.carregar(0);
  }

  mudarPagina(pagina: number): void {
    this.carregar(pagina);
  }

  criar(): void {
    this.sucesso.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const valores = this.form.getRawValue();
    this.enviando.set(true);
    this.erroCadastro.set(null);
    this.usuarios
      .criar({ nome: valores.nome.trim(), email: valores.email.trim(), senha: valores.senha, perfil: valores.perfil })
      .subscribe({
        next: (usuario) => {
          this.enviando.set(false);
          this.sucesso.set(`Conta ${usuario.email} criada com o perfil ${usuario.perfil}.`);
          this.form.reset({ nome: '', email: '', senha: '', perfil: 'CONSULTA' });
          this.carregar(0);
        },
        error: (erro: unknown) => {
          this.enviando.set(false);
          this.erroCadastro.set(erroDaApi(erro));
        },
      });
  }

  private carregar(pagina: number): void {
    this.carregando.set(true);
    this.erro.set(null);
    this.usuarios.listar({ pagina, tamanho: this.tamanho }).subscribe({
      next: (resposta) => {
        this.resultado.set(resposta);
        this.carregando.set(false);
      },
      error: (erro: unknown) => {
        this.erro.set(erroDaApi(erro));
        this.carregando.set(false);
      },
    });
  }
}
