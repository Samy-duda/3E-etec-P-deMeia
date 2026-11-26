package br.etec.pedemeia.notas.repositorio;

import br.etec.pedemeia.notas.entidade.NotaProva;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class NotaRepositorio implements PanacheRepository<NotaProva> {

    public boolean existeParaMatriculaNaProva(long provaId, long matriculaId) {
        return count("prova.id = ?1 and matricula.id = ?2", provaId, matriculaId) > 0;
    }

    public Optional<NotaProva> buscarNaProva(long notaId, long provaId) {
        return find("id = ?1 and prova.id = ?2", notaId, provaId).firstResultOptional();
    }

    public PanacheQuery<NotaProva> listarPorProva(long provaId) {
        return find("prova.id = ?1 order by matricula.aluno.nome, id", provaId);
    }

    /** Todas as notas da prova, sem paginação (usado pela geração da classificação). */
    public java.util.List<NotaProva> listarTodasDaProva(long provaId) {
        return list("prova.id = ?1 order by id", provaId);
    }
}
