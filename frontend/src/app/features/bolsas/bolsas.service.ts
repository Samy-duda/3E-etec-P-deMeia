import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from '../../core/api/api-base';
import { BolsaResposta, ConcessaoBolsaRequisicao } from '../../shared/modelos/bolsa';

/**
 * Bolsas do processo. No MVP não há cancelamento nem alteração de situação (MVP-CA30):
 * este serviço não oferece nenhum método para isso.
 */
@Injectable({ providedIn: 'root' })
export class BolsasService {
  private readonly http = inject(HttpClient);

  listarPorProcesso(processoId: number): Observable<BolsaResposta[]> {
    return this.http.get<BolsaResposta[]>(`${API_BASE}/processos-seletivos/${processoId}/bolsas`);
  }

  /**
   * Sem `matriculaId`, concede em lote a todos os selecionados pendentes.
   * Com `matriculaId`, concede só a esse aluno.
   */
  conceder(processoId: number, requisicao: ConcessaoBolsaRequisicao = {}): Observable<BolsaResposta[]> {
    return this.http.post<BolsaResposta[]>(`${API_BASE}/processos-seletivos/${processoId}/bolsas`, requisicao);
  }
}
