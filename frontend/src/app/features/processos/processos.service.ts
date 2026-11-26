import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from '../../core/api/api-base';
import { montarParametros } from '../../core/api/parametros';
import { ParametrosPagina, Pagina } from '../../shared/modelos/pagina';
import { ProcessoSeletivoRequisicao, ProcessoSeletivoResposta } from '../../shared/modelos/processo-seletivo';

@Injectable({ providedIn: 'root' })
export class ProcessosService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_BASE}/processos-seletivos`;

  listar(parametros: ParametrosPagina & { periodoLetivoId?: number | null } = {}): Observable<Pagina<ProcessoSeletivoResposta>> {
    return this.http.get<Pagina<ProcessoSeletivoResposta>>(this.url, {
      params: montarParametros({
        pagina: parametros.pagina,
        tamanho: parametros.tamanho,
        periodoLetivoId: parametros.periodoLetivoId,
      }),
    });
  }

  consultar(processoId: number): Observable<ProcessoSeletivoResposta> {
    return this.http.get<ProcessoSeletivoResposta>(`${this.url}/${processoId}`);
  }

  criar(requisicao: ProcessoSeletivoRequisicao): Observable<ProcessoSeletivoResposta> {
    return this.http.post<ProcessoSeletivoResposta>(this.url, requisicao);
  }
}
