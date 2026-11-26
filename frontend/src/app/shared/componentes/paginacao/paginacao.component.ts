import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';

/** Controles de página. Página base zero, como no contrato. */
@Component({
  selector: 'app-paginacao',
  templateUrl: './paginacao.component.html',
  styleUrl: './paginacao.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaginacaoComponent {
  readonly pagina = input.required<number>();
  readonly totalPaginas = input.required<number>();
  readonly mudarPagina = output<number>();

  readonly temAnterior = computed(() => this.pagina() > 0);
  readonly temProxima = computed(() => this.pagina() + 1 < this.totalPaginas());

  anterior(): void {
    if (this.temAnterior()) {
      this.mudarPagina.emit(this.pagina() - 1);
    }
  }

  proxima(): void {
    if (this.temProxima()) {
      this.mudarPagina.emit(this.pagina() + 1);
    }
  }
}
