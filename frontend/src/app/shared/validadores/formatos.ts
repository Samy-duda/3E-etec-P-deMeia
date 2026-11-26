import { AbstractControl, ValidationErrors } from '@angular/forms';

/**
 * Valida só o formato numérico de nota e valor: no máximo duas casas decimais (contrato, multipleOf 0.01).
 * Regra de negócio (intervalo da nota, valor da bolsa) fica com a API.
 */
export function ateDuasCasasDecimais(controle: AbstractControl): ValidationErrors | null {
  const valor: unknown = controle.value;
  if (typeof valor !== 'number' || Number.isNaN(valor)) {
    return null;
  }
  const centesimos = valor * 100;
  return Math.abs(centesimos - Math.round(centesimos)) < 1e-8 ? null : { duasCasasDecimais: true };
}
