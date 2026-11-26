import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { perfilPodeOperar } from '../../core/auth/perfil';
import { ErroExibicao, erroDaApi } from '../../core/erros/mensagens-erro';
import { MensagemErroComponent } from '../../shared/componentes/mensagem-erro/mensagem-erro.component';
import { formatarData, formatarInstante, formatarMoeda, formatarNota, rotuloSelecao } from '../../shared/formatacao/formatacao';
import { BolsaResposta } from '../../shared/modelos/bolsa';
import { ClassificacaoItem, ClassificacaoResposta } from '../../shared/modelos/classificacao';
import { ProcessoSeletivoResposta } from '../../shared/modelos/processo-seletivo';
import { BolsasService } from '../bolsas/bolsas.service';
import { ClassificacaoService } from './classificacao.service';
import { ProcessosService } from './processos.service';

/**
 * Classificação e bolsas de um processo. A API decide o que cada perfil recebe:
 * colunas de matrícula só aparecem se a resposta trouxer o campo; ações de escrita só para ADMIN e SECRETARIA.
 */
@Component({
  selector: 'app-processo-detalhe',
  imports: [RouterLink, MensagemErroComponent],
  templateUrl: './processo-detalhe.component.html',
  styleUrl: './processo-detalhe.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcessoDetalheComponent {
  private readonly auth = inject(AuthService);
  private readonly rota = inject(ActivatedRoute);
  private readonly processos = inject(ProcessosService);
  private readonly classificacoes = inject(ClassificacaoService);
  private readonly bolsas = inject(BolsasService);

  readonly processoId = Number(this.rota.snapshot.paramMap.get('processoId'));
  readonly anosEscolares = [1, 2, 3];

  readonly formatarData = formatarData;
  readonly formatarInstante = formatarInstante;
  readonly formatarMoeda = formatarMoeda;
  readonly formatarNota = formatarNota;
  readonly rotuloSelecao = rotuloSelecao;

  /** Ações de escrita (gerar classificação, conceder bolsa) só para ADMIN e SECRETARIA. */
  readonly podeOperar = computed(() => perfilPodeOperar(this.auth.perfil()));

  readonly processo = signal<ProcessoSeletivoResposta | null>(null);
  readonly carregandoProcesso = signal(true);
  readonly erroProcesso = signal<ErroExibicao | null>(null);

  readonly classificacao = signal<ClassificacaoResposta | null>(null);
  readonly classificacaoGerada = computed(() => !!this.classificacao()?.calculadaEm);
  readonly filtroAno = signal<number | null>(null);
  readonly carregandoClassificacao = signal(false);
  readonly erroClassificacao = signal<ErroExibicao | null>(null);
  readonly gerandoClassificacao = signal(false);
  readonly erroGerar = signal<ErroExibicao | null>(null);
  /** Coluna de matrícula só aparece se a API a enviou (ADMIN e SECRETARIA). */
  readonly mostrarMatricula = computed(() =>
    (this.classificacao()?.itens ?? []).some((item) => !!item.matriculaEscolar),
  );

  readonly listaBolsas = signal<BolsaResposta[]>([]);
  readonly carregandoBolsas = signal(false);
  readonly erroBolsas = signal<ErroExibicao | null>(null);
  readonly concedendo = signal(false);
  readonly erroConceder = signal<ErroExibicao | null>(null);
  readonly sucessoConceder = signal<string | null>(null);
  readonly matriculasComBolsa = computed(() => new Set(this.listaBolsas().map((bolsa) => bolsa.matriculaId)));
  readonly mostrarMatriculaBolsa = computed(() => this.listaBolsas().some((bolsa) => !!bolsa.matriculaEscolar));

  constructor() {
    this.carregarProcesso();
    this.carregarClassificacao(null);
    this.carregarBolsas();
  }

  filtrarAno(ano: number | null): void {
    this.filtroAno.set(ano);
    this.carregarClassificacao(ano);
  }

  gerarClassificacao(): void {
    this.gerandoClassificacao.set(true);
    this.erroGerar.set(null);
    this.classificacoes.gerar(this.processoId).subscribe({
      next: (resultado) => {
        this.gerandoClassificacao.set(false);
        this.filtroAno.set(null);
        this.classificacao.set(resultado);
      },
      error: (erro: unknown) => {
        this.gerandoClassificacao.set(false);
        this.erroGerar.set(erroDaApi(erro));
      },
    });
  }

  /** Sem `matriculaId`, concede em lote a todos os selecionados pendentes. */
  concederLote(): void {
    this.conceder({});
  }

  concederIndividual(item: ClassificacaoItem): void {
    this.conceder({ matriculaId: item.matriculaId });
  }

  /** Mostra o botão só para selecionado que ainda não tem bolsa. A API confere de novo. */
  podeConcederIndividual(item: ClassificacaoItem): boolean {
    return this.podeOperar() && item.selecionado && !this.matriculasComBolsa().has(item.matriculaId);
  }

  private conceder(requisicao: { matriculaId?: number }): void {
    this.concedendo.set(true);
    this.erroConceder.set(null);
    this.sucessoConceder.set(null);
    this.bolsas.conceder(this.processoId, requisicao).subscribe({
      next: (novas) => {
        this.concedendo.set(false);
        this.listaBolsas.update((atuais) => [...atuais, ...novas]);
        this.sucessoConceder.set(
          novas.length === 1 ? '1 bolsa concedida.' : `${novas.length} bolsas concedidas.`,
        );
      },
      error: (erro: unknown) => {
        this.concedendo.set(false);
        this.erroConceder.set(erroDaApi(erro));
      },
    });
  }

  private carregarProcesso(): void {
    this.carregandoProcesso.set(true);
    this.processos.consultar(this.processoId).subscribe({
      next: (processo) => {
        this.processo.set(processo);
        this.carregandoProcesso.set(false);
      },
      error: (erro: unknown) => {
        this.erroProcesso.set(erroDaApi(erro));
        this.carregandoProcesso.set(false);
      },
    });
  }

  private carregarClassificacao(anoEscolar: number | null): void {
    this.carregandoClassificacao.set(true);
    this.erroClassificacao.set(null);
    this.classificacoes.consultar(this.processoId, anoEscolar).subscribe({
      next: (resposta) => {
        this.classificacao.set(resposta);
        this.carregandoClassificacao.set(false);
      },
      error: (erro: unknown) => {
        this.erroClassificacao.set(erroDaApi(erro));
        this.carregandoClassificacao.set(false);
      },
    });
  }

  private carregarBolsas(): void {
    this.carregandoBolsas.set(true);
    this.erroBolsas.set(null);
    this.bolsas.listarPorProcesso(this.processoId).subscribe({
      next: (lista) => {
        this.listaBolsas.set(lista);
        this.carregandoBolsas.set(false);
      },
      error: (erro: unknown) => {
        this.erroBolsas.set(erroDaApi(erro));
        this.carregandoBolsas.set(false);
      },
    });
  }
}
