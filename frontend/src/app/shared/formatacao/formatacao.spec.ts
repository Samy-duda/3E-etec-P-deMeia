import { FormControl } from '@angular/forms';
import { formatarData, formatarMoeda, formatarNota } from './formatacao';
import { ateDuasCasasDecimais } from '../validadores/formatos';

describe('formatação', () => {
  it('formata moeda no padrão brasileiro', () => {
    expect(formatarMoeda(300)).toBe('R$ 300,00');
    expect(formatarMoeda(null)).toBe('');
  });

  it('formata nota com duas casas e vírgula', () => {
    expect(formatarNota(7.5)).toBe('7,50');
    expect(formatarNota(10)).toBe('10,00');
  });

  it('converte yyyy-MM-dd em dd/MM/yyyy e preserva texto fora do formato', () => {
    expect(formatarData('2008-03-15')).toBe('15/03/2008');
    expect(formatarData('sem data')).toBe('sem data');
    expect(formatarData(null)).toBe('');
  });
});

describe('ateDuasCasasDecimais (formato da nota)', () => {
  it('aceita até duas casas decimais', () => {
    expect(ateDuasCasasDecimais(new FormControl(7.5))).toBeNull();
    expect(ateDuasCasasDecimais(new FormControl(0.01))).toBeNull();
  });

  it('recusa três casas decimais', () => {
    expect(ateDuasCasasDecimais(new FormControl(7.125))).toEqual({ duasCasasDecimais: true });
  });

  it('não valida valor vazio (o required cuida disso)', () => {
    expect(ateDuasCasasDecimais(new FormControl(null))).toBeNull();
  });
});
