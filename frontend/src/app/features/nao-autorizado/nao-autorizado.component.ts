import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-nao-autorizado',
  imports: [RouterLink],
  templateUrl: './nao-autorizado.component.html',
  styleUrl: './nao-autorizado.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NaoAutorizadoComponent {}
