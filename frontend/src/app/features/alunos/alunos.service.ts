import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from '../../core/api/api-base';
import { montarParametros } from '../../core/api/parametros';
import { AlunoRequisicao, AlunoResposta, FiltroAlunos } from '../../shared/modelos/aluno';
import { ParametrosPagina, Pagina } from '../../shared/modelos/pagina';

@Injectable({ providedIn: 'root' })
export class AlunosService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_BASE}/alunos`;

  listar(filtro: FiltroAlunos & ParametrosPagina): Observable<Pagina<AlunoResposta>> {
    return this.http.get<Pagina<AlunoResposta>>(this.url, {
      params: montarParametros({
        pagina: filtro.pagina,
        tamanho: filtro.tamanho,
        matriculaEscolar: filtro.matriculaEscolar,
        nome: filtro.nome,
      }),
    });
  }

  consultar(alunoId: number): Observable<AlunoResposta> {
    return this.http.get<AlunoResposta>(`${this.url}/${alunoId}`);
  }

  cadastrar(requisicao: AlunoRequisicao): Observable<AlunoResposta> {
    return this.http.post<AlunoResposta>(this.url, requisicao);
  }
}
