import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { ErroExibicao } from '../../../core/erros/mensagens-erro';

/** Exibe um erro da API: mensagem pelo código e, em validação, a lista de campos. */
@Component({
  selector: 'app-mensagem-erro',
  templateUrl: './mensagem-erro.component.html',
  styleUrl: './mensagem-erro.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MensagemErroComponent {
  readonly erro = input<ErroExibicao | null>(null);
}
