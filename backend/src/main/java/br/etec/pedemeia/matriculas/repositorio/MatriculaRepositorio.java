package br.etec.pedemeia.matriculas.repositorio;

import br.etec.pedemeia.matriculas.entidade.Matricula;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MatriculaRepositorio implements PanacheRepository<Matricula> {

    public boolean existeParaAlunoNoPeriodo(long alunoId, long periodoId) {
        return count("aluno.id = ?1 and periodo.id = ?2", alunoId, periodoId) > 0;
    }

    public io.quarkus.hibernate.orm.panache.PanacheQuery<Matricula> listarPorPeriodo(long periodoId) {
        return find("periodo.id = ?1 order by aluno.nome, id", periodoId);
    }
}
