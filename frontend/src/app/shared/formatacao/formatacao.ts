/** Formata um valor monetário no padrão brasileiro: R$ 300,00. */
export function formatarMoeda(valor: number | null | undefined): string {
  if (valor === null || valor === undefined || Number.isNaN(valor)) {
    return '';
  }
  return `R$ ${valor.toFixed(2).replace('.', ',')}`;
}

/** Formata nota com duas casas decimais e vírgula: 7,50. */
export function formatarNota(valor: number | null | undefined): string {
  if (valor === null || valor === undefined || Number.isNaN(valor)) {
    return '';
  }
  return valor.toFixed(2).replace('.', ',');
}

/** Converte yyyy-MM-dd (formato do contrato) em dd/MM/yyyy. Outros textos voltam sem alteração. */
export function formatarData(dataIso: string | null | undefined): string {
  if (!dataIso) {
    return '';
  }
  const partes = /^(\d{4})-(\d{2})-(\d{2})/.exec(dataIso);
  return partes ? `${partes[3]}/${partes[2]}/${partes[1]}` : dataIso;
}

/** Converte instante ISO 8601 (UTC) em data e hora locais no padrão brasileiro. */
export function formatarInstante(instante: string | null | undefined): string {
  if (!instante) {
    return '';
  }
  const data = new Date(instante);
  if (Number.isNaN(data.getTime())) {
    return instante;
  }
  return data.toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
}

/** Rótulo da situação do processo ou da classificação. */
export function rotuloSelecao(selecionado: boolean): string {
  return selecionado ? 'Selecionado' : 'Não selecionado';
}
