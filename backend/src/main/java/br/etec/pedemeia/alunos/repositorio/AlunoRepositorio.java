package br.etec.pedemeia.alunos.repositorio;

import br.etec.pedemeia.alunos.entidade.Aluno;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class AlunoRepositorio implements PanacheRepository<Aluno> {

    public Optional<Aluno> buscarPorMatriculaEscolar(String matriculaEscolar) {
        return find("matriculaEscolar", matriculaEscolar).firstResultOptional();
    }

    /**
     * Busca combinada: matrícula exata e/ou nome contendo o texto, sem diferenciar maiúsculas (Q18).
     * Caracteres curinga do LIKE são escapados com "!".
     */
    public io.quarkus.hibernate.orm.panache.PanacheQuery<Aluno> buscar(String matriculaEscolar, String nome) {
        StringBuilder consulta = new StringBuilder("1 = 1");
        Parameters parametros = new Parameters();
        if (matriculaEscolar != null && !matriculaEscolar.isBlank()) {
            consulta.append(" and matriculaEscolar = :matricula");
            parametros.and("matricula", matriculaEscolar.trim());
        }
        if (nome != null && !nome.isBlank()) {
            consulta.append(" and lower(nome) like :nome escape '!'");
            parametros.and("nome", "%" + escapar(nome.trim().toLowerCase()) + "%");
        }
        consulta.append(" order by nome, id");
        return find(consulta.toString(), parametros);
    }

    static String escapar(String texto) {
        return texto.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }
}
