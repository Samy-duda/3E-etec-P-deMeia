import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ErroExibicao, erroDaApi } from '../../core/erros/mensagens-erro';
import { MensagemErroComponent } from '../../shared/componentes/mensagem-erro/mensagem-erro.component';
import { PaginacaoComponent } from '../../shared/componentes/paginacao/paginacao.component';
import { formatarInstante, formatarNota } from '../../shared/formatacao/formatacao';
import { MatriculaResposta } from '../../shared/modelos/matricula';
import { NotaResposta } from '../../shared/modelos/nota';
import { Pagina } from '../../shared/modelos/pagina';
import { ProvaResposta } from '../../shared/modelos/prova';
import { ateDuasCasasDecimais } from '../../shared/validadores/formatos';
import { MatriculasService } from '../matriculas/matriculas.service';
import { ProvasService } from '../provas/provas.service';
import { NotasService } from './notas.service';

@Component({
  selector: 'app-notas-prova',
  imports: [ReactiveFormsModule, RouterLink, MensagemErroComponent, PaginacaoComponent],
  templateUrl: './notas-prova.component.html',
  styleUrl: './notas-prova.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotasProvaComponent {
  private readonly fb = inject(FormBuilder);
  private readonly rota = inject(ActivatedRoute);
  private readonly provas = inject(ProvasService);
  private readonly matriculas = inject(MatriculasService);
  private readonly notas = inject(NotasService);

  readonly provaId = Number(this.rota.snapshot.paramMap.get('provaId'));
  readonly tamanho = 20;
  readonly formatarNota = formatarNota;
  readonly formatarInstante = formatarInstante;

  readonly prova = signal<ProvaResposta | null>(null);
  readonly carregandoProva = signal(true);
  readonly erroProva = signal<ErroExibicao | null>(null);

  readonly carregandoNotas = signal(false);
  readonly erroNotas = signal<ErroExibicao | null>(null);
  readonly notasPagina = signal<Pagina<NotaResposta> | null>(null);

  readonly carregandoMatriculas = signal(false);
  readonly erroMatriculas = signal<ErroExibicao | null>(null);
  readonly matriculasPagina = signal<Pagina<MatriculaResposta> | null>(null);

  readonly lancamentoForm = this.fb.nonNullable.group({
    matriculaId: [null as number | null, [Validators.required]],
    nota: [null as number | null, [Validators.required, Validators.min(0), ateDuasCasasDecimais]],
  });
  readonly enviandoNota = signal(false);
  readonly erroLancamento = signal<ErroExibicao | null>(null);
  readonly sucessoLancamento = signal<string | null>(null);

  /** Correção de nota já lançada (PUT). Só é aceita antes da classificação (Q07). */
  readonly notaEmCorrecao = signal<number | null>(null);
  readonly correcaoControle = new FormControl<number | null>(null, [
    Validators.required,
    Validators.min(0),
    ateDuasCasasDecimais,
  ]);
  readonly enviandoCorrecao = signal(false);
  readonly erroCorrecao = signal<ErroExibicao | null>(null);

  constructor() {
    this.carregarProva();
    this.carregarNotas(0);
  }

  mudarPaginaNotas(pagina: number): void {
    this.carregarNotas(pagina);
  }

  mudarPaginaMatriculas(pagina: number): void {
    this.carregarMatriculas(pagina);
  }

  lancar(): void {
    this.sucessoLancamento.set(null);
    if (this.lancamentoForm.invalid) {
      this.lancamentoForm.markAllAsTouched();
      return;
    }
    const valores = this.lancamentoForm.getRawValue();
    this.enviandoNota.set(true);
    this.erroLancamento.set(null);
    this.notas.lancar(this.provaId, {
      matriculaId: Number(valores.matriculaId),
      nota: Number(valores.nota),
    }).subscribe({
      next: (nota) => {
        this.enviandoNota.set(false);
        this.sucessoLancamento.set(`Nota ${formatarNota(nota.nota)} lançada para ${nota.nomeAluno}.`);
        this.lancamentoForm.reset({ matriculaId: null, nota: null });
        this.carregarNotas(this.notasPagina()?.pagina ?? 0);
      },
      error: (erro: unknown) => {
        this.enviandoNota.set(false);
        this.erroLancamento.set(erroDaApi(erro));
      },
    });
  }

  iniciarCorrecao(nota: NotaResposta): void {
    this.erroCorrecao.set(null);
    this.correcaoControle.setValue(nota.nota);
    this.notaEmCorrecao.set(nota.id);
  }

  cancelarCorrecao(): void {
    this.notaEmCorrecao.set(null);
    this.erroCorrecao.set(null);
  }

  salvarCorrecao(nota: NotaResposta): void {
    if (this.correcaoControle.invalid) {
      this.correcaoControle.markAsTouched();
      return;
    }
    this.enviandoCorrecao.set(true);
    this.erroCorrecao.set(null);
    this.notas.corrigir(this.provaId, nota.id, { nota: Number(this.correcaoControle.value) }).subscribe({
      next: () => {
        this.enviandoCorrecao.set(false);
        this.notaEmCorrecao.set(null);
        this.carregarNotas(this.notasPagina()?.pagina ?? 0);
      },
      error: (erro: unknown) => {
        this.enviandoCorrecao.set(false);
        this.erroCorrecao.set(erroDaApi(erro));
      },
    });
  }

  private carregarProva(): void {
    this.carregandoProva.set(true);
    this.provas.consultar(this.provaId).subscribe({
      next: (prova) => {
        this.prova.set(prova);
        this.carregandoProva.set(false);
        this.carregarMatriculas(0);
      },
      error: (erro: unknown) => {
        this.erroProva.set(erroDaApi(erro));
        this.carregandoProva.set(false);
      },
    });
  }

  private carregarNotas(pagina: number): void {
    this.carregandoNotas.set(true);
    this.erroNotas.set(null);
    this.notas.listarPorProva(this.provaId, { pagina, tamanho: this.tamanho }).subscribe({
      next: (resposta) => {
        this.notasPagina.set(resposta);
        this.carregandoNotas.set(false);
      },
      error: (erro: unknown) => {
        this.erroNotas.set(erroDaApi(erro));
        this.carregandoNotas.set(false);
      },
    });
  }

  /** Matrículas do período da prova, para escolher o aluno no lançamento. */
  private carregarMatriculas(pagina: number): void {
    const periodoId = this.prova()?.periodoLetivoId;
    if (periodoId === undefined) {
      return;
    }
    this.carregandoMatriculas.set(true);
    this.erroMatriculas.set(null);
    this.matriculas.listarPorPeriodo(periodoId, { pagina, tamanho: this.tamanho }).subscribe({
      next: (resposta) => {
        this.matriculasPagina.set(resposta);
        this.carregandoMatriculas.set(false);
      },
      error: (erro: unknown) => {
        this.erroMatriculas.set(erroDaApi(erro));
        this.carregandoMatriculas.set(false);
      },
    });
  }
}
