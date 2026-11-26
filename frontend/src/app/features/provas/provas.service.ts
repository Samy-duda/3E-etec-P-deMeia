import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from '../../core/api/api-base';
import { montarParametros } from '../../core/api/parametros';
import { ParametrosPagina, Pagina } from '../../shared/modelos/pagina';
import { ProvaRequisicao, ProvaResposta } from '../../shared/modelos/prova';

@Injectable({ providedIn: 'root' })
export class ProvasService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_BASE}/provas`;

  listar(parametros: ParametrosPagina & { periodoLetivoId?: number | null } = {}): Observable<Pagina<ProvaResposta>> {
    return this.http.get<Pagina<ProvaResposta>>(this.url, {
      params: montarParametros({
        pagina: parametros.pagina,
        tamanho: parametros.tamanho,
        periodoLetivoId: parametros.periodoLetivoId,
      }),
    });
  }

  consultar(provaId: number): Observable<ProvaResposta> {
    return this.http.get<ProvaResposta>(`${this.url}/${provaId}`);
  }

  cadastrar(requisicao: ProvaRequisicao): Observable<ProvaResposta> {
    return this.http.post<ProvaResposta>(this.url, requisicao);
  }
}
