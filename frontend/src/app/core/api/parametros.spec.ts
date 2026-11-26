import { montarParametros } from './parametros';

describe('montarParametros', () => {
  it('ignora undefined, null e texto em branco', () => {
    const parametros = montarParametros({ pagina: 0, nome: '   ', matricula: undefined, turma: null });

    expect(parametros.keys()).toEqual(['pagina']);
    expect(parametros.get('pagina')).toBe('0');
  });

  it('apara espaços de texto e mantém zero como valor válido', () => {
    const parametros = montarParametros({ nome: '  Ana ', anoEscolar: 0 });

    expect(parametros.get('nome')).toBe('Ana');
    expect(parametros.get('anoEscolar')).toBe('0');
  });
});
