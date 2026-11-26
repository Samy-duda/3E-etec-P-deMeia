import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from '../../core/api/api-base';
import { PeriodoLetivoRequisicao, PeriodoLetivoResposta } from '../../shared/modelos/periodo-letivo';

@Injectable({ providedIn: 'root' })
export class PeriodosService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_BASE}/periodos-letivos`;

  /** Lista não paginada no contrato (quantidade pequena). */
  listar(): Observable<PeriodoLetivoResposta[]> {
    return this.http.get<PeriodoLetivoResposta[]>(this.url);
  }

  cadastrar(requisicao: PeriodoLetivoRequisicao): Observable<PeriodoLetivoResposta> {
    return this.http.post<PeriodoLetivoResposta>(this.url, requisicao);
  }
}
