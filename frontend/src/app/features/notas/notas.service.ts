import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from '../../core/api/api-base';
import { montarParametros } from '../../core/api/parametros';
import { ParametrosPagina, Pagina } from '../../shared/modelos/pagina';
import { NotaCorrecaoRequisicao, NotaRequisicao, NotaResposta } from '../../shared/modelos/nota';

@Injectable({ providedIn: 'root' })
export class NotasService {
  private readonly http = inject(HttpClient);

  listarPorProva(provaId: number, parametros: ParametrosPagina = {}): Observable<Pagina<NotaResposta>> {
    return this.http.get<Pagina<NotaResposta>>(`${API_BASE}/provas/${provaId}/notas`, {
      params: montarParametros({ pagina: parametros.pagina, tamanho: parametros.tamanho }),
    });
  }

  lancar(provaId: number, requisicao: NotaRequisicao): Observable<NotaResposta> {
    return this.http.post<NotaResposta>(`${API_BASE}/provas/${provaId}/notas`, requisicao);
  }

  corrigir(provaId: number, notaId: number, requisicao: NotaCorrecaoRequisicao): Observable<NotaResposta> {
    return this.http.put<NotaResposta>(`${API_BASE}/provas/${provaId}/notas/${notaId}`, requisicao);
  }
}
