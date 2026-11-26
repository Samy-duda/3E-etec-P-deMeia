import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ErroExibicao, erroDaApi } from '../../core/erros/mensagens-erro';
import { MensagemErroComponent } from '../../shared/componentes/mensagem-erro/mensagem-erro.component';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, MensagemErroComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly rota = inject(ActivatedRoute);

  /** Campo de texto (não type="email"): contas de teste têm "_" no domínio (ADR-006). */
  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.maxLength(255)]],
    senha: ['', [Validators.required, Validators.maxLength(128)]],
  });

  readonly enviando = signal(false);
  readonly erro = signal<ErroExibicao | null>(null);
  readonly aviso = signal<string | null>(
    this.rota.snapshot.queryParamMap.get('motivo') === 'sessao-expirada'
      ? 'Sua sessão expirou. Entre novamente.'
      : null,
  );

  entrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { email, senha } = this.form.getRawValue();
    this.enviando.set(true);
    this.erro.set(null);
    this.auth.login(email, senha).subscribe({
      next: () => {
        this.enviando.set(false);
        this.router.navigateByUrl(this.destinoAposLogin());
      },
      error: (erro: unknown) => {
        this.enviando.set(false);
        this.erro.set(erroDaApi(erro));
      },
    });
  }

  /** Volta para a rota pedida pelo guard, sem aceitar endereço externo. */
  private destinoAposLogin(): string {
    const retorno = this.rota.snapshot.queryParamMap.get('retorno');
    return retorno && retorno.startsWith('/') && !retorno.startsWith('/login') ? retorno : '/';
  }
}
