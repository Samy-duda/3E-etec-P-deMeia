/** Perfis de acesso do MVP (D02). A API é a autoridade; estas constantes só ajustam a tela. */
export const PERFIS = ['ADMIN', 'SECRETARIA', 'CONSULTA'] as const;

export type Perfil = (typeof PERFIS)[number];

/** Cadastros, provas, notas, processos seletivos e bolsas (escrita). */
export const PERFIS_OPERADORES: readonly Perfil[] = ['ADMIN', 'SECRETARIA'];

/** Somente leitura de classificações, bolsas e processos. */
export const PERFIS_TODOS: readonly Perfil[] = ['ADMIN', 'SECRETARIA', 'CONSULTA'];

/** Gestão de contas de acesso. */
export const PERFIS_ADMINISTRADOR: readonly Perfil[] = ['ADMIN'];

/** Indica se o perfil pode executar ações de escrita na tela (a API confere de novo). */
export function perfilPodeOperar(perfil: Perfil | null | undefined): boolean {
  return perfil === 'ADMIN' || perfil === 'SECRETARIA';
}
