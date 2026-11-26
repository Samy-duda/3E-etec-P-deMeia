package br.etec.pedemeia.usuarios;

import br.etec.pedemeia.usuarios.dto.UsuarioResposta;
import br.etec.pedemeia.usuarios.entidade.Usuario;

public final class Mapeador {

    private Mapeador() {
    }

    public static UsuarioResposta paraResposta(Usuario usuario) {
        return new UsuarioResposta(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil(),
                usuario.isAtivo(), usuario.getCriadoEm());
    }
}
