import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ErroExibicao, erroDaApi } from '../../core/erros/mensagens-erro';
import { MensagemErroComponent } from '../../shared/componentes/mensagem-erro/mensagem-erro.component';
import { PaginacaoComponent } from '../../shared/componentes/paginacao/paginacao.component';
import { formatarData, formatarNota } from '../../shared/formatacao/formatacao';
import { Pagina } from '../../shared/modelos/pagina';
import { PeriodoLetivoResposta } from '../../shared/modelos/periodo-letivo';
import { ProvaResposta } from '../../shared/modelos/prova';
import { PeriodosService } from '../periodos/periodos.service';
import { ProvasService } from './provas.service';

@Component({
  selector: 'app-provas-lista',
  imports: [ReactiveFormsModule, RouterLink, MensagemErroComponent, PaginacaoComponent],
  templateUrl: './provas-lista.component.html',
  styleUrl: './provas-lista.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvasListaComponent {
  private readonly fb = inject(FormBuilder);
  private readonly provas = inject(ProvasService);
  private readonly periodos = inject(PeriodosService);

  readonly tamanho = 20;
  readonly formatarData = formatarData;
  readonly formatarNota = formatarNota;

  readonly filtroForm = this.fb.nonNullable.group({ periodoLetivoId: [''] });

  readonly periodosDisponiveis = signal<PeriodoLetivoResposta[]>([]);
  readonly nomesPeriodo = computed(
    () => new Map(this.periodosDisponiveis().map((periodo) => [periodo.id, periodo.nome])),
  );

  readonly carregando = signal(false);
  readonly erro = signal<ErroExibicao | null>(null);
  readonly resultado = signal<Pagina<ProvaResposta> | null>(null);

  constructor() {
    this.periodos.listar().subscribe({
      next: (lista) => this.periodosDisponiveis.set(lista),
      error: (erro: unknown) => this.erro.set(erroDaApi(erro)),
    });
    this.filtroForm.controls.periodoLetivoId.valueChanges.subscribe(() => this.buscar(0));
    this.buscar(0);
  }

  aplicarFiltro(): void {
    this.buscar(0);
  }

  mudarPagina(pagina: number): void {
    this.buscar(pagina);
  }

  nomePeriodo(periodoId: number): string {
    return this.nomesPeriodo().get(periodoId) ?? `Período ${periodoId}`;
  }

  private buscar(pagina: number): void {
    const valor = this.filtroForm.controls.periodoLetivoId.value;
    const periodoLetivoId = valor === '' ? null : Number(valor);
    this.carregando.set(true);
    this.erro.set(null);
    this.provas.listar({ pagina, tamanho: this.tamanho, periodoLetivoId }).subscribe({
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
