import { HttpParams } from '@angular/common/http';

export type ValorParametro = string | number | boolean | null | undefined;

/**
 * Monta os query params ignorando valores vazios (undefined, null e texto em branco).
 * Texto é apenas aparado; a validação de conteúdo fica com a API.
 */
export function montarParametros(valores: Record<string, ValorParametro>): HttpParams {
  let parametros = new HttpParams();
  for (const [chave, valor] of Object.entries(valores)) {
    if (valor === undefined || valor === null) {
      continue;
    }
    const texto = typeof valor === 'string' ? valor.trim() : String(valor);
    if (texto === '') {
      continue;
    }
    parametros = parametros.set(chave, texto);
  }
  return parametros;
}
