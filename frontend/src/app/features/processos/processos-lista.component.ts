import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { perfilPodeOperar } from '../../core/auth/perfil';
import { ErroExibicao, erroDaApi } from '../../core/erros/mensagens-erro';
import { MensagemErroComponent } from '../../shared/componentes/mensagem-erro/mensagem-erro.component';
import { PaginacaoComponent } from '../../shared/componentes/paginacao/paginacao.component';
import { PeriodoLetivoResposta } from '../../shared/modelos/periodo-letivo';
import { Pagina } from '../../shared/modelos/pagina';
import { ProcessoSeletivoResposta } from '../../shared/modelos/processo-seletivo';
import { PeriodosService } from '../periodos/periodos.service';
import { ProcessosService } from './processos.service';

@Component({
  selector: 'app-processos-lista',
  imports: [ReactiveFormsModule, RouterLink, MensagemErroComponent, PaginacaoComponent],
  templateUrl: './processos-lista.component.html',
  styleUrl: './processos-lista.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcessosListaComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly processos = inject(ProcessosService);
  private readonly periodos = inject(PeriodosService);

  readonly tamanho = 20;
  /** Só operadores listam períodos (o contrato não dá esse acesso a CONSULTA). */
  readonly podeOperar = computed(() => perfilPodeOperar(this.auth.perfil()));

  readonly filtroForm = this.fb.nonNullable.group({ periodoLetivoId: [''] });

  readonly periodosDisponiveis = signal<PeriodoLetivoResposta[]>([]);
  readonly nomesPeriodo = computed(
    () => new Map(this.periodosDisponiveis().map((periodo) => [periodo.id, periodo.nome])),
  );

  readonly carregando = signal(false);
  readonly erro = signal<ErroExibicao | null>(null);
  readonly resultado = signal<Pagina<ProcessoSeletivoResposta> | null>(null);

  constructor() {
    if (this.podeOperar()) {
      this.periodos.listar().subscribe({
        next: (lista) => this.periodosDisponiveis.set(lista),
        error: (erro: unknown) => this.erro.set(erroDaApi(erro)),
      });
    }
    this.filtroForm.controls.periodoLetivoId.valueChanges.subscribe(() => this.buscar(0));
    this.buscar(0);
  }

  aplicarFiltro(): void {
    this.buscar(0);
  }

  mudarPagina(pagina: number): void {
    this.buscar(pagina);
  }

  /** Para CONSULTA, sem nome de período (não há acesso ao cadastro): mostra o número. */
  rotuloPeriodo(periodoId: number): string {
    return this.nomesPeriodo().get(periodoId) ?? `Período nº ${periodoId}`;
  }

  private buscar(pagina: number): void {
    const valor = this.filtroForm.controls.periodoLetivoId.value;
    const periodoLetivoId = this.podeOperar() && valor !== '' ? Number(valor) : null;
    this.carregando.set(true);
    this.erro.set(null);
    this.processos.listar({ pagina, tamanho: this.tamanho, periodoLetivoId }).subscribe({
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
