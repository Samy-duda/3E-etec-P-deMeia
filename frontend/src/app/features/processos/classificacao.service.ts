import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from '../../core/api/api-base';
import { montarParametros } from '../../core/api/parametros';
import { ClassificacaoResposta } from '../../shared/modelos/classificacao';

@Injectable({ providedIn: 'root' })
export class ClassificacaoService {
  private readonly http = inject(HttpClient);

  /** Sem classificação gerada, a API responde 200 com `calculadaEm` nulo e `itens` vazio. */
  consultar(processoId: number, anoEscolar?: number | null): Observable<ClassificacaoResposta> {
    return this.http.get<ClassificacaoResposta>(`${API_BASE}/processos-seletivos/${processoId}/classificacao`, {
      params: montarParametros({ anoEscolar }),
    });
  }

  gerar(processoId: number): Observable<ClassificacaoResposta> {
    return this.http.post<ClassificacaoResposta>(`${API_BASE}/processos-seletivos/${processoId}/classificacao`, null);
  }
}
