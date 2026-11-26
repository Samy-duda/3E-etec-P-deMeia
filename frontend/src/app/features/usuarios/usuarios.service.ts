import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from '../../core/api/api-base';
import { montarParametros } from '../../core/api/parametros';
import { ParametrosPagina, Pagina } from '../../shared/modelos/pagina';
import { UsuarioRequisicao, UsuarioResposta } from '../../shared/modelos/usuario';

@Injectable({ providedIn: 'root' })
export class UsuariosService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_BASE}/usuarios`;

  listar(parametros: ParametrosPagina = {}): Observable<Pagina<UsuarioResposta>> {
    return this.http.get<Pagina<UsuarioResposta>>(this.url, {
      params: montarParametros({ pagina: parametros.pagina, tamanho: parametros.tamanho }),
    });
  }

  criar(requisicao: UsuarioRequisicao): Observable<UsuarioResposta> {
    return this.http.post<UsuarioResposta>(this.url, requisicao);
  }
}
