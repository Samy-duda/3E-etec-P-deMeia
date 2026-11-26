import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ErroExibicao, erroDaApi } from '../../core/erros/mensagens-erro';
import { MensagemErroComponent } from '../../shared/componentes/mensagem-erro/mensagem-erro.component';
import { AlunoRequisicao } from '../../shared/modelos/aluno';
import { AlunosService } from './alunos.service';

@Component({
  selector: 'app-aluno-form',
  imports: [ReactiveFormsModule, RouterLink, MensagemErroComponent],
  templateUrl: './aluno-form.component.html',
  styleUrl: './aluno-form.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlunoFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly alunos = inject(AlunosService);

  /** Obrigatórios: nome, matrícula escolar e data de nascimento (MVP-RNF02). Contatos são opcionais. */
  readonly form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    matriculaEscolar: ['', [Validators.required, Validators.maxLength(30)]],
    dataNascimento: ['', [Validators.required]],
    email: ['', [Validators.maxLength(255)]],
    telefone: ['', [Validators.maxLength(20)]],
  });

  readonly enviando = signal(false);
  readonly erro = signal<ErroExibicao | null>(null);
  readonly sucesso = signal<string | null>(null);

  salvar(): void {
    this.sucesso.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const valores = this.form.getRawValue();
    const requisicao: AlunoRequisicao = {
      nome: valores.nome.trim(),
      matriculaEscolar: valores.matriculaEscolar.trim(),
      dataNascimento: valores.dataNascimento,
      email: valores.email.trim() || null,
      telefone: valores.telefone.trim() || null,
    };
    this.enviando.set(true);
    this.erro.set(null);
    this.alunos.cadastrar(requisicao).subscribe({
      next: (aluno) => {
        this.enviando.set(false);
        this.sucesso.set(`Aluno ${aluno.nome} cadastrado com a matrícula ${aluno.matriculaEscolar}.`);
        this.form.reset();
      },
      error: (erro: unknown) => {
        this.enviando.set(false);
        this.erro.set(erroDaApi(erro));
      },
    });
  }
}
