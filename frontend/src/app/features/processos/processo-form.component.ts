import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ErroExibicao, erroDaApi } from '../../core/erros/mensagens-erro';
import { MensagemErroComponent } from '../../shared/componentes/mensagem-erro/mensagem-erro.component';
import { PeriodoLetivoResposta } from '../../shared/modelos/periodo-letivo';
import { ProcessoSeletivoRequisicao } from '../../shared/modelos/processo-seletivo';
import { ProvaResposta } from '../../shared/modelos/prova';
import { PeriodosService } from '../periodos/periodos.service';
import { ProvasService } from '../provas/provas.service';
import { ProcessosService } from './processos.service';

@Component({
  selector: 'app-processo-form',
  imports: [ReactiveFormsModule, RouterLink, MensagemErroComponent],
  templateUrl: './processo-form.component.html',
  styleUrl: './processo-form.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcessoFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly processos = inject(ProcessosService);
  private readonly periodos = inject(PeriodosService);
  private readonly provas = inject(ProvasService);
  private readonly router = inject(Router);

  readonly form = this.fb.nonNullable.group({
    periodoLetivoId: ['', [Validators.required]],
    provaId: ['', [Validators.required]],
  });

  readonly periodosDisponiveis = signal<PeriodoLetivoResposta[]>([]);
  readonly provasDoPeriodo = signal<ProvaResposta[]>([]);
  readonly carregandoPeriodos = signal(true);
  readonly carregandoProvas = signal(false);
  readonly erroCarregamento = signal<ErroExibicao | null>(null);
  readonly enviando = signal(false);
  readonly erro = signal<ErroExibicao | null>(null);

  constructor() {
    this.periodos.listar().subscribe({
      next: (lista) => {
        this.periodosDisponiveis.set(lista);
        this.carregandoPeriodos.set(false);
      },
      error: (erro: unknown) => {
        this.erroCarregamento.set(erroDaApi(erro));
        this.carregandoPeriodos.set(false);
      },
    });
    this.form.controls.periodoLetivoId.valueChanges.subscribe(() => this.aoMudarPeriodo());
  }

  /** Ao trocar o período, recarrega as provas dele e limpa a prova escolhida. */
  aoMudarPeriodo(): void {
    const periodoId = this.form.controls.periodoLetivoId.value;
    this.form.controls.provaId.setValue('');
    this.provasDoPeriodo.set([]);
    if (periodoId === '') {
      return;
    }
    this.carregandoProvas.set(true);
    this.provas.listar({ pagina: 0, tamanho: 100, periodoLetivoId: Number(periodoId) }).subscribe({
      next: (resposta) => {
        this.provasDoPeriodo.set(resposta.conteudo);
        this.carregandoProvas.set(false);
      },
      error: (erro: unknown) => {
        this.erroCarregamento.set(erroDaApi(erro));
        this.carregandoProvas.set(false);
      },
    });
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const valores = this.form.getRawValue();
    const requisicao: ProcessoSeletivoRequisicao = {
      periodoLetivoId: Number(valores.periodoLetivoId),
      provaId: Number(valores.provaId),
    };
    this.enviando.set(true);
    this.erro.set(null);
    this.processos.criar(requisicao).subscribe({
      next: (processo) => {
        this.enviando.set(false);
        this.router.navigate(['/processos-seletivos', processo.id]);
      },
      error: (erro: unknown) => {
        this.enviando.set(false);
        this.erro.set(erroDaApi(erro));
      },
    });
  }
}
