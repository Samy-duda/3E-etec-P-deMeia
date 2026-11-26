import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from '../../core/api/api-base';
import { montarParametros } from '../../core/api/parametros';
import { MatriculaRequisicao, MatriculaResposta } from '../../shared/modelos/matricula';
import { ParametrosPagina, Pagina } from '../../shared/modelos/pagina';

@Injectable({ providedIn: 'root' })
export class MatriculasService {
  private readonly http = inject(HttpClient);

  listarPorPeriodo(periodoId: number, parametros: ParametrosPagina = {}): Observable<Pagina<MatriculaResposta>> {
    return this.http.get<Pagina<MatriculaResposta>>(`${API_BASE}/periodos-letivos/${periodoId}/matriculas`, {
      params: montarParametros({ pagina: parametros.pagina, tamanho: parametros.tamanho }),
    });
  }

  matricular(periodoId: number, requisicao: MatriculaRequisicao): Observable<MatriculaResposta> {
    return this.http.post<MatriculaResposta>(`${API_BASE}/periodos-letivos/${periodoId}/matriculas`, requisicao);
  }
}
