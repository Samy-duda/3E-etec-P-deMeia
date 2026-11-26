package br.etec.pedemeia.usuarios.repositorio;

import br.etec.pedemeia.usuarios.entidade.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UsuarioRepositorio implements PanacheRepository<Usuario> {

    public Optional<Usuario> buscarPorEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}
