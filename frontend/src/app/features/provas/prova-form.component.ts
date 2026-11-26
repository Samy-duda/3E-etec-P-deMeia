import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ErroExibicao, erroDaApi } from '../../core/erros/mensagens-erro';
import { MensagemErroComponent } from '../../shared/componentes/mensagem-erro/mensagem-erro.component';
import { PeriodoLetivoResposta } from '../../shared/modelos/periodo-letivo';
import { ProvaRequisicao } from '../../shared/modelos/prova';
import { ateDuasCasasDecimais } from '../../shared/validadores/formatos';
import { PeriodosService } from '../periodos/periodos.service';
import { ProvasService } from './provas.service';

@Component({
  selector: 'app-prova-form',
  imports: [ReactiveFormsModule, RouterLink, MensagemErroComponent],
  templateUrl: './prova-form.component.html',
  styleUrl: './prova-form.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvaFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly provas = inject(ProvasService);
  private readonly periodos = inject(PeriodosService);
  private readonly router = inject(Router);

  /** Formato apenas. Nota máxima maior que zero é conferida pela API (P04, MVP-CA09). */
  readonly form = this.fb.nonNullable.group({
    periodoLetivoId: ['', [Validators.required]],
    titulo: ['', [Validators.required, Validators.maxLength(150)]],
    descricao: [''],
    dataAplicacao: ['', [Validators.required]],
    notaMaxima: [null as number | null, [Validators.required, ateDuasCasasDecimais]],
  });

  readonly periodosDisponiveis = signal<PeriodoLetivoResposta[]>([]);
  readonly carregandoPeriodos = signal(true);
  readonly erroPeriodos = signal<ErroExibicao | null>(null);
  readonly enviando = signal(false);
  readonly erro = signal<ErroExibicao | null>(null);

  constructor() {
    this.periodos.listar().subscribe({
      next: (lista) => {
        this.periodosDisponiveis.set(lista);
        this.carregandoPeriodos.set(false);
      },
      error: (erro: unknown) => {
        this.erroPeriodos.set(erroDaApi(erro));
        this.carregandoPeriodos.set(false);
      },
    });
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const valores = this.form.getRawValue();
    const requisicao: ProvaRequisicao = {
      periodoLetivoId: Number(valores.periodoLetivoId),
      titulo: valores.titulo.trim(),
      descricao: valores.descricao.trim() || null,
      dataAplicacao: valores.dataAplicacao,
      notaMaxima: Number(valores.notaMaxima),
    };
    this.enviando.set(true);
    this.erro.set(null);
    this.provas.cadastrar(requisicao).subscribe({
      next: (prova) => {
        this.enviando.set(false);
        this.router.navigate(['/provas', prova.id, 'notas']);
      },
      error: (erro: unknown) => {
        this.enviando.set(false);
        this.erro.set(erroDaApi(erro));
      },
    });
  }
}
