import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ErroExibicao, erroDaApi } from '../../core/erros/mensagens-erro';
import { MensagemErroComponent } from '../../shared/componentes/mensagem-erro/mensagem-erro.component';
import { formatarData } from '../../shared/formatacao/formatacao';
import { PeriodoLetivoResposta } from '../../shared/modelos/periodo-letivo';
import { PeriodosService } from './periodos.service';

@Component({
  selector: 'app-periodos-letivos',
  imports: [ReactiveFormsModule, RouterLink, MensagemErroComponent],
  templateUrl: './periodos-letivos.component.html',
  styleUrl: './periodos-letivos.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeriodosLetivosComponent {
  private readonly fb = inject(FormBuilder);
  private readonly periodos = inject(PeriodosService);

  readonly formatarData = formatarData;

  readonly form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(60)]],
    dataInicio: ['', [Validators.required]],
    dataFim: ['', [Validators.required]],
  });

  readonly carregando = signal(false);
  readonly erro = signal<ErroExibicao | null>(null);
  readonly lista = signal<PeriodoLetivoResposta[]>([]);

  readonly enviando = signal(false);
  readonly erroCadastro = signal<ErroExibicao | null>(null);
  readonly sucesso = signal<string | null>(null);

  constructor() {
    this.carregar();
  }

  salvar(): void {
    this.sucesso.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const valores = this.form.getRawValue();
    this.enviando.set(true);
    this.erroCadastro.set(null);
    this.periodos.cadastrar({ ...valores, nome: valores.nome.trim() }).subscribe({
      next: (periodo) => {
        this.enviando.set(false);
        this.sucesso.set(`Período ${periodo.nome} cadastrado.`);
        this.form.reset();
        this.carregar();
      },
      error: (erro: unknown) => {
        this.enviando.set(false);
        this.erroCadastro.set(erroDaApi(erro));
      },
    });
  }

  private carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);
    this.periodos.listar().subscribe({
      next: (lista) => {
        this.lista.set(lista);
        this.carregando.set(false);
      },
      error: (erro: unknown) => {
        this.erro.set(erroDaApi(erro));
        this.carregando.set(false);
      },
    });
  }
}
