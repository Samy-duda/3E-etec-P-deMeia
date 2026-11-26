package br.etec.pedemeia.bolsas.repositorio;

import br.etec.pedemeia.bolsas.entidade.Bolsa;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@ApplicationScoped
public class BolsaRepositorio implements PanacheRepository<Bolsa> {

    public boolean existeParaMatriculaNoProcesso(long matriculaId, long processoId) {
        return count("matricula.id = ?1 and processo.id = ?2", matriculaId, processoId) > 0;
    }

    public Set<Long> matriculasComBolsaNoProcesso(long processoId) {
        List<Long> ids = getEntityManager()
                .createQuery("select b.matricula.id from Bolsa b where b.processo.id = :processo", Long.class)
                .setParameter("processo", processoId)
                .getResultList();
        return new HashSet<>(ids);
    }

    public List<Bolsa> listarPorProcesso(long processoId) {
        return find("select b from Bolsa b join fetch b.matricula m join fetch m.aluno "
                + "where b.processo.id = ?1 order by b.id", processoId).list();
    }
}
