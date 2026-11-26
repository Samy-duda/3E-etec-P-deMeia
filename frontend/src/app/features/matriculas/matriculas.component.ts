import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AlunosService } from '../alunos/alunos.service';
import { ErroExibicao, erroDaApi } from '../../core/erros/mensagens-erro';
import { MensagemErroComponent } from '../../shared/componentes/mensagem-erro/mensagem-erro.component';
import { PaginacaoComponent } from '../../shared/componentes/paginacao/paginacao.component';
import { AlunoResposta } from '../../shared/modelos/aluno';
import { MatriculaResposta } from '../../shared/modelos/matricula';
import { Pagina } from '../../shared/modelos/pagina';
import { MatriculasService } from './matriculas.service';

@Component({
  selector: 'app-matriculas',
  imports: [ReactiveFormsModule, RouterLink, MensagemErroComponent, PaginacaoComponent],
  templateUrl: './matriculas.component.html',
  styleUrl: './matriculas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MatriculasComponent {
  private readonly fb = inject(FormBuilder);
  private readonly rota = inject(ActivatedRoute);
  private readonly matriculas = inject(MatriculasService);
  private readonly alunos = inject(AlunosService);

  readonly periodoId = Number(this.rota.snapshot.paramMap.get('periodoId'));
  readonly tamanho = 20;
  /** Ano escolar válido: 1, 2 ou 3 (P05). A API confere de novo. */
  readonly anosEscolares = [1, 2, 3];

  readonly carregando = signal(false);
  readonly erro = signal<ErroExibicao | null>(null);
  readonly resultado = signal<Pagina<MatriculaResposta> | null>(null);

  readonly buscaForm = this.fb.nonNullable.group({
    criterio: ['matricula' as 'matricula' | 'nome'],
    texto: ['', [Validators.maxLength(120)]],
  });
  readonly buscandoAluno = signal(false);
  readonly erroBusca = signal<ErroExibicao | null>(null);
  readonly resultadosBusca = signal<AlunoResposta[]>([]);
  readonly alunoEscolhido = signal<AlunoResposta | null>(null);

  readonly matriculaForm = this.fb.nonNullable.group({
    anoEscolar: [1, [Validators.required]],
    turma: ['', [Validators.maxLength(20)]],
  });
  readonly enviandoMatricula = signal(false);
  readonly erroMatricula = signal<ErroExibicao | null>(null);
  readonly sucessoMatricula = signal<string | null>(null);

  constructor() {
    this.carregar(0);
  }

  mudarPagina(pagina: number): void {
    this.carregar(pagina);
  }

  buscarAluno(): void {
    const { criterio, texto } = this.buscaForm.getRawValue();
    const termo = texto.trim();
    if (termo === '') {
      this.erroBusca.set({ mensagem: 'Informe a matrícula escolar ou o nome para buscar.', campos: [] });
      return;
    }
    this.buscandoAluno.set(true);
    this.erroBusca.set(null);
    this.alunos
      .listar({
        pagina: 0,
        tamanho: 10,
        matriculaEscolar: criterio === 'matricula' ? termo : undefined,
        nome: criterio === 'nome' ? termo : undefined,
      })
      .subscribe({
        next: (resposta) => {
          this.resultadosBusca.set(resposta.conteudo);
          this.buscandoAluno.set(false);
        },
        error: (erro: unknown) => {
          this.erroBusca.set(erroDaApi(erro));
          this.buscandoAluno.set(false);
        },
      });
  }

  escolherAluno(aluno: AlunoResposta): void {
    this.alunoEscolhido.set(aluno);
    this.sucessoMatricula.set(null);
    this.erroMatricula.set(null);
  }

  matricular(): void {
    this.sucessoMatricula.set(null);
    const aluno = this.alunoEscolhido();
    if (!aluno) {
      this.erroMatricula.set({ mensagem: 'Busque e escolha um aluno antes de matricular.', campos: [] });
      return;
    }
    if (this.matriculaForm.invalid) {
      this.matriculaForm.markAllAsTouched();
      return;
    }
    const valores = this.matriculaForm.getRawValue();
    this.enviandoMatricula.set(true);
    this.erroMatricula.set(null);
    this.matriculas
      .matricular(this.periodoId, {
        alunoId: aluno.id,
        anoEscolar: Number(valores.anoEscolar),
        turma: valores.turma.trim() || null,
      })
      .subscribe({
        next: (matricula) => {
          this.enviandoMatricula.set(false);
          this.sucessoMatricula.set(`${matricula.nomeAluno} matriculado(a) no ${matricula.anoEscolar}º ano.`);
          this.alunoEscolhido.set(null);
          this.matriculaForm.reset({ anoEscolar: 1, turma: '' });
          this.carregar(0);
        },
        error: (erro: unknown) => {
          this.enviandoMatricula.set(false);
          this.erroMatricula.set(erroDaApi(erro));
        },
      });
  }

  private carregar(pagina: number): void {
    this.carregando.set(true);
    this.erro.set(null);
    this.matriculas.listarPorPeriodo(this.periodoId, { pagina, tamanho: this.tamanho }).subscribe({
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
