import { Perfil } from '../../core/auth/perfil';

export interface LoginRequisicao {
  email: string;
  senha: string;
}

export interface UsuarioResumo {
  id: number;
  nome: string;
  email: string;
  perfil: Perfil;
}

export interface LoginResposta {
  token: string;
  tipo: 'Bearer';
  expiraEm: string;
  usuario: UsuarioResumo;
}

export interface UsuarioRequisicao {
  nome: string;
  email: string;
  senha: string;
  perfil: Perfil;
}

export interface UsuarioResposta {
  id: number;
  nome: string;
  email: string;
  perfil: Perfil;
  ativo: boolean;
  criadoEm: string;
}
