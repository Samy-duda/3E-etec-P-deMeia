import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ErroExibicao, erroDaApi } from '../../core/erros/mensagens-erro';
import { MensagemErroComponent } from '../../shared/componentes/mensagem-erro/mensagem-erro.component';
import { PaginacaoComponent } from '../../shared/componentes/paginacao/paginacao.component';
import { AlunoResposta } from '../../shared/modelos/aluno';
import { Pagina } from '../../shared/modelos/pagina';
import { formatarData } from '../../shared/formatacao/formatacao';
import { AlunosService } from './alunos.service';

@Component({
  selector: 'app-alunos-lista',
  imports: [ReactiveFormsModule, RouterLink, MensagemErroComponent, PaginacaoComponent],
  templateUrl: './alunos-lista.component.html',
  styleUrl: './alunos-lista.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlunosListaComponent {
  private readonly fb = inject(FormBuilder);
  private readonly alunos = inject(AlunosService);

  readonly tamanho = 20;
  readonly formatarData = formatarData;

  readonly filtroForm = this.fb.nonNullable.group({
    matriculaEscolar: [''],
    nome: [''],
  });

  readonly carregando = signal(false);
  readonly erro = signal<ErroExibicao | null>(null);
  readonly resultado = signal<Pagina<AlunoResposta> | null>(null);

  constructor() {
    this.buscar(0);
  }

  pesquisar(): void {
    this.buscar(0);
  }

  mudarPagina(pagina: number): void {
    this.buscar(pagina);
  }

  private buscar(pagina: number): void {
    const { matriculaEscolar, nome } = this.filtroForm.getRawValue();
    this.carregando.set(true);
    this.erro.set(null);
    this.alunos.listar({ matriculaEscolar, nome, pagina, tamanho: this.tamanho }).subscribe({
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
